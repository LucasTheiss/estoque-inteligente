import os
from datetime import timedelta
from flask import Flask, render_template, request, g
import click
import psycopg
from werkzeug.security import generate_password_hash
from . import db
from .domain import ValidationError
from .security import csrf_token, load_identity, seed_permissions, ROLES, PERMISSIONS


def create_app(config=None):
    app = Flask(__name__)
    app.config.update(SECRET_KEY=os.environ.get('SECRET_KEY'),
        DATABASE_URL=os.environ.get('DATABASE_URL', 'postgresql://estoque:estoque@localhost:5432/estoque'),
        SESSION_COOKIE_HTTPONLY=True, SESSION_COOKIE_SAMESITE='Lax',
        SESSION_COOKIE_SECURE=os.environ.get('COOKIE_SECURE', 'true') == 'true',
        PERMANENT_SESSION_LIFETIME=timedelta(hours=8), MAX_CONTENT_LENGTH=128 * 1024,
        MQTT_USER=os.environ.get('MQTT_USER', ''), MQTT_PASSWORD=os.environ.get('MQTT_PASSWORD', ''))
    if config:
        app.config.update(config)
    if not app.config['SECRET_KEY'] or len(app.config['SECRET_KEY']) < 32:
        raise RuntimeError('Configure SECRET_KEY com pelo menos 32 caracteres aleatórios.')
    app.teardown_appcontext(db.close_db)
    app.before_request(load_identity)
    from .web import bp
    app.register_blueprint(bp)
    from .integrations import bp as integrations
    app.register_blueprint(integrations)
    app.jinja_env.globals.update(csrf_token=csrf_token, roles=ROLES, permissions=PERMISSIONS)

    @app.after_request
    def headers(response):
        response.headers.update({'X-Content-Type-Options': 'nosniff', 'X-Frame-Options': 'DENY',
            'Referrer-Policy': 'same-origin', 'Cache-Control': 'no-store',
            'Content-Security-Policy': "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; frame-ancestors 'none'; form-action 'self'; base-uri 'self'"})
        return response

    @app.errorhandler(ValidationError)
    def invalid(error):
        db.get_db().rollback()
        return render_template('error.html', message=str(error), code=400), 400

    @app.errorhandler(psycopg.IntegrityError)
    def conflict(error):
        db.get_db().rollback()
        return render_template('error.html', message='Cadastro duplicado ou ainda vinculado a outros registros.', code=409), 409

    @app.errorhandler(403)
    @app.errorhandler(404)
    @app.errorhandler(400)
    @app.errorhandler(429)
    def http_error(error):
        return render_template('error.html', message=error.description, code=error.code), error.code

    @app.cli.command('init-db')
    def initialize():
        with db.connect(app.config['DATABASE_URL']) as connection:
            db.init_db(connection)
        click.echo('Banco inicializado.')

    @app.cli.command('create-tenant')
    @click.option('--slug', required=True)
    @click.option('--name', required=True)
    @click.option('--username', default='admin')
    @click.option('--password', prompt=True, hide_input=True, confirmation_prompt=True)
    @click.option('--iot', is_flag=True)
    @click.option('--enterprise', is_flag=True)
    @click.option('--fractional', is_flag=True)
    def create_tenant(slug, name, username, password, iot, enterprise, fractional):
        import re
        if not re.fullmatch(r'[a-z0-9-]{1,40}', slug) or len(password) < 12:
            raise click.ClickException('Slug inválido ou senha menor que 12 caracteres.')
        with db.connect(app.config['DATABASE_URL']) as connection:
            tenant = connection.execute('INSERT INTO tenants(slug,name,iot,enterprise,fractional) '
                'VALUES(%s,%s,%s,%s,%s) RETURNING id', (slug,name,iot,enterprise,fractional)).fetchone()['id']
            connection.execute('INSERT INTO users(tenant_id,username,password_hash,role) VALUES(%s,%s,%s,%s)',
                               (tenant,username,generate_password_hash(password), 'admin'))
            seed_permissions(connection, tenant)
        click.echo('Loja e administrador criados.')
    return app

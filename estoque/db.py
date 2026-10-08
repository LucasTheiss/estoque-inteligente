"""Uma conexão por requisição; o contexto psycopg faz commit ou rollback."""
from pathlib import Path
import psycopg
from psycopg.rows import dict_row
from flask import current_app, g
from psycopg.types.json import Jsonb


def connect(dsn):
    return psycopg.connect(dsn, row_factory=dict_row, connect_timeout=3)


def get_db():
    if 'db' not in g:
        g.db = connect(current_app.config['DATABASE_URL'])
    return g.db


def close_db(error=None):
    if db := g.pop('db', None):
        db.close()  # transações não confirmadas nunca são persistidas


def init_db(db):
    db.execute(Path(__file__).with_name('schema.sql').read_text(encoding='utf-8'))


def audit(db, tenant, actor, action, **detail):
    db.execute('INSERT INTO audit(tenant_id,actor_id,action,detail) VALUES(%s,%s,%s,%s)',
               (tenant, actor, action, Jsonb(detail)))

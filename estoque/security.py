import functools
import hashlib
import secrets
from flask import abort, current_app, g, redirect, request, session, url_for
from .db import get_db

ROLES = {'vendedor': 'Vendedor', 'estoquista': 'Estoquista', 'gerente': 'Gerente', 'admin': 'Administrador'}
PERMISSIONS = {'consultar': 'Consultar estoque', 'saida': 'Registrar saída', 'entrada': 'Registrar entrada',
               'cadastros': 'Produtos, posições e vínculos', 'ajuste': 'Ajustar saldo', 'estorno': 'Estornar',
               'historico': 'Histórico', 'usuarios': 'Gerenciar usuários', 'iot': 'Configurar IoT',
               'relatorios': 'Exportar e consultar ocupação', 'rbac': 'Alterar permissões'}
DEFAULTS = {
    'vendedor': {'consultar', 'saida'},
    'estoquista': {'consultar', 'saida', 'entrada', 'cadastros', 'ajuste', 'estorno', 'historico'},
    'gerente': set(PERMISSIONS) - {'iot', 'rbac'},
    'admin': set(PERMISSIONS),
}


def seed_permissions(db, tenant):
    for role, permissions in DEFAULTS.items():
        for permission in permissions:
            db.execute('INSERT INTO role_permissions VALUES(%s,%s,%s) ON CONFLICT DO NOTHING',
                       (tenant, role, permission))


def csrf_token():
    if 'csrf' not in session:
        session['csrf'] = secrets.token_urlsafe(32)
    return session['csrf']


def load_identity():
    g.user, g.tenant, g.permissions = None, None, set()
    if request.method == 'POST' and request.endpoint != 'integrations.alexa':
        if not secrets.compare_digest(session.get('csrf', ''), request.form.get('csrf', request.headers.get('X-CSRF-Token', ''))) or 'csrf' not in session:
            abort(400, 'Formulário expirado. Recarregue a página e tente novamente.')
    if session.get('user'):
        db = get_db()
        g.user = db.execute('SELECT id,tenant_id,username,role,auth_version FROM users '
                            'WHERE id=%s AND tenant_id=%s AND active',
                            (session['user'], session.get('tenant'))).fetchone()
        if not g.user or g.user['auth_version'] != session.get('version'):
            session.clear()
            g.user = None
            return
        g.tenant = db.execute('SELECT * FROM tenants WHERE id=%s', (g.user['tenant_id'],)).fetchone()
        g.permissions = {r['permission'] for r in db.execute(
            'SELECT permission FROM role_permissions WHERE tenant_id=%s AND role=%s',
            (g.tenant['id'], g.user['role']))}


def require(permission, feature=None):
    def decorate(fn):
        @functools.wraps(fn)
        def wrapped(*args, **kwargs):
            if not g.user:
                return redirect(url_for('web.login'))
            if permission not in g.permissions or (feature and not g.tenant[feature]):
                abort(403, 'Seu perfil ou plano não permite esta ação.')
            return fn(*args, **kwargs)
        return wrapped
    return decorate


def login_key(tenant, username):
    return hashlib.sha256(f'{tenant}:{username.casefold()}:{request.remote_addr}'.encode()).hexdigest()

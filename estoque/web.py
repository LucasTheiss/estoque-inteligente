import secrets
from datetime import date
from flask import Blueprint, abort, flash, g, redirect, render_template, request, session, url_for
from werkzeug.security import check_password_hash, generate_password_hash
from .db import get_db, audit
from .domain import ValidationError
from .security import require, login_key, ROLES, PERMISSIONS
from . import catalog
from .stock import EstoqueService

bp = Blueprint('web', __name__)


def render(name, **context):
    return render_template(name, request_key=secrets.token_urlsafe(24), **context)


def done(message, endpoint, **args):
    get_db().commit()
    flash(message, 'success')
    return redirect(url_for(endpoint, **args))


@bp.get('/health')
def health():
    get_db().execute('SELECT 1')
    return {'status': 'ok'}


@bp.route('/login', methods=['GET','POST'])
def login():
    db = get_db()
    if request.method == 'POST':
        slug = catalog.text(request.form,'tenant',True,40)
        username = catalog.text(request.form,'username',True)
        key = login_key(slug,username)
        db.execute('DELETE FROM login_attempts WHERE expires_at<now()')
        attempt = db.execute('''INSERT INTO login_attempts(key,attempts) VALUES(%s,1)
            ON CONFLICT(key) DO UPDATE SET attempts=login_attempts.attempts+1 RETURNING attempts''',(key,)).fetchone()
        db.commit()
        if attempt['attempts'] > 10:
            abort(429,'Muitas tentativas. Aguarde 15 minutos antes de tentar novamente.')
        user = db.execute('SELECT u.* FROM users u JOIN tenants t ON t.id=u.tenant_id '
                          'WHERE t.slug=%s AND u.username=%s AND u.active',(slug,username)).fetchone()
        # Mesmo custo de hashing para usuário inexistente (mitiga enumeração por tempo).
        password_hash = user['password_hash'] if user else bp_dummy_hash
        valid = check_password_hash(password_hash, request.form.get('password',''))
        if not user or not valid:
            flash('Loja, usuário ou senha inválidos.', 'error')
        else:
            db.execute('DELETE FROM login_attempts WHERE key=%s',(key,))
            audit(db,user['tenant_id'],user['id'],'login')
            db.commit()
            session.clear()
            session.update(user=user['id'],tenant=user['tenant_id'],version=user['auth_version'],
                           theme='light' if request.form.get('theme')=='light' else 'dark')
            session.permanent=True
            return redirect(url_for('web.dashboard'))
    return render('login.html', tenants=db.execute('SELECT slug,name FROM tenants ORDER BY name').fetchall())


bp_dummy_hash = generate_password_hash(secrets.token_urlsafe(32))


@bp.post('/logout')
def logout():
    session.clear()
    return redirect(url_for('web.login'))


@bp.get('/')
@require('consultar')
def dashboard():
    page = catalog.integer(request.args.get('page',1),1,10000)
    return render('dashboard.html', products=catalog.products(get_db(),g.tenant['id'],request.args.get('q',''),page),
                  locations=catalog.locations(get_db(),g.tenant['id']), page=page)


@bp.route('/produtos/novo',methods=['GET','POST'])
@bp.route('/produtos/<int:id>',methods=['GET','POST'])
@require('cadastros')
def product_form(id=None):
    db=get_db()
    if request.method=='POST':
        ids=catalog.save_product(db,g.tenant,g.user['id'],request.form,id)
        position=request.form.get('position_id')
        if position and not id:
            for pid in ids:
                link(pid,catalog.integer(position,1))
                amount=request.form.get('quantity_'+str(catalog.product(db,g.tenant['id'],pid)['size']), request.form.get('quantity','0'))
                if catalog.decimal(amount)>0:
                    EstoqueService(db,g.tenant['id'],g.user['id']).movimentar(pid,int(position),'entrada',amount,
                        'Estoque inicial',request.form['request_key']+':'+str(pid))
        return done('Produto salvo.','web.dashboard')
    return render('product.html', item=catalog.product(db,g.tenant['id'],id) if id else {},
                  positions=db.execute('SELECT * FROM positions WHERE tenant_id=%s AND active ORDER BY code',(g.tenant['id'],)).fetchall())


@bp.post('/produtos/<int:id>/excluir')
@require('cadastros')
def product_delete(id):
    catalog.archive(get_db(),g.tenant['id'],g.user['id'],'produto',id)
    return done('Produto excluído; histórico preservado.','web.dashboard')


@bp.route('/posicoes',methods=['GET','POST'])
@bp.route('/posicoes/<int:id>',methods=['GET','POST'])
@require('cadastros')
def positions(id=None):
    db=get_db()
    if request.method=='POST':
        catalog.save_position(db,g.tenant,g.user['id'],request.form,id)
        return done('Posição salva.','web.positions')
    rows=db.execute('SELECT * FROM positions WHERE tenant_id=%s AND active ORDER BY sector,row_no,col_no,code',(g.tenant['id'],)).fetchall()
    item=next((r for r in rows if r['id']==id),{})
    if id and not item: abort(404)
    return render('positions.html', rows=rows,item=item)


@bp.post('/posicoes/<int:id>/excluir')
@require('cadastros')
def position_delete(id):
    catalog.archive(get_db(),g.tenant['id'],g.user['id'],'posicao',id)
    return done('Posição excluída; histórico preservado.','web.positions')


def link(product,position):
    db=get_db()
    pair=db.execute('''SELECT p.id FROM products p JOIN positions s ON s.tenant_id=p.tenant_id
        WHERE p.tenant_id=%s AND p.id=%s AND s.id=%s AND p.active AND s.active AND p.unit=s.unit
        FOR SHARE OF p,s''',(g.tenant['id'],product,position)).fetchone()
    if not pair: raise ValidationError('Produto e posição devem existir na loja e usar a mesma unidade.')
    db.execute('INSERT INTO occupancy(tenant_id,product_id,position_id) VALUES(%s,%s,%s)',(g.tenant['id'],product,position))
    audit(db,g.tenant['id'],g.user['id'],'vinculo.criar',product=product,position=position)


@bp.route('/vinculos',methods=['GET','POST'])
@require('cadastros')
def links():
    db=get_db()
    if request.method=='POST':
        product=catalog.integer(request.form.get('product_id'),1)
        position=catalog.integer(request.form.get('position_id'),1)
        if request.form.get('action')=='delete':
            result=db.execute('DELETE FROM occupancy WHERE tenant_id=%s AND product_id=%s AND position_id=%s '
                              'AND quantity=0 RETURNING product_id',(g.tenant['id'],product,position)).fetchone()
            if not result: raise ValidationError('Vínculo inexistente ou com saldo. Zere o saldo antes de excluir.')
            audit(db,g.tenant['id'],g.user['id'],'vinculo.excluir',product=product,position=position)
        else:
            link(product,position)
        return done('Vínculo atualizado.','web.links')
    return render('links.html', rows=catalog.locations(db,g.tenant['id']), products=catalog.products(db,g.tenant['id']),
                  positions=db.execute('SELECT * FROM positions WHERE tenant_id=%s AND active ORDER BY code',(g.tenant['id'],)).fetchall())


@bp.route('/movimentar',methods=['GET','POST'])
@require('consultar')
def movement():
    if request.method=='POST':
        kind=request.form.get('kind')
        if kind not in ('entrada','saida','ajuste') or kind not in g.permissions: abort(403)
        EstoqueService(get_db(),g.tenant['id'],g.user['id']).movimentar(
            catalog.integer(request.form.get('product_id'),1),catalog.integer(request.form.get('position_id'),1),
            kind,request.form.get('quantity'),catalog.text(request.form,'reason',True,1000),request.form.get('request_key'))
        return done('Movimentação registrada.','web.dashboard')
    return render('movement.html', rows=catalog.locations(get_db(),g.tenant['id']))


@bp.get('/baixa-rapida')
@require('saida')
def quick():
    rows=catalog.locations(get_db(),g.tenant['id'])
    code=request.args.get('code','').strip()
    if code:
        ids={r['id'] for r in get_db().execute('SELECT id FROM products WHERE tenant_id=%s AND active AND (sku=%s OR barcode=%s)',
                                             (g.tenant['id'],code,code))}
        rows=[r for r in rows if r['product_id'] in ids]
    return render('quick.html',rows=rows,code=code)


@bp.route('/estorno',methods=['GET','POST'])
@require('estorno')
def reversal():
    if request.method=='POST':
        result=EstoqueService(get_db(),g.tenant['id'],g.user['id']).estornar(
            catalog.integer(request.form.get('movement_id'),1),catalog.text(request.form,'reason',True,1000),request.form.get('request_key'))
        if request.form.get('notify'):
            audit(get_db(),g.tenant['id'],g.user['id'],'alerta.gerencia',movement=result['id'])
        return done('Estorno registrado com justificativa.','web.history')
    return render('reversal.html',rows=history_rows()[:100])


def history_rows():
    sql='''SELECT v.*,p.sku,m.name,s.code,u.username FROM movements v
        JOIN products p ON p.id=v.product_id AND p.tenant_id=v.tenant_id
        JOIN models m ON m.id=p.model_id AND m.tenant_id=p.tenant_id
        JOIN positions s ON s.id=v.position_id AND s.tenant_id=v.tenant_id
        JOIN users u ON u.id=v.actor_id AND u.tenant_id=v.tenant_id WHERE v.tenant_id=%s'''
    params=[g.tenant['id']]
    if request.args.get('kind'):
        sql+=' AND v.kind=%s'; params.append(request.args['kind'])
    for key,operator in [('from','>='),('to','<')]:
        if request.args.get(key):
            try: date.fromisoformat(request.args[key])
            except ValueError: raise ValidationError('Data inválida.') from None
            sql+=f" AND v.created_at {operator} %s::date" + ("+interval '1 day'" if key=='to' else '')
            params.append(request.args[key])
    return get_db().execute(sql+' ORDER BY v.id DESC LIMIT 5000',params).fetchall()


@bp.get('/historico')
@require('historico')
def history():
    return render('history.html',rows=history_rows(),logs=get_db().execute(
        'SELECT a.*,u.username FROM audit a LEFT JOIN users u ON u.id=a.actor_id AND u.tenant_id=a.tenant_id '
        'WHERE a.tenant_id=%s ORDER BY a.id DESC LIMIT 100',(g.tenant['id'],)).fetchall())


@bp.get('/mapa')
@require('consultar')
def map_view():
    return render('map.html',rows=position_stats(),locations=catalog.locations(get_db(),g.tenant['id']))


def position_stats():
    return get_db().execute('''SELECT s.*,COALESCE(SUM(o.quantity),0) quantity FROM positions s
        LEFT JOIN occupancy o ON o.tenant_id=s.tenant_id AND o.position_id=s.id
        WHERE s.tenant_id=%s AND s.active GROUP BY s.id ORDER BY s.sector,s.row_no,s.col_no,s.code''',(g.tenant['id'],)).fetchall()


@bp.get('/ocupacao')
@require('relatorios','enterprise')
def heatmap():
    return render('heatmap.html',rows=position_stats())


@bp.route('/usuarios',methods=['GET','POST'])
@bp.route('/usuarios/<int:id>',methods=['GET','POST'])
@require('usuarios')
def users(id=None):
    db=get_db()
    if request.method=='POST':
        # Serializa alterações de perfis para proteger o último administrador.
        db.execute('SELECT id FROM tenants WHERE id=%s FOR UPDATE',(g.tenant['id'],))
        old=db.execute('SELECT * FROM users WHERE tenant_id=%s AND id=%s AND active',(g.tenant['id'],id)).fetchone() if id else None
        role=request.form.get('role')
        if (role not in ROLES or (id and not old) or
            (g.user['role']!='admin' and (role=='admin' or (old and old['role']=='admin')))):
            abort(403)
        if id==g.user['id'] and (role!=g.user['role'] or request.form.get('action')=='delete'):
            raise ValidationError('Não remova nem altere seu próprio perfil.')
        if old and old['role']=='admin' and (role!='admin' or request.form.get('action')=='delete'):
            admins=db.execute("SELECT count(*) n FROM users WHERE tenant_id=%s AND role='admin' AND active",(g.tenant['id'],)).fetchone()['n']
            if admins<=1: raise ValidationError('Mantenha ao menos um administrador ativo.')
        password=request.form.get('password','')
        if (not id or password) and not 12<=len(password)<=200:
            raise ValidationError('A senha deve ter entre 12 e 200 caracteres.')
        username=catalog.text(request.form,'username',True)
        if request.form.get('action')=='delete' and id:
            db.execute('UPDATE users SET active=false,auth_version=auth_version+1 WHERE tenant_id=%s AND id=%s',(g.tenant['id'],id))
        elif id:
            db.execute('UPDATE users SET username=%s,role=%s,password_hash=%s,auth_version=auth_version+1 WHERE tenant_id=%s AND id=%s',
                       (username,role,generate_password_hash(password) if password else old['password_hash'],g.tenant['id'],id))
        else:
            db.execute('INSERT INTO users(tenant_id,username,role,password_hash) VALUES(%s,%s,%s,%s)',
                       (g.tenant['id'],username,role,generate_password_hash(password)))
        audit(db,g.tenant['id'],g.user['id'],'usuario.'+request.form.get('action','salvar'),username=username,role=role)
        return done('Usuário atualizado.','web.users')
    rows=db.execute('SELECT id,username,role FROM users WHERE tenant_id=%s AND active ORDER BY username',(g.tenant['id'],)).fetchall()
    item=next((r for r in rows if r['id']==id),{})
    if id and not item: abort(404)
    grants={(r['role'],r['permission']) for r in db.execute('SELECT * FROM role_permissions WHERE tenant_id=%s',(g.tenant['id'],))}
    return render('users.html',rows=rows,item=item,grants=grants)


@bp.post('/permissoes')
@require('rbac')
def rbac():
    if g.user['role']!='admin': abort(403)
    db=get_db()
    for role in ROLES:
        if role=='admin': continue
        db.execute('DELETE FROM role_permissions WHERE tenant_id=%s AND role=%s',(g.tenant['id'],role))
        for permission in PERMISSIONS:
            if request.form.get(role+':'+permission):
                if permission=='rbac': continue
                db.execute('INSERT INTO role_permissions VALUES(%s,%s,%s)',(g.tenant['id'],role,permission))
    audit(db,g.tenant['id'],g.user['id'],'permissoes.editar')
    return done('Permissões atualizadas.','web.users')

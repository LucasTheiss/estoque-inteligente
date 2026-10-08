from concurrent.futures import ThreadPoolExecutor
from decimal import Decimal
import time
import pytest
import psycopg
from estoque.db import connect
from estoque.domain import ValidationError
from estoque.stock import EstoqueService
from conftest import post


def test_all_screens_and_exports(env):
    app,client,dsn=env
    for path in ('/','/produtos/novo','/produtos/1','/posicoes','/vinculos','/movimentar','/baixa-rapida',
                 '/estorno','/historico','/mapa','/ocupacao','/usuarios','/iot/configuracao','/busca-assistida','/login'):
        response=client.get(path)
        assert response.status_code==200,(path,response.data)
    assert client.get('/historico/exportar/pdf').data.startswith(b'%PDF')
    assert client.get('/historico/exportar/csv').status_code==200
    assert 'Content-Security-Policy' in client.get('/').headers


def test_auth_csrf_and_session_revocation(env):
    app,client,dsn=env
    anonymous=app.test_client()
    assert anonymous.get('/').status_code==302
    assert client.post('/movimentar',data={}).status_code==400
    anonymous.get('/login')
    with anonymous.session_transaction() as s: token=s['csrf']
    assert anonymous.post('/login',data={'csrf':token,'tenant':'loja-1','username':'admin','password':'wrong'}).status_code==200
    assert anonymous.post('/login',data={'csrf':token,'tenant':'loja-1','username':'admin','password':'senha-testes-123'}).status_code==302
    assert anonymous.get('/').status_code==200
    with connect(dsn) as db: db.execute('UPDATE users SET auth_version=2 WHERE id=1')
    assert anonymous.get('/').status_code==302


def test_tenant_isolation_and_features(env):
    app,client,dsn=env
    assert client.get('/produtos/2').status_code==400
    assert post(client,'/movimentar',product_id=2,position_id=2,kind='saida',quantity=1,reason='x',request_key='cross').status_code==400
    assert post(client,'/vinculos',product_id=1,position_id=2).status_code==400
    with connect(dsn) as db:
        db.execute('UPDATE tenants SET enterprise=false,iot=false,fractional=false WHERE id=1')
    for path in ('/ocupacao','/historico/exportar/pdf','/iot/configuracao','/busca-assistida'):
        assert client.get(path).status_code==403,path
    with connect(dsn) as db: db.execute("UPDATE users SET role='vendedor' WHERE id=1")
    for path in ('/usuarios','/posicoes','/produtos/novo','/estorno'):
        assert client.get(path).status_code==403,path
    assert post(client,'/movimentar',product_id=1,position_id=1,kind='ajuste',quantity=0,reason='x',request_key='forbid').status_code==403


def test_catalog_crud_variants_and_links(env):
    app,client,dsn=env
    response=post(client,'/produtos/novo',name='Tênis Grade',brand='X',sku='GRADE',unit='un',price='10.00',structure='grade',sizes='38,39,40',attributes='{"material":"couro"}')
    assert response.status_code==302
    with connect(dsn) as db:
        products=db.execute("SELECT * FROM products WHERE tenant_id=1 AND sku LIKE 'GRADE-%' ORDER BY id").fetchall()
        assert len(products)==3 and len({p['model_id'] for p in products})==1
        pid=products[0]['id']
    assert post(client,'/posicoes',code='A1',sector='A',capacity='10',unit='un').status_code==302
    with connect(dsn) as db: place=db.execute("SELECT id FROM positions WHERE tenant_id=1 AND code='A1'").fetchone()['id']
    assert post(client,'/vinculos',product_id=pid,position_id=place).status_code==302
    assert post(client,'/movimentar',product_id=pid,position_id=place,kind='entrada',quantity='5',reason='Recebimento',request_key='new-stock').status_code==302
    assert post(client,f'/produtos/{pid}/excluir').status_code==400
    assert post(client,'/movimentar',product_id=pid,position_id=place,kind='ajuste',quantity='0',reason='Contagem',request_key='zero').status_code==302
    assert post(client,'/vinculos',product_id=pid,position_id=place,action='delete').status_code==302
    assert post(client,f'/produtos/{pid}/excluir').status_code==302
    assert post(client,f'/posicoes/{place}/excluir').status_code==302
    assert b'GRADE-39' in client.get('/?q=couro').data


def test_idempotency_reversal_and_stock_constraints(env):
    _,client,dsn=env
    data=dict(product_id=1,position_id=1,kind='saida',quantity='2',reason='Venda',request_key='same-key')
    assert post(client,'/movimentar',**data).status_code==302
    assert post(client,'/movimentar',**data).status_code==302
    assert post(client,'/movimentar',**{**data,'quantity':'3'}).status_code==400
    with connect(dsn) as db:
        assert db.execute('SELECT quantity FROM occupancy WHERE tenant_id=1').fetchone()['quantity']==18
        original=db.execute('SELECT id FROM movements WHERE tenant_id=1').fetchone()['id']
    assert post(client,'/estorno',movement_id=original,reason='Venda cancelada',request_key='reverse').status_code==302
    assert post(client,'/estorno',movement_id=original,reason='Venda cancelada',request_key='reverse').status_code==302
    assert post(client,'/estorno',movement_id=original,reason='Outra',request_key='reverse2').status_code==400
    with connect(dsn) as db:
        assert db.execute('SELECT quantity FROM occupancy WHERE tenant_id=1').fetchone()['quantity']==20
        assert db.execute('SELECT count(*) n FROM movements WHERE tenant_id=1').fetchone()['n']==2


def test_atomicity_on_audit_failure(env):
    _,_,dsn=env
    with connect(dsn) as db:
        db.execute("CREATE FUNCTION fail_audit() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN RAISE EXCEPTION 'test failure'; END $$")
        db.execute('CREATE TRIGGER fail BEFORE INSERT ON audit FOR EACH ROW EXECUTE FUNCTION fail_audit()')
    with pytest.raises(psycopg.Error):
        with connect(dsn) as db: EstoqueService(db,1,1).movimentar(1,1,'saida','1','Falha forçada','fail')
    with connect(dsn) as db:
        assert db.execute('SELECT quantity FROM occupancy WHERE tenant_id=1').fetchone()['quantity']==20
        assert db.execute('SELECT count(*) n FROM movements').fetchone()['n']==0


def test_concurrent_withdrawals_and_retries(env):
    _,_,dsn=env
    def withdraw(key):
        try:
            with connect(dsn) as db:
                return EstoqueService(db,1,1).movimentar(1,1,'saida','1','Concorrência',key)['id']
        except ValidationError:
            return None
    with ThreadPoolExecutor(max_workers=8) as pool:
        repeated=list(pool.map(withdraw,['one']*8))
        assert len(set(repeated))==1
        results=list(pool.map(withdraw,[f'call-{i}' for i in range(30)]))
    assert sum(r is not None for r in results)==19
    with connect(dsn) as db:
        assert db.execute('SELECT quantity FROM occupancy WHERE tenant_id=1').fetchone()['quantity']==0
        assert db.execute('SELECT count(*) n FROM movements WHERE tenant_id=1').fetchone()['n']==20


def test_users_rbac_audit_and_passwords(env):
    _,client,dsn=env
    assert post(client,'/usuarios',username='vendedor',password='senha-testes-456',role='vendedor').status_code==302
    with connect(dsn) as db:
        user=db.execute("SELECT * FROM users WHERE username='vendedor'").fetchone()
        assert user['password_hash'].startswith('scrypt:')
    assert post(client,f"/usuarios/{user['id']}",username='vendedor',password='',role='estoquista').status_code==302
    assert post(client,f"/usuarios/{user['id']}",username='vendedor',password='',role='estoquista',action='delete').status_code==302
    assert post(client,'/usuarios/1',username='admin',password='',role='admin',action='delete').status_code==400


def test_fractional_stock(env):
    _,client,dsn=env
    assert post(client,'/produtos/novo',name='Maçã',sku='MACA',unit='kg',price='9.90').status_code==302
    assert post(client,'/posicoes',code='PESO',sector='Granel',capacity='30.000',unit='kg').status_code==302
    with connect(dsn) as db:
        pid=db.execute("SELECT id FROM products WHERE sku='MACA'").fetchone()['id']
        pos=db.execute("SELECT id FROM positions WHERE code='PESO'").fetchone()['id']
    assert post(client,'/vinculos',product_id=pid,position_id=pos).status_code==302
    for key,kind,qty in [('in','entrada','2.500'),('out','saida','0.125')]:
        assert post(client,'/movimentar',product_id=pid,position_id=pos,kind=kind,quantity=qty,reason='Peso',request_key=key).status_code==302
    with connect(dsn) as db:
        assert db.execute('SELECT quantity FROM occupancy WHERE product_id=%s',(pid,)).fetchone()['quantity']==Decimal('2.375')

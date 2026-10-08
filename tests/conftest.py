import os
import uuid
import pytest
import psycopg
from psycopg.conninfo import make_conninfo
from werkzeug.security import generate_password_hash
from estoque import create_app
from estoque.db import connect, init_db
from estoque.security import seed_permissions


@pytest.fixture
def env():
    dsn=os.environ.get('TEST_DATABASE_URL')
    if not dsn:
        pytest.skip('Defina TEST_DATABASE_URL para executar contra PostgreSQL real.')
    schema='test_'+uuid.uuid4().hex
    with connect(dsn) as db:
        db.execute(f'CREATE SCHEMA {schema}')
    scoped=make_conninfo(dsn,options=f'-c search_path={schema},public')
    app=create_app({'SECRET_KEY':'test-only-'*8,'DATABASE_URL':scoped,'TESTING':True,'SESSION_COOKIE_SECURE':False})
    with connect(scoped) as db:
        init_db(db)
        for tenant in (1,2):
            db.execute('INSERT INTO tenants(id,slug,name,iot,enterprise,fractional) VALUES(%s,%s,%s,true,true,true)',
                       (tenant,f'loja-{tenant}',f'Loja {tenant}'))
            db.execute("INSERT INTO users(id,tenant_id,username,password_hash,role) VALUES(%s,%s,'admin',%s,'admin')",
                       (tenant,tenant,generate_password_hash('senha-testes-123')))
            seed_permissions(db,tenant)
            db.execute("INSERT INTO models(id,tenant_id,name,brand,category) VALUES(%s,%s,'Tênis Modelo','Marca','Calçados')",(tenant,tenant))
            db.execute("INSERT INTO products(id,tenant_id,model_id,sku,unit,price) VALUES(%s,%s,%s,%s,'un',100)",(tenant,tenant,tenant,f'SKU-{tenant}'))
            db.execute("INSERT INTO positions(id,tenant_id,code,sector,capacity,unit) VALUES(%s,%s,'B4','B',30,'un')",(tenant,tenant))
            db.execute('INSERT INTO occupancy VALUES(%s,%s,%s,20)',(tenant,tenant,tenant))
        for table in ('tenants','users','models','products','positions'):
            db.execute(f"SELECT setval(pg_get_serial_sequence('{table}','id'),(SELECT MAX(id) FROM {table}))")
    client=app.test_client()
    with client.session_transaction() as session:
        session.update(user=1,tenant=1,version=1,csrf='test-csrf')
    yield app,client,scoped
    for entry in app.extensions.get('mqtt',{}).values(): entry[1].close()
    with connect(dsn) as db:
        db.execute(f'DROP SCHEMA {schema} CASCADE')


def post(client,path,**data):
    return client.post(path,data={'csrf':'test-csrf',**data})

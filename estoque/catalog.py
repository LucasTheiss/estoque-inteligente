import re
from psycopg.types.json import Jsonb
from .db import audit
from .domain import decimal, ValidationError


def text(data, key, required=False, limit=120):
    value = str(data.get(key, '')).strip()
    if len(value) > limit or (required and not value):
        raise ValidationError(f'Preencha {key} com até {limit} caracteres.')
    return value


def integer(value, minimum=0, maximum=1000000):
    try:
        result = int(value)
    except (ValueError, TypeError):
        raise ValidationError('Informe um número inteiro válido.') from None
    if not minimum <= result <= maximum:
        raise ValidationError(f'O número deve estar entre {minimum} e {maximum}.')
    return result


PRODUCT_QUERY = '''SELECT p.*,m.name,m.brand,m.category,
    COALESCE((SELECT SUM(o.quantity) FROM occupancy o WHERE o.tenant_id=p.tenant_id AND o.product_id=p.id),0) AS quantity
    FROM products p JOIN models m ON m.id=p.model_id AND m.tenant_id=p.tenant_id
    WHERE p.tenant_id=%s AND p.active'''


def products(db, tenant, query='', page=1):
    sql, params = PRODUCT_QUERY, [tenant]
    if len(query) > 200:
        raise ValidationError('A busca deve ter até 200 caracteres.')
    for word in query.split():
        sql += ''' AND (concat_ws(' ',p.sku,p.barcode,p.color,p.size,p.unit,p.price,p.attributes::text,
            m.name,m.brand,m.category,(SELECT SUM(o.quantity)::text FROM occupancy o
            WHERE o.tenant_id=p.tenant_id AND o.product_id=p.id)) ILIKE %s
            OR EXISTS(SELECT 1 FROM occupancy o JOIN positions s ON s.id=o.position_id AND s.tenant_id=o.tenant_id
            WHERE o.tenant_id=p.tenant_id AND o.product_id=p.id AND concat_ws(' ',s.code,s.sector) ILIKE %s))'''
        word = word.replace('\\', '\\\\').replace('%', '\\%').replace('_', '\\_')
        params.extend([f'%{word}%'] * 2)
    return db.execute(sql + ' ORDER BY m.name,p.sku LIMIT 100 OFFSET %s', (*params, (page-1)*100)).fetchall()


def product(db, tenant, id):
    item = db.execute(PRODUCT_QUERY + ' AND p.id=%s', (tenant, id)).fetchone()
    if not item:
        raise ValidationError('Produto não encontrado nesta loja.')
    return item


def locations(db, tenant, product_id=None):
    sql = '''SELECT o.*,s.code,s.sector,s.unit,s.device,s.pin,p.sku,m.name,p.size,p.color
        FROM occupancy o JOIN positions s ON s.tenant_id=o.tenant_id AND s.id=o.position_id
        JOIN products p ON p.tenant_id=o.tenant_id AND p.id=o.product_id
        JOIN models m ON m.tenant_id=p.tenant_id AND m.id=p.model_id
        WHERE o.tenant_id=%s AND p.active AND s.active'''
    return db.execute(sql + (' AND o.product_id=%s' if product_id else '') + ' ORDER BY s.code,p.sku',
                      (tenant, product_id) if product_id else (tenant,)).fetchall()


def save_product(db, tenant, actor, data, id=None):
    name, sku = text(data, 'name', True), text(data, 'sku', True)
    unit = data.get('unit', 'un')
    if unit not in ('kg', 'un') or (unit == 'kg' and not tenant['fractional']):
        raise ValidationError('Unidade não habilitada para esta loja.')
    price = decimal(data.get('price', '0'), 2)
    if price < 0:
        raise ValidationError('Preço não pode ser negativo.')
    if id:
        old = db.execute('SELECT * FROM products WHERE tenant_id=%s AND id=%s AND active FOR UPDATE',
                         (tenant['id'], id)).fetchone()
        if not old:
            raise ValidationError('Produto não encontrado.')
        if old['unit'] != unit and db.execute('SELECT 1 FROM occupancy WHERE tenant_id=%s AND product_id=%s',
                                               (tenant['id'], id)).fetchone():
            raise ValidationError('Remova os vínculos vazios antes de alterar a unidade.')
    model = db.execute('''INSERT INTO models(tenant_id,name,brand,category) VALUES(%s,%s,%s,%s)
        ON CONFLICT(tenant_id,name,brand,category) DO UPDATE SET name=EXCLUDED.name RETURNING id''',
        (tenant['id'], name, text(data,'brand'), text(data,'category'))).fetchone()['id']
    import json
    try:
        attrs = json.loads(data.get('attributes', '{}') or '{}')
        if not isinstance(attrs, dict) or len(json.dumps(attrs)) > 2000:
            raise ValueError
    except (ValueError, TypeError):
        raise ValidationError('Características adicionais devem ser um objeto JSON de até 2000 caracteres.') from None
    sizes = [text(data, 'size')]
    if data.get('structure') == 'grade' and not id:
        sizes = [s.strip() for s in text(data, 'sizes', True, 200).split(',')]
        if len(sizes) > 30 or any(not re.fullmatch(r'[0-9]{1,3}', s) for s in sizes) or len(set(sizes)) != len(sizes):
            raise ValidationError('Informe até 30 tamanhos distintos separados por vírgula.')
    ids = []
    for size in sizes:
        variant_sku = f'{sku}-{size}' if data.get('structure') == 'grade' and not id else sku
        values = (model, variant_sku, text(data,'barcode') if len(sizes)==1 else '', text(data,'color'),
                  size, unit, price, Jsonb(attrs))
        if id:
            result = db.execute('''UPDATE products SET model_id=%s,sku=%s,barcode=%s,color=%s,size=%s,unit=%s,
                price=%s,attributes=%s WHERE tenant_id=%s AND id=%s RETURNING id''', (*values,tenant['id'],id)).fetchone()
        else:
            result = db.execute('''INSERT INTO products(model_id,sku,barcode,color,size,unit,price,attributes,tenant_id)
                VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s) RETURNING id''', (*values,tenant['id'])).fetchone()
        ids.append(result['id'])
    audit(db, tenant['id'], actor, 'produto.editar' if id else 'produto.criar', ids=ids)
    return ids


def save_position(db, tenant, actor, data, id=None):
    unit = data.get('unit', 'un')
    capacity = decimal(data.get('capacity'))
    device = text(data, 'device')
    if (unit not in ('un','kg') or capacity <= 0 or (unit=='un' and capacity != int(capacity))
            or not re.fullmatch(r'[a-zA-Z0-9_-]*', device)):
        raise ValidationError('Capacidade, unidade ou identificador do dispositivo inválido.')
    if unit == 'kg' and not tenant['fractional']:
        raise ValidationError('Estoque fracionado não habilitado.')
    if id:
        old = db.execute('SELECT * FROM positions WHERE tenant_id=%s AND id=%s AND active FOR UPDATE',
                         (tenant['id'],id)).fetchone()
        if not old:
            raise ValidationError('Posição não encontrada.')
        if old['unit'] != unit and db.execute('SELECT 1 FROM occupancy WHERE tenant_id=%s AND position_id=%s',
                                               (tenant['id'],id)).fetchone():
            raise ValidationError('Remova os vínculos antes de alterar a unidade.')
    values = (text(data,'code',True), text(data,'sector',True), integer(data.get('row_no',0)),
              integer(data.get('col_no',0)), capacity, unit, device,
              integer(data['pin'],0,39) if data.get('pin') else None)
    if id:
        db.execute('''UPDATE positions SET code=%s,sector=%s,row_no=%s,col_no=%s,capacity=%s,unit=%s,device=%s,pin=%s
            WHERE tenant_id=%s AND id=%s''', (*values,tenant['id'],id))
    else:
        id = db.execute('''INSERT INTO positions(code,sector,row_no,col_no,capacity,unit,device,pin,tenant_id)
            VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s) RETURNING id''', (*values,tenant['id'])).fetchone()['id']
    audit(db,tenant['id'],actor,'posicao.salvar',id=id)
    return id


def archive(db, tenant, actor, entity, id):
    # Identificadores SQL vêm apenas desta lista interna, nunca da requisição.
    table, fk = {'produto': ('products','product_id'), 'posicao': ('positions','position_id')}[entity]
    if not db.execute(f'SELECT id FROM {table} WHERE tenant_id=%s AND id=%s AND active FOR UPDATE', (tenant,id)).fetchone():
        raise ValidationError('Registro não encontrado.')
    if db.execute(f'SELECT 1 FROM occupancy WHERE tenant_id=%s AND {fk}=%s AND quantity>0', (tenant,id)).fetchone():
        raise ValidationError('Zere o estoque antes de excluir este cadastro.')
    db.execute(f'DELETE FROM occupancy WHERE tenant_id=%s AND {fk}=%s', (tenant,id))
    db.execute(f'UPDATE {table} SET active=false WHERE tenant_id=%s AND id=%s', (tenant,id))
    audit(db,tenant,actor,entity+'.excluir',id=id)

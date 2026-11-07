"""CRUD HTTP real com login, CSRF e conferência independente via psql. App deve estar iniciada."""
from http.cookiejar import CookieJar
from html.parser import HTMLParser
from pathlib import Path
from urllib.request import build_opener, HTTPCookieProcessor
from urllib.parse import urlencode
from urllib.error import HTTPError
import json
import subprocess
import uuid

root = Path(__file__).resolve().parents[1]
settings = dict(line.split('=', 1) for line in (root / '.env').read_text().splitlines() if '=' in line)
client = build_opener(HTTPCookieProcessor(CookieJar()))
base = 'http://127.0.0.1:8082'

class Token(HTMLParser):
    value = None
    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        if tag == 'input' and attrs.get('name') == '_csrf':
            self.value = attrs['value']

def request(path, data=None):
    response = client.open(base + path, None if data is None else urlencode(data).encode(), timeout=20)
    return response.read().decode()

def csrf(html):
    parser = Token()
    parser.feed(html)
    assert parser.value, 'Token CSRF ausente'
    return parser.value

def sql(statement):
    result = subprocess.run(['docker', 'compose', '--env-file', str(root / '.env'), '-f', str(root / 'compose.yaml'),
        'exec', '-T', 'db', 'psql', '-X', '-A', '-t', '-v', 'ON_ERROR_STOP=1', '-U', settings['DF_DB_USER'],
        '-d', 'estoque_databasefirst', '-c', statement], check=True, capture_output=True, text=True)
    return result.stdout.strip()

assert '/login' in client.open(base + '/').url
page = request('/login')
page = request('/login', {'username': settings['DF_WEB_USER'], 'password': settings['DF_WEB_PASSWORD'], '_csrf': csrf(page)})
assert 'Modelos de demonstração' in page
try:
    request('/models', {'name': 'Sem CSRF'})
    raise AssertionError('POST sem CSRF foi aceito')
except HTTPError as error:
    assert error.code == 403
name = 'DF-HTTP-' + uuid.uuid4().hex
assert sql('select current_database()') == 'estoque_databasefirst'
record_id = None
evidence = {'database': 'estoque_databasefirst', 'login': True, 'csrf_without_token': 403}
try:
    page = request('/models', {'name': name, 'brand': 'Equipe', 'category': 'Aula', '_csrf': csrf(page)})
    assert name in page
    record_id = sql(f"select id from models where name='{name}'")
    assert record_id.isdigit()
    evidence['create_read'] = {'id': int(record_id), 'name': name, 'postgres_brand': sql(f'select brand from models where id={record_id}')}
    page = request('/?id=' + record_id)
    assert 'Salvar alterações' in page
    page = request('/models', {'id': record_id, 'name': name, 'brand': 'Atualizada', 'category': 'Aula', '_csrf': csrf(page)})
    assert sql(f'select brand from models where id={record_id}') == 'Atualizada'
    evidence['update_postgres_brand'] = 'Atualizada'
    page = request(f'/models/{record_id}/delete', {'_csrf': csrf(page)})
    assert name not in page
    assert sql(f'select count(*) from models where id={record_id}') == '0'
    evidence['delete_postgres_count'] = 0
    evidence['audit_count'] = int(sql(f"select count(*) from audit where detail->>'modelId'='{record_id}'"))
    assert evidence['audit_count'] == 3
finally:
    if record_id and sql(f'select count(*) from models where id={record_id}') != '0':
        page = request('/')
        request(f'/models/{record_id}/delete', {'_csrf': csrf(page)})
(root / 'evidence').mkdir(exist_ok=True)
(root / 'evidence/http-crud.json').write_text(json.dumps(evidence, ensure_ascii=False, indent=2), encoding='utf-8')
print(json.dumps(evidence, ensure_ascii=False, indent=2))

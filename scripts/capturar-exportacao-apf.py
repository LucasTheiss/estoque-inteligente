"""Registra a entrada e o PDF realmente produzido pela SE09 na loja demonstrativa."""
from pathlib import Path
from datetime import datetime,timezone
import json,hashlib
import pymupdf
from playwright.sync_api import sync_playwright
root=Path(__file__).resolve().parents[1];out=root/'docs/apf/coletiva/capturas'
config=dict(l.split('=',1) for l in (root/'.env.demo').read_text().splitlines() if '=' in l and not l.startswith('#'))
manifest=json.loads((out/'manifesto-final.json').read_text())
def register(key,url,description):
 path=out/(key+'.png')
 manifest[:]=[m for m in manifest if m['id']!=key]
 manifest.append(dict(id=key,arquivo=str(path.relative_to(root)),url=url,descricao=description,data=datetime.now(timezone.utc).isoformat(),sha256=hashlib.sha256(path.read_bytes()).hexdigest()))
with sync_playwright() as p:
 browser=p.chromium.launch(executable_path='/usr/bin/chromium',headless=True,args=['--no-sandbox','--disable-dev-shm-usage'])
 page=browser.new_page(viewport={'width':1440,'height':1000},locale='pt-BR')
 page.goto('http://host.docker.internal:8080/login')
 page.locator('[name=loja]').select_option('calcados');page.locator('[name=username]').fill('admin');page.locator('[name=password]').fill(config['DEMO_PASSWORD'])
 page.get_by_role('button',name='Entrar',exact=True).click();page.wait_for_url(lambda u:'/login' not in u)
 page.goto('http://host.docker.internal:8080/historico',wait_until='networkidle')
 page.screenshot(path=str(out/'SE09-entrada.png'))
 register('SE09-entrada','http://localhost:8080/historico','Filtros e comandos reais de exportação do histórico antes de solicitar o arquivo PDF.')
 with page.expect_download() as info:page.get_by_role('link',name='Exportar PDF').click()
 info.value.save_as(str(out/'SE09-exportacao-real.pdf'))
 browser.close()
doc=pymupdf.open(out/'SE09-exportacao-real.pdf');doc[0].get_pixmap(matrix=pymupdf.Matrix(1.6,1.6)).save(out/'SE09-saida.png')
register('SE09-saida','http://localhost:8080/historico/exportar/pdf','Primeira página do PDF realmente exportado pelo sistema; renderização fiel do arquivo preservado SE09-exportacao-real.pdf. Dados de demonstração; a exportação também grava a auditoria.')
(out/'manifesto-final.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
print('Exportação real preservada:',len(doc),'página(s).')

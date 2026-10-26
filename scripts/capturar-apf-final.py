"""Capturas reais complementares; executar com Playwright e Chromium instalados.
Usa somente consultas, autenticação e comando ao simulador LED. Não altera estoque.
"""
from pathlib import Path
from datetime import datetime, timezone
import hashlib, json, time
from playwright.sync_api import sync_playwright, expect

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'docs/apf/coletiva/capturas'
BASE='http://host.docker.internal:8080'
config=dict(line.split('=',1) for line in (ROOT/'.env.demo').read_text().splitlines() if '=' in line and not line.startswith('#'))
manifest=[]
with sync_playwright() as p:
 browser=p.chromium.launch(executable_path='/usr/bin/chromium',headless=True,args=['--no-sandbox','--disable-dev-shm-usage'])
 context=browser.new_context(viewport={'width':1440,'height':1000},locale='pt-BR',timezone_id='America/Sao_Paulo',color_scheme='light')
 page=context.new_page()
 page.set_default_timeout(15000)
 def go(route):
  res=page.goto(BASE+route,wait_until='networkidle');assert res.status==200,(route,res.status)
 def snap(key,description):
  page.evaluate('document.fonts.ready');page.mouse.move(0,0)
  path=OUT/(key+'.png');page.screenshot(path=str(path),animations='disabled')
  manifest.append(dict(id=key,arquivo=str(path.relative_to(ROOT)).replace('\\','/'),url=page.url.replace(BASE,'http://localhost:8080'),descricao=description,data=datetime.now(timezone.utc).isoformat(),sha256=hashlib.sha256(path.read_bytes()).hexdigest()))
  (OUT/'manifesto-final.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
  print(key,flush=True)
 def login(shop):
  context.clear_cookies();go('/login')
  page.locator('[name=loja]').select_option(shop)
  page.locator('[name=username]').fill('admin' if shop!='global' else 'superadmin')
  page.locator('[name=password]').fill(config['DEMO_PASSWORD' if shop!='global' else 'SUPERADMIN_PASSWORD'])
  page.get_by_role('button',name='Entrar',exact=True).click()
  page.wait_for_url(lambda u:'/login' not in u)
 login('calcados')
 go('/modelos');snap('CE05-entrada','Seleção do modelo na lista pelo link Editar; consulta GET anterior a qualquer manutenção.')
 page.locator('a[href^="/modelos?id="]').first.click();page.wait_for_load_state('networkidle')
 snap('CE05-saida','Nome, marca e categoria recuperados pelo ID; nenhum formulário de alteração foi enviado.')
 for key,route,desc in [('X01','/fornecedores','Dados mantidos no provedor externo de fornecedores, consultados pela aplicação.'),('X02','/ofertas','Dados mantidos no provedor externo de ofertas, consultados pela aplicação.')]:
  go(route);snap(key,desc)
 for key,route in [('CE06','/vinculos'),('CE07','/usuarios'),('CE09','/iot/configuracao')]:
  go('/');snap(key+'-entrada','Acionamento da consulta pela navegação autenticada; não há filtro obrigatório para a listagem.')
  go(route);snap(key+'-saida','Resultado real da consulta na loja ativa. Os formulários visíveis não foram submetidos.')
 go('/');snap('CE11-entrada','Acionamento da consulta de auditoria pelo menu Histórico; escopo definido pela loja da sessão.')
 go('/historico')
 page.get_by_text('Auditoria recente',exact=True).scroll_into_view_if_needed()
 snap('CE11-saida','Registros reais de auditoria consultável, armazenados pela aplicação.')
 go('/');page.locator('.product-link').first.click();page.wait_for_load_state('networkidle')
 if page.locator('[data-signal]').count():
  snap('EE27-entrada','Botão de sinalização da posição do produto selecionado.')
  page.locator('[data-signal]').first.click()
  expect(page.locator('#feedback')).to_contain_text('confirmada pelo simulador',timeout=15000)
  snap('EE27-saida','Comando enviado ao broker MQTT real e confirmado pelo simulador; não comprova acionamento de hardware físico.')
 go('/');snap('EE28-entrada','Comando Sair disponível na sessão autenticada, antes de encerrar.')
 page.locator('form[action="/logout"] button').click();page.wait_for_url('**/login?logout')
 snap('EE28-saida','Sessão encerrada, com mensagem real de saída da conta.')
 login('granel');go('/movimentar')
 # Cada vínculo identifica produto/posição. A loja granel possui produtos em kg.
 page.locator('select[name=productId]').select_option(index=1)
 snap('SE08-entrada','Produto em kg e dispositivo balanca-demo selecionados; consulta de peso sem registrar movimentação.')
 for attempt in range(6):
  page.locator('[data-scale]').click();page.wait_for_timeout(2200)
  if 'Leitura simulada' in page.locator('[data-scale-output]').inner_text():break
 assert 'Leitura simulada' in page.locator('[data-scale-output]').inner_text(),page.locator('[data-scale-output]').inner_text()
 snap('SE08-saida','Leitura real do simulador MQTT: peso líquido e subtotal calculados pela aplicação. Não foi registrada movimentação.')
 browser.close()

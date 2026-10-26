"""Valida no motor real do LibreOffice, restaura entradas e salva fórmulas/caches.
Executar com Python do sistema e python3-uno, após iniciar soffice --headless
--accept='socket,host=localhost,port=2002;urp;StarOffice.ComponentContext'.
"""
import json,math,time
from pathlib import Path
from datetime import datetime,timezone
import uno
from com.sun.star.beans import PropertyValue

root=Path(__file__).resolve().parents[1]
path=root/'docs/apf/coletiva/entrega-coletiva-apf.xlsx'
expected=json.loads((root/'docs/apf/resultados.json').read_text(encoding='utf-8-sig'))
def prop(name,value):
 p=PropertyValue();p.Name=name;p.Value=value;return p
local=uno.getComponentContext();resolver=local.ServiceManager.createInstanceWithContext('com.sun.star.bridge.UnoUrlResolver',local)
for i in range(20):
 try:ctx=resolver.resolve('uno:socket,host=localhost,port=2002;urp;StarOffice.ComponentContext');break
 except Exception:
  if i==19:raise
  time.sleep(.5)
desktop=ctx.ServiceManager.createInstanceWithContext('com.sun.star.frame.Desktop',ctx)
doc=desktop.loadComponentFromURL(uno.systemPathToFileUrl(str(path)),'_blank',0,(prop('Hidden',True),prop('ReadOnly',False),prop('UpdateDocMode',3)))
assert doc is not None,'Falha de abertura no LibreOffice'
doc.enableAutomaticCalculation(True);doc.calculateAll()
checks=[]
def cell(sheet,ref):return doc.Sheets.getByName(sheet).getCellRangeByName(ref)
def check(sheet,ref,expected,label):
 c=cell(sheet,ref);assert c.getError()==0,(sheet,ref,c.getError())
 value=c.getValue() if isinstance(expected,(int,float)) else c.getString()
 assert math.isclose(value,expected,rel_tol=1e-9,abs_tol=1e-8) if isinstance(expected,(int,float)) else value==expected,(label,value,expected)
 checks.append(dict(verificacao=label,celula=f'{sheet}!{ref}',obtido=value,esperado=expected))
def formulas():
 result={}
 for s in doc.Sheets:
  cursor=s.createCursor();cursor.gotoEndOfUsedArea(False);end=cursor.RangeAddress
  region=s.getCellRangeByPosition(0,0,end.EndColumn,end.EndRow)
  for r,row in enumerate(region.getFormulaArray()):
   for c,f in enumerate(row):
    if f.startswith('='):result[(s.Name,c,r)]=f
 return result
before=formulas()
for r in expected['funcoes']:
 check(r['id'],'D65',r['pf'],'PF '+r['id'])
 check(r['id'],'B60',r['td'],'TD '+r['id'])
 check(r['id'],'D60',r['ar_rl'],'AR/RL '+r['id'])
 check(r['id'],'D64',r['complexidade'],'Complexidade '+r['id'])
for ref,key in [('B10','PFB'),('B11','NI'),('B12','FA'),('B13','PFA')]:check('Resumo',ref,expected['resumo'][key],key)
for ref,key in [('B39','esforcoHoras'),('B42','prazoDiasUteisInteiros'),('B44','custoReais')]:check('NI e estimativas',ref,expected['resumo'][key],key)

def sensitivity(ref,value,tests):
 c=cell('NI e estimativas',ref);old=c.getFormula()
 try:
  c.setValue(value);doc.calculateAll()
  for sheet,target,expected_value,label in tests:check(sheet,target,expected_value,label)
 finally:c.setFormula(old);doc.calculateAll()
pfb=expected['resumo']['PFB'];ni=expected['resumo']['NI'];pfa=expected['resumo']['PFA'];hours=expected['resumo']['esforcoHoras']
sensitivity('B6',1,[('Resumo','B11',ni+1,'Sensibilidade NI'),('Resumo','B13',pfb*(.65+.01*(ni+1)),'Sensibilidade PFA')])
sensitivity('B27',12,[('NI e estimativas','B39',pfa*12,'Sensibilidade produtividade')])
sensitivity('B28',4,[('NI e estimativas','B41',hours/24,'Sensibilidade tamanho da equipe')])
sensitivity('B30',.5,[('NI e estimativas','B41',hours/12,'Sensibilidade capacidade produtiva')])
sensitivity('B31',30,[('NI e estimativas','B44',hours*30,'Sensibilidade valor-hora')])

matrix=[[0,0,1],[0,1,2],[1,2,2]]
def idx(ar,td,kind):
 if kind=='D':r=0 if ar<=1 else 1 if ar<=5 else 2;c=0 if td<=19 else 1 if td<=50 else 2
 elif kind=='E':r=0 if ar<=1 else 1 if ar==2 else 2;c=0 if td<=4 else 1 if td<=15 else 2
 else:r=0 if ar<=1 else 1 if ar<=3 else 2;c=0 if td<=5 else 1 if td<=19 else 2
 return matrix[r][c]
for sheet,kind,ars,tds,weights in [('A01','D',[1,2,5,6],[1,19,20,50,51],[7,10,15]),('X01','D',[1,2,5,6],[1,19,20,50,51],[5,7,10]),('EE04','E',[0,1,2,3],[1,4,5,15,16],[3,4,6]),('SE02','S',[1,2,3,4],[1,5,6,19,20],[4,5,7])]:
 saved={ref:cell(sheet,ref).getFormula() for ref in ['B60','D60']}
 try:
  for ar in ars:
   for td in tds:
    cell(sheet,'B60').setValue(td);cell(sheet,'D60').setValue(ar);doc.calculateAll()
    check(sheet,'D65',weights[idx(ar,td,kind)],f'Limite {sheet}: TD {td}, AR/RL {ar}')
 finally:
  for ref,formula in saved.items():cell(sheet,ref).setFormula(formula)
  doc.calculateAll()
saved={ref:cell('CE05',ref).getFormula() for ref in ['B61','B62','D61','D62']}
try:
 for td1,ar1,td2,ar2 in [(3,1,6,2),(16,3,1,1),(4,2,5,1),(5,2,19,3),(15,1,20,1),(16,1,5,4),(1,0,1,1)]:
  for ref,value in zip(['B61','B62','D61','D62'],[td1,ar1,td2,ar2]):cell('CE05',ref).setValue(value)
  doc.calculateAll();check('CE05','D65',[3,4,6][max(idx(ar1,td1,'E'),idx(ar2,td2,'S'))],f'CE maior complexidade: {td1}/{ar1} e {td2}/{ar2}')
finally:
 for ref,f in saved.items():cell('CE05',ref).setFormula(f)
 doc.calculateAll()
# Exercita a lista editável e a eliminação de TD repetidos, preservando fórmulas.
c=cell('CE05','A17');b=cell('CE05','B17');old=(c.getFormula(),b.getFormula())
try:
 c.setString('nome');b.setString('Saída');doc.calculateAll();check('CE05','D61',3,'TD repetido não duplica saída')
 c.setString('campo demonstrativo de validação');doc.calculateAll();check('CE05','D61',4,'Novo TD altera contagem')
finally:c.setFormula(old[0]);b.setFormula(old[1]);doc.calculateAll()
assert formulas()==before,'Fórmulas alteradas durante teste de sensibilidade'
errors=[]
for sheet,c,r in before:
 error=doc.Sheets.getByName(sheet).getCellByPosition(c,r).getError()
 if error:errors.append([sheet,c,r,error])
assert not errors,errors
doc.calculateAll();doc.store()
pdf=root/'tmp/apf-final-impressao.pdf'
doc.storeToURL(uno.systemPathToFileUrl(str(pdf)),(prop('FilterName','calc_pdf_Export'),prop('Overwrite',True)))
report=dict(data=datetime.now(timezone.utc).isoformat(),motor='LibreOffice Calc (UNO)',abertura=True,recalculoAutomatico=doc.isAutomaticCalculationEnabled(),formulas=len(before),erros=errors,abas=doc.Sheets.Count,verificacoes=len(checks),resultados=expected['resumo'],testes=checks,limite='Microsoft Excel não instalado nesta máquina; não foi verificada abertura nativa nele.')
(root/'docs/apf/coletiva/validacao-final.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
doc.close(True)
print(json.dumps({k:v for k,v in report.items() if k!='testes'},ensure_ascii=False))

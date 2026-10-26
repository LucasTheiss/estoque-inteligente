"""Sincroniza a documentação coletiva com o catálogo revisto, sem tocar nos recortes."""
from pathlib import Path
from collections import Counter
import json, math
ROOT=Path(__file__).resolve().parents[1]
APF=ROOT/'docs/apf'
p=APF/'coletiva/catalogo-revisado.json'
d=json.loads(p.read_text(encoding='utf-8'))
entrada={'CE04':['X01'],'CE05':['A01'],'CE06':['A01','A02','A03','A04'],'CE07':['A06'],'CE08':['A08'],'CE09':[],'CE11':[],'CE12':['X02']}
reason={
 'CE04':'X01 aplica o filtro por código/nome/documento e fornece os dados recuperados.',
 'CE05':'A01 localiza o modelo pelo ID e fornece nome, marca e categoria.',
 'CE06':'A01/A02/A03/A04 participam da busca do vínculo pela chave produto/posição e da apresentação do vínculo. A busca q do seletor de produtos reutiliza SE06 e não é nova função CE.',
 'CE07':'A06 localiza o usuário selecionado. A saída consulta A06 e A07 para apresentar usuários, perfis e a matriz de permissões; A07 não valida a entrada do usuário.',
 'CE08':'A08 localiza a loja selecionada e fornece seus dados e recursos.',
 'CE09':'Sem seleção de registro nem validação de dado de negócio na entrada; o acionamento usa a loja da sessão. A08 é lido para apresentar a configuração.',
 'CE11':'Sem filtro de negócio na entrada; o acionamento usa a loja da sessão. A09 e A06 são lidos na saída para listar eventos e responsáveis.',
 'CE12':'X02 aplica GTIN/fornecedor e fornece os campos recuperados da oferta.'}
for f in d['funcoes']:
 if f['tipo']=='CE':
  old=(f.get('arEntrada'),f.get('arSaida'))
  f['arquivosEntrada']=entrada[f['id']];f['arquivosSaida']=f['arquivos'][:]
  f['arEntrada']=len(f['arquivosEntrada']);f['arSaida']=len(f['arquivosSaida'])
  f['regraAR']=reason[f['id']]+' Na função, cada arquivo conta uma vez na união; as avaliações de entrada/saída usam suas referências próprias.'
  if old!=(f['arEntrada'],f['arSaida']):
   d['revisao'].append(dict(id=f['id'],campo='AR entrada/saída',antes=list(old),depois=[f['arEntrada'],f['arSaida']],motivo=f['regraAR']))
 if f['id']=='SE08':f['evidencia']='IoTService.peso; movimento.html em /movimentar; DispositivosDemo na loja granel'

names=['Simples','Moderada','Complexa'];mat=[[0,0,1],[0,1,2],[1,2,2]]
weights={'ALI':[7,10,15],'AIE':[5,7,10],'EE':[3,4,6],'CE':[3,4,6],'SE':[4,5,7]}
def index(ar,td,kind):
 if kind=='D':r=0 if ar<=1 else 1 if ar<=5 else 2;c=0 if td<=19 else 1 if td<=50 else 2
 elif kind=='E':r=0 if ar<=1 else 1 if ar==2 else 2;c=0 if td<=4 else 1 if td<=15 else 2
 else:r=0 if ar<=1 else 1 if ar<=3 else 2;c=0 if td<=5 else 1 if td<=19 else 2
 return mat[r][c]
rows=[]
for f in d['dados']+d['funcoes']:
 kind=f['tipo']
 if kind in ['ALI','AIE']:
  td=len(set(f['campos']));ar=len(f['registrosLogicos']);ei=so=0;i=index(ar,td,'D')
 else:
  td=len(set(f['entrada']+f['saida']+f['controle']));ar=len(set(f['arquivos']))
  ei=len(set(f['entrada']+f['controle']));so=len(set(f['saida'])-set(f['entrada']+f['controle']))
  i=max(index(f['arEntrada'],ei,'E'),index(f['arSaida'],so,'S')) if kind=='CE' else index(ar,td,'E' if kind=='EE' else 'S')
 rows.append(dict(id=f['id'],tipo=kind,nome=f['nome'],ar_rl=ar,td=td,tdEntrada=ei,tdSaida=so,complexidade=names[i],pf=weights[kind][i]))
ni=sum(c['nota'] for c in d['caracteristicas']);pfb=sum(r['pf'] for r in rows);fa=.65+.01*ni;pfa=round(pfb*fa,2)
pr=d['premissas'];effort=round(pfa*pr['horasPorPF'],2);days=effort/(pr['integrantes']*pr['horasPorDia'])
result=dict(NI=ni,PFB=pfb,FA=fa,PFA=pfa,esforcoHoras=effort,prazoDias=round(days,2),prazoDiasUteisInteiros=math.ceil(days),custoReais=round(effort*pr['valorHora'],2))
def write(path,text):path.write_text(text+'\n',encoding='utf-8')
def js(path,obj):write(path,json.dumps(obj,ensure_ascii=False,indent=2))
js(APF/'contagem.json',{k:d[k] for k in ['dados','funcoes','caracteristicas','premissas']})
js(APF/'resultados.json',dict(resumo=result,funcoes=rows))
d['resultadosIndependentes']=dict(resumo=result,funcoes=[dict(r,ar=r['ar_rl']) for r in rows]);js(p,d)
byid={x['id']:x for x in d['dados']}
md=['# Fichas da contagem coletiva','', 'Revisão final de 25/10/2026. As 55 fichas e suas capturas estão incorporadas em [entrega-coletiva-apf.xlsx](coletiva/entrega-coletiva-apf.xlsx). ALI/AIE: TD e RL. Transações: TD e AR. A união de TD elimina repetições; acionamento e feedback são campos de controle. A leitura de arquivos não transforma automaticamente seus campos internos em TD de transação.','']
for f,r in zip(d['dados']+d['funcoes'],rows):
 md += [f"## {f['id']} — {f['nome']} ({f['tipo']})",'']
 if f['tipo'] in ['ALI','AIE']:
  md += ['Origem: '+f['origem']+'.','', 'Registros lógicos: '+ '; '.join(f['registrosLogicos'])+'.','', 'Itens de dados: '+ '; '.join(f['campos'])+'.']
 else:
  md += ['Entrada: '+('; '.join(f['entrada']) or 'acionamento; sem filtro de negócio obrigatório')+'.','', 'Saída: '+('; '.join(f['saida']) or 'resultado da operação')+'.','', 'Controle: '+'; '.join(f['controle'])+'.','', 'Arquivos: '+'; '.join(f['arquivos'])+'.','', 'Evidência: '+f['evidencia']+'.']
  if f.get('leitura'):md += ['', 'Dados internos lidos/mantidos: '+f['leitura']]
  else:md += ['', 'Dicionário dos arquivos referenciados: '+' | '.join(f"{a}: "+'; '.join(byid[a]['campos']) for a in f['arquivos'])+'. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.']
  if f.get('derivacao'):md += ['', 'Classificação / derivação: '+f['derivacao']]
  if f['tipo']=='CE':md += ['', f"Entrada: {r['tdEntrada']} TD / {f['arEntrada']} AR. Saída exclusiva: {r['tdSaida']} TD / {f['arSaida']} AR. "+f['regraAR']]
 md += ['',f"Contagem: {r['td']} TD; {r['ar_rl']} {'RL' if f['tipo'] in ['ALI','AIE'] else 'AR na união'}; complexidade {r['complexidade']}; **{r['pf']} PF**.",'',f"Protótipo real: aba {f['id']} do Excel coletivo; origem em capturas/manifesto-incorporado.json.",'']
write(APF/'FICHAS.md','\n'.join(md))
md=['# Nível de influência — características gerais','','Avaliação técnica documental da aplicação; notas de 0 a 5. Premissas acadêmicas são identificadas, sem atribuir consenso ou votos não informados.','','| Nº | Característica | NI | Justificativa | Evidência |','|---:|---|---:|---|---|']
for i,c in enumerate(d['caracteristicas'],1):md.append(f"| {i} | {c['caracteristica']} | {c['nota']} | {c['justificativa']} | {c['evidencia']} |")
md += ['',f'**NI = {ni}; FA = 0,65 + 0,01 × {ni} = {fa:.2f}.**','','Referência: docs/pdfs/Características Gerais do Software.pdf. A Parte IV, p. 11, define a constante 0,65; a p. 15 contém 0,35 e limites incoerentes. Adota-se a p. 11, com a divergência explicitada também no Excel.']
write(APF/'NI.md','\n'.join(md))
print(json.dumps(result,ensure_ascii=False))

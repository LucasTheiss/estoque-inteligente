"""Publica as cópias locais finais e registra apenas verificações efetivamente concluídas."""
from pathlib import Path
import json,hashlib,shutil
root=Path(__file__).resolve().parents[1];apf=root/'docs/apf';base=apf/'coletiva'
v=json.loads((base/'validacao-final.json').read_text())
a=json.loads((base/'validacao-impressao.json').read_text())
assert not a['erros'] and not a['cachesAusentes'] and not a['imagensForaDaPagina'] and not a['textosNaoLocalizados']
target=root/'outputs/apf-coletiva';target.mkdir(parents=True,exist_ok=True)
shutil.copy2(base/'entrega-coletiva-apf.xlsx',target/'entrega-coletiva-apf.xlsx')
stale=target/'entrega-coletiva-apf.xlsx.inspect.ndjson'
if stale.exists():stale.unlink()
sha=hashlib.sha256((base/'entrega-coletiva-apf.xlsx').read_bytes()).hexdigest()
assert sha==hashlib.sha256((target/'entrega-coletiva-apf.xlsx').read_bytes()).hexdigest()
def write(path,text):path.write_text(text.strip()+'\n',encoding='utf-8')
write(base/'README.md',f'''
# Entrega coletiva de Análise de Pontos de Função

Revisão concluída em 25/10/2026 (horário de Brasília). Entregável principal: [entrega-coletiva-apf.xlsx](entrega-coletiva-apf.xlsx). A cópia em `outputs/apf-coletiva/entrega-coletiva-apf.xlsx` é idêntica.

Integrantes: **Artur Ribeiro de Souza**, **Cristhian Ruan de Rezende** e **Lucas Andriani Theiss**.

## Conteúdo e atendimento ao enunciado

| Exigência | Onde conferir no Excel | Atendimento |
|---|---|---|
| ALIs, AIEs, itens e registros lógicos | A01–A09, X01, X02; Catálogo | 9 ALIs e 2 AIEs; TD/RL e PF calculados; capturas reais |
| EE, incluir/alterar/excluir, ALIs e dados | EE01–EE28 | 28 EEs; CRUD de modelos EE04/05/06; referências, TD/AR e imagens de entrada/resultado |
| Ao menos cinco SEs e derivação | SE02/04/06/07/08/13; Exemplos SE | Seis saídas numéricas; regras e exemplos calculáveis; também SE09 (exportação/auditoria) e SE10 (indicador de estorno) |
| Quatro CEs com entrada/saída | CE04/05/08/12 em destaque | Oito CEs no catálogo; todas com entrada e saída reais, referências por lado e complexidade máxima |
| NI, PFB, FA e PFA | Resumo, Matrizes, NI e estimativas | Fórmulas nativas; 14 notas NI justificadas |
| Esforço, prazo e custo | NI e estimativas | Premissas editáveis, unidades e fórmulas vinculadas |
| Documento único com identificação | Capa e demais 62 abas | Imagens incorporadas; não é necessário anexar arquivos para visualizar os protótipos |

## Resultado coletivo

| Tipo | Quantidade | PF |
|---|---:|---:|
| ALI | 9 | 63 |
| AIE | 2 | 10 |
| EE | 28 | 135 |
| SE | 8 | 48 |
| CE | 8 | 27 |
| **Total** | **55** | **283** |

- NI = 38; FA = 0,65 + 0,01 × 38 = 1,03; PFA = 283 × 1,03 = **291,49**.
- Premissas acadêmicas: 10 horas-pessoa/PFA; 3 integrantes; 8 horas/dia × 75% produtivos = 6 horas úteis/pessoa/dia; R$ 25/hora-pessoa.
- Esforço: **2.914,90 horas-pessoa**. Prazo: **161,9389 dias úteis**, arredondado para **162**. Custo: **R$ 72.872,50**.
- Estima-se o desenvolvimento integral da aplicação. Não é estimativa das horas restantes da atividade; infraestrutura, impostos e reserva estão explicitados como não estimados/zero.

## Correções da revisão

CE10 foi reclassificada como SE10: o indicador Estornado é derivado por EXISTS de um movimento reversor. Não houve duplicação de função. Foram corrigidos os TD de CE06, SE06 e SE09 e reduzidas três notas NI conforme a evidência disponível (41 → 38). O catálogo registra os motivos.

As referências de entrada e saída das CEs foram revistas separadamente. CE07 usa A06 na seleção e A06/A07 na saída; CE09 e CE11 não têm validação de dado de negócio na entrada, usando os arquivos para a saída. A pontuação dessas consultas não mudou. O seletor q de produtos na tela de vínculos reutiliza SE06.

SE08 foi corrigida para `/movimentar`, loja `granel`. A captura mostra 1,425 kg e R$ 42,61, derivados de 1,440 − 0,015 kg e preço R$ 29,90/kg. LED e balança usam o simulador MQTT real e estão identificados como simulação. SE09 inclui a primeira página do PDF efetivamente exportado.

## Validação realizada nesta conclusão

- Abertura, gravação e recálculo automático no **LibreOffice Calc**, via UNO.
- **{v['formulas']} fórmulas** preservadas, sem erros e com valores recalculados salvos.
- **{v['verificacoes']} verificações** de resultados, classificação, limites de matrizes, TD repetidos e sensibilidade de NI/produtividade/equipe/capacidade/valor-hora, com restauração das entradas.
- **{a['imagensIncorporadas']} imagens reais incorporadas** nas 55 fichas. Manifesto identifica arquivo, função, origem, data e hash.
- Impressão configurada em A3: **{a['paginasImpressao']} páginas** conferidas por extração de texto e limites das imagens. Nenhuma imagem dividida ou fora da página; nenhum texto de célula ausente na extração.
- Modelo e três Excel individuais preservados, com SHA-256 conferido.
- Os logs anteriores registram 90 testes Java e 13 JavaScript aprovados; essa suíte não foi repetida nesta conclusão, que não alterou o código da aplicação.

O Microsoft Excel **não está instalado nesta máquina**. O pacote XLSX foi regravado e validado no LibreOffice, mas não se afirma ter reproduzido ou confirmado a solução do erro COM observado no Excel do ambiente anterior. A abertura nativa no Microsoft Excel permanece uma limitação de verificação, não ocultada por conversão para PDF.

## Fontes e reprodução

- `catalogo-revisado.json`: catálogo e justificativas da revisão; `../contagem.json`, `../resultados.json`, `../CALCULOS.md`, `../FICHAS.md` e `../NI.md`: documentação coletiva sincronizada.
- `capturas/manifesto-incorporado.json`: todas as imagens do Excel. `capturas/SE09-exportacao-real.pdf`: saída preservada da aplicação.
- `validacao-final.json` e `validacao-impressao.json`: resultados detalhados da conferência.
- `originais-sha256.json`: integridade do modelo e dos três recortes.
- Scripts em `scripts/`: `sincronizar-apf.py`, `calcular-apf.ps1`, `finalizar-apf.py`, `validar-apf-libreoffice.py`, `auditar-entrega-apf.py`, `registrar-conclusao-apf.py` e scripts de captura. Após editar/gerar o XLSX, executar o recálculo antes de publicar a cópia.

As notas NI são avaliação técnica com premissas documentadas. Não se inventam contribuições individuais, votos ou consenso. Os recortes individuais e PDFs explicativos antigos representam suas revisões históricas e não devem ser somados nem usados como totais atuais do coletivo.

SHA-256 do XLSX final: `{sha}`.
''')
write(apf/'README.md','''
# Análise de Pontos de Função — entrega coletiva

Revisão de 25/10/2026. A entrega atual é o **[Excel coletivo](coletiva/entrega-coletiva-apf.xlsx)**, com capa, três integrantes, 63 abas, fichas, 93 capturas reais e fórmulas editáveis. Consulte a [conferência completa](coletiva/README.md) e os registros de validação antes de usar materiais históricos.

Integrantes: Artur Ribeiro de Souza; Cristhian Ruan de Rezende; Lucas Andriani Theiss.

| Tipo | Funções | PF |
|---|---:|---:|
| ALI | 9 | 63 |
| AIE | 2 | 10 |
| EE | 28 | 135 |
| SE | 8 | 48 |
| CE | 8 | 27 |
| **Total** | **55** | **283** |

**NI 38; FA 1,03; PFA 291,49.** Nas premissas de 10 horas/PFA, 3 pessoas com 6 horas produtivas/dia e R$ 25/hora: esforço 2.914,90 horas-pessoa, prazo de 162 dias úteis e custo R$ 72.872,50. A estimativa corresponde ao desenvolvimento integral, não ao restante da atividade.

## Conteúdo para apresentação

- Dados: A01–A09 e X01/X02, com itens e registros lógicos. Os provedores mantêm seus próprios TSV por CLI, fora da fronteira da aplicação de estoque; HTTP isoladamente não caracteriza AIE.
- EEs: 28 processos. Para incluir/alterar/excluir, apresentar EE04/05/06 (modelos); produto, posição, usuário e loja usam exclusão lógica quando aplicável. Movimentações/auditoria não têm edição/exclusão arbitrária.
- Saídas numéricas: SE02 (valor), SE04 (fluxo), SE06 (saldo), SE07 (ocupação), SE08 (peso líquido/subtotal), SE13 (comparação de preços). SE09 é exportação com atualização de auditoria; SE10 deriva o indicador Estornado.
- Consultas destacadas: CE04 fornecedores, CE05 modelos, CE08 lojas e CE12 ofertas. As oito CEs incluem entradas/saídas, AR separados e maior complexidade conforme os slides.
- SE08: loja `granel`, tela `/movimentar`, produto em kg, dispositivo `balanca-demo`. O simulador usa o broker MQTT real; nenhuma evidência é apresentada como acionamento físico.

## Documentação e critérios

[FICHAS.md](FICHAS.md) descreve itens e arquivos por função; [contagem.json](contagem.json) contém a fonte estruturada; [CALCULOS.md](CALCULOS.md) e [resultados.json](resultados.json) trazem os resultados; [NI.md](NI.md) justifica as 14 notas; [VALIDACAO.md](VALIDACAO.md) distingue verificações atuais e históricas.

RL é subgrupo lógico, não quantidade de linhas no banco. TD conta tipos de dados distintos que atravessam a fronteira, não todos os campos internos lidos. Um comando e um campo de feedback são contados por processo; textos alternativos no mesmo campo não multiplicam TD. Canais, paginação, status técnico, CSV/PDF e telas auxiliares da mesma intenção não geram novas funções.

Nas CEs, entrada e saída são avaliadas separadamente e a maior complexidade determina PF. Itens repetidos na saída são retirados conforme a Parte III, páginas 7 e 10–13. AR de cada lado é explicitado por processamento; o total da função usa a união. A fórmula FA = 0,65 + 0,01 × NI segue a Parte IV, página 11; a página 15 contém uma constante e limites incoerentes, explicitados no Excel.

As três planilhas individuais, seus roteiros, o PDF de divisão e os PDFs explicativos anteriores ficam preservados como materiais históricos. Seus recortes se sobrepõem e seus PFs não devem ser somados. A revisão coletiva reclassificou CE10 em SE10 e reviu o NI, razão pela qual seus totais diferem.

O XLSX foi aberto, recalculado e salvo no LibreOffice. Microsoft Excel não está instalado neste ambiente; sua abertura nativa não foi verificada. Evidências completas e limitação estão no [README coletivo](coletiva/README.md).

Para recalcular a memória textual: `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/calcular-apf.ps1`. Para executar a aplicação: seguir o README principal e o Compose com `.env.demo`.
''')
old=(apf/'VALIDACAO.md').read_text(encoding='utf-8')
if '## Conclusão coletiva de 25/10/2026' not in old:
 prefix=f'''# Verificação da APF

## Conclusão coletiva de 25/10/2026

- Entrega coletiva: 63 abas, 55 funções, {v['formulas']} fórmulas e 93 imagens reais incorporadas.
- {v['verificacoes']} verificações no LibreOffice: totais, pesos/complexidades, fronteiras das matrizes, TD repetidos e sensibilidade de NI/produtividade/capacidade/equipe/valor-hora. Entradas restauradas; recálculo automático e caches salvos sem erros.
- {a['paginasImpressao']} páginas de impressão A3; 93 imagens inteiras, dentro das páginas, e conteúdo textual das células localizado na extração. Modelo e três planilhas individuais preservados por SHA-256.
- Nova evidência da CE05 e das consultas complementares; SE08 mostra peso/subtotal reais do simulador; EE27 mostra confirmação real do simulador LED; SE09 inclui PDF exportado pelo sistema.
- Totais atuais: PFB 283, NI 38, FA 1,03, PFA 291,49, esforço 2.914,90 horas-pessoa, 162 dias úteis, R$ 72.872,50, sob premissas documentadas.
- Microsoft Excel não instalado nesta máquina: abertura nativa não verificada. O pacote foi regravado e validado no LibreOffice.
- Sem alteração no código da aplicação nesta conclusão; logs anteriores de 90 testes Java e 13 JavaScript preservados, sem alegar nova execução.
- Registros completos: `coletiva/validacao-final.json`, `coletiva/validacao-impressao.json` e `coletiva/capturas/manifesto-incorporado.json`.

## Registros históricos — números e estados das respectivas revisões

'''
 write(apf/'VALIDACAO.md',prefix+old.removeprefix('# Verificação da APF').lstrip())
print('Conclusão registrada; cópias idênticas:',sha)

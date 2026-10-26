# Prompt de retomada — entrega coletiva de APF

Continue o trabalho da entrega coletiva do projeto **Estoque Inteligente**, na branch `entrega-trabalho`. O agente anterior parou por falta de créditos. Use o estado atual do repositório como ponto de retomada; não reinicie a atividade nem refaça itens já produzidos sem antes encontrar um defeito concreto.

## Estado confirmado no repositório

O último commit é `113b343a770c43cfacad7f35091f7db886e95049` (`entrega em equipe incompleta`, 25/10/2026). Ele adicionou o rascunho coletivo `docs/apf/coletiva/entrega-coletiva-apf.xlsx` (também copiado para `outputs/apf-coletiva/`), catálogo JSON revisado, capturas de tela, manifesto, logs de testes e roteiro de vídeo. O prompt integral da atividade e os caminhos de referência estão em `docs/prompt_entrega_grupo_apf.md`.

O README da entrega coletiva registra 63 abas, 55 funções catalogadas (9 ALIs, 2 AIEs, 28 EEs, 8 SEs, 8 CEs), 283 PFB, NI proposto 38, FA 1,03, PFA 291,49, esforço 2.914,90 horas-pessoa, prazo arredondado de 162 dias úteis para 3 pessoas e custo estimado R$ 72.872,50. Trate esses números como **rascunho a validar**, não como resultado final. O catálogo JSON propõe revisões e não substitui automaticamente os documentos APF anteriores.

## Bloqueadores e pendências documentados

Leia `docs/apf/coletiva/README.md` antes de agir. Ele registra expressamente que a entrega está incompleta e ainda não deve ser entregue:

1. O arquivo coletivo é legível por ZIP/openpyxl, mas falha ao abrir no Microsoft Excel instalado. A causa ainda não foi isolada. Corrija a estrutura/exportação e valide abertura nativa sem perder conteúdo ou fórmulas.
2. Capturas reais adicionais estão no diretório `docs/apf/coletiva/capturas/`, mas parte não foi incorporada. Completar evidências específicas de entrada/saída, em especial CE05; corrigir a evidência da SE08 para a tela `/movimentar` e não alegar leitura de balança/retorno LED que não esteja comprovada.
3. A inspeção visual integral e impressão de todas as abas não foram concluídas. Os protótipos em células são descritos como ilustrativos; verifique se atendem à exigência de prototipação e se não são confundidos com capturas reais.
4. As fórmulas foram conferidas pelo gerador e cálculo independente, mas **não** foram recalculadas/sensibilizadas no Excel. Validar recálculo automático, erros, totais e edição/restauração de entradas.
5. Conciliar AR/TD das CEs, NI e documentação antiga (`docs/apf/contagem.json`, `resultados.json`, `CALCULOS.md`, `FICHAS.md`, `NI.md`, `README.md`, `VALIDACAO.md`) com a decisão final, sem contar funções sobrepostas dos Excel individuais duas vezes.
6. A equipe ainda precisa confirmar contribuições pessoais e as 14 notas/justificativas de NI; não invente consenso nem autoria.

## Roteiro de continuação

1. Faça uma matriz curta de requisito × evidência × estado, confrontando o enunciado integral no prompt original, o README, o XLSX e o código atual. Inspecione primeiro os artefatos existentes; não gere outra planilha do zero.
2. Priorize resolver a abertura do XLSX no Excel. Preserve `docs/exemploEntregaPf.xlsx` e os três Excel individuais. Use cópias temporárias durante diagnóstico. Não aceite apenas validação estrutural por biblioteca como prova de abertura no Excel.
3. Complete as capturas faltantes com imagens existentes do manifesto quando forem correspondentes; só capture de novo se faltar evidência correta. Confira se cada captura mostra a função e o estado que a ficha afirma.
4. Confira protótipos e cobertura para ALI/AIE, EE (incluindo incluir/alterar/excluir aplicáveis), ao menos cinco SEs com explicação verificável do dado derivado e quatro CEs com entrada e saída. Relacione TD/DER, RL/AR, arquivos referenciados e complexidade segundo os slides em `docs/pdfs/`.
5. Revalide fórmulas editáveis no próprio Excel: pesos/complexidade, subtotais, PFB, NI, FA, PFA, esforço, prazo e custo. Mantenha entradas separadas das fórmulas e registre premissas. Resolva divergências entre os slides e o modelo explicitamente.
6. Só depois atualize os documentos dependentes e o README para refletir o que foi realmente verificado. Liste pendências que dependam da equipe/professor sem fabricar respostas.

## Cuidados

- Não alterar silenciosamente contagens, notas NI, premissas, nomes dos integrantes ou a fronteira da aplicação. Registre a evidência de cada decisão.
- Não afirmar que os testes do log foram executados novamente nesta retomada; diferencie evidência anterior de validação nova.
- Não adicionar funções apenas para atingir mínimos se o sistema já as tiver. Não contar telas semelhantes como funções distintas sem justificativa funcional.
- Não fazer commit ou push sem solicitação explícita.
- Ao terminar, informar em poucas linhas o que foi corrigido, o que foi validado, o caminho do Excel final e as pendências reais.

## Enunciado integral

> A entrega completa da equipe é composta por:
>
> Atribua PFs de acordo com ALI e AIE na estrutura atual do seu projeto.
> Crie novas funcionalidades nos modelos de uma ALI e AIE caso não tenha.
> Não se esquecer de identificar quais são os itens de dados referenciados e registros lógicos.
> Atribua PFs de acordo com EE
> Crie funcionalidades nos modelos de uma EE (Incluir, Alterar e Excluir) senão houver.
> Identificar qual ou quais ALIs e itens de dados referenciados de cada EE.
> Atribua PFs de acordo com SE
> Crie funcionalidades (ao menos 5) nos modelos de uma SE caso não tenha em seu projeto.
> Explique porque a SE criada é um dado derivado.
> Identificar qual ou quais ALIs e itens de dados referenciados de cada SE.
> Atribua PFs de acordo com CE
> Crie 4 funcionalidades nos modelos de uma CE com cada uma delas contendo entrada e saída em seu projeto caso não tenha.
> Identificar qual ou quais ALIs e/ou AIEs e itens de dados referenciados de cada CE.
> Realize todos os cálculos NI, PFB, FA e PFA.
> Elabore a justificativa de acordo com o NI atribuído nas características gerais da aplicação.
> Realize a estimativa de esforço, prazo e custo.

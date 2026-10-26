# Prompt — entrega coletiva de Análise de Pontos de Função

Copie o texto abaixo para a conversa ou agente que vai preparar a entrega. Atualize a branch/contexto se o projeto tiver mudado.

---

Você é um assistente técnico trabalhando no projeto **Estoque Inteligente**, na branch `entrega-trabalho`. Prepare a entrega coletiva descrita no enunciado ao final deste prompt. Antes de concluir, confira a implementação atual no código e não trate documentos antigos, protótipos ilustrativos ou planilhas individuais como prova de que uma função está implementada.

## Objetivo e formato

Prepare uma entrega coletiva em **um único arquivo Excel `.xlsx`**, usando `docs/exemploEntregaPf.xlsx` como modelo visual e estrutural. A planilha Excel é o entregável principal porque as fórmulas precisam estar gravadas nas próprias células e permanecer auditáveis. Um PDF pode acompanhar o Excel para facilitar leitura/apresentação, mas não o substitui.

Não basta concatenar as três planilhas individuais: elas são recortes sobrepostos. Consolide o catálogo coletivo, remova contagens duplicadas e garanta que cada função do sistema entre uma única vez no PFB coletivo. Preserve o modelo original e os três Excel individuais.

**Não grave resultados calculados como números fixos.** Insira fórmulas nativas de Excel nas células de complexidade/peso, subtotais, PFB, NI, FA, PFA e estimativas. Deixe as premissas e entradas em células próprias; as fórmulas devem referenciar essas células e recalcular quando uma entrada mudar. Ative recálculo automático ao abrir. Não converta as fórmulas em valores após recalcular.

Inclua, no mínimo, fórmulas de planilha equivalentes a:

- Complexidade por tabela/matriz da disciplina, usando TD e RL para ALI/AIE, e TD/AR para transações.
- PF por função a partir da complexidade e do peso apropriado ao tipo.
- Subtotais de ALI, AIE, EE, SE e CE e soma total `PFB`.
- `NI = SOMA(notas das 14 características)`.
- `FA = 0,65 + 0,01 × NI`, conforme a fórmula adotada e justificada pelos slides.
- `PFA = PFB × FA`.
- Esforço, prazo e custo ligados às premissas editáveis de produtividade, capacidade e valor-hora. Escreva as premissas e unidades junto aos resultados.

Nas CEs, mantenha células para contagem/classificação da entrada e da saída separadamente e uma fórmula que aplique a regra ensinada nos slides, incluindo a seleção da maior complexidade. Não duplique TD repetidos na soma. Confira as fórmulas contra os slides e explique qualquer divergência do modelo.

Se for necessário completar funcionalidades que realmente não existam, implemente-as no projeto e gere protótipos correspondentes. Prototipe todas as funções de dados e transações incluídas na entrega. Prefira capturas reais do sistema em execução; identifique claramente dados de demonstração. Não apresente protótipo ilustrativo como captura real. Confira permissões, fluxos e integração com a fronteira do sistema.

## Arquivos do projeto para consultar

Use os arquivos abaixo como fontes complementares e compare as alegações com o código:

- `docs/apf/README.md` — resumo coletivo, cobertura alegada, funções escolhidas e estimativa atual.
- `docs/apf/FICHAS.md` — fichas funcionais, itens de dados, arquivos/RLs referenciados, entradas, saídas, classificação e evidências de código.
- `docs/apf/contagem.json` — catálogo estruturado dos grupos de dados, transações e características gerais.
- `docs/apf/resultados.json` — resultados tabulados de PF por função e totais.
- `docs/apf/CALCULOS.md` — fórmulas, convenções e estimativas.
- `docs/apf/NI.md` — 14 características gerais, notas, justificativas e evidências.
- `docs/apf/VALIDACAO.md` — validações que já foram executadas; confira se ainda se aplicam à versão atual.
- `docs/entregas-individuais/README.md` — escopo e limitações dos três recortes individuais.
- `docs/entregas-individuais/catalogo_produtos.xlsx` — recorte de Artur Ribeiro de Souza.
- `docs/entregas-individuais/posicoes_ocupacao.xlsx` — recorte de Cristhian Ruan de Rezende.
- `docs/entregas-individuais/movimentacoes_usuarios.xlsx` — recorte de Lucas Andriani Theiss.
- `docs/entregas-individuais/capturas-reais/README.md` e `manifesto.json` — origem e descrição das capturas reais incorporadas nos Excel.
- `docs/entregas-individuais/roteiro_catalogo_produtos.md`, `roteiro_posicoes_ocupacao.md` e `roteiro_movimentacoes_usuarios.md` — explicações dos recortes individuais.
- `docs/entregas-individuais/guia_estrutura_dos_excels.md` — estrutura das planilhas e significado dos códigos e siglas.
- `docs/divisao_entregas_individuais.pdf` — divisão anterior; pode estar desatualizada quanto às funcionalidades implementadas.
- `docs/exemploEntregaPf.xlsx` — modelo original de Excel, preservado. Use-o como referência de estrutura e fórmulas, sem alterá-lo.
- `docs/pdfs/` — slides e material da disciplina para matrizes, pesos, classificação, NI, estimativas e exemplos.
- `src/main/` — implementação do sistema; verifique controllers, serviços, repositórios e telas.
- `src/test/` — testes existentes, como evidência complementar de comportamento.
- `external/fornecedores/` e `external/ofertas/` — provedores externos usados para demonstrar os AIEs.

Não use os números acima sem revalidar o estado atual do código e dos arquivos. A última contagem coletiva registrada no projeto é **9 ALIs, 2 AIEs, 28 EEs, 7 SEs e 9 CEs: 282 PFB; NI 41; FA 1,06; PFA 298,92**. Trate isso como ponto de partida a conferir, não como resultado imutável. Os três recortes individuais têm sobreposição e seus PFBs não devem ser somados.

## O que a entrega precisa demonstrar

1. Inventário coletivo das funções de dados da aplicação: ALIs e AIEs realmente mantidos/consultados dentro da fronteira definida. Para cada ALI/AIE, descreva finalidade, registros lógicos (RL), itens de dados distintos (TD/DER), regra de contagem, complexidade, peso e pontos de função. Crie ou complete dados e formulários de manutenção dos grupos que realmente estejam faltando.
2. Inventário coletivo de entradas externas (EEs). Verifique operações de incluir, alterar e excluir nos grupos em que essas operações se aplicam. Identifique a intenção, os dados distintos que atravessam a fronteira, acionamento e mensagem de resultado, os ALIs/AIEs lidos ou mantidos, campos internos relevantes, AR, complexidade e PF de cada EE. Distinga alteração/exclusão lógica da exclusão física quando for o comportamento real.
3. Pelo menos cinco saídas externas (SEs). Para cada uma, indique entradas/filtros, saídas, ALIs referenciados, itens de dados, RL/AR, TD, complexidade e PF. Mostre a regra que produz o valor derivado e use um exemplo numérico conferível. Explique por que o resultado é derivado, não apenas recuperado. Não conte várias telas da mesma intenção como funções distintas sem justificativa funcional.
4. Quatro consultas externas (CEs), cada uma com protótipo de entrada e protótipo de saída. Para cada CE, identifique filtros/campos de entrada, campos recuperados na saída, ALIs e/ou AIEs referenciados, AR, TD sem duplicação, complexidade e PF. Mostre que a consulta não mantém arquivos nem calcula novo dado de negócio. Se a aplicação tiver mais CEs, mantenha a contagem completa consistente e explique quais quatro atendem ao destaque do enunciado.
5. Protótipos de cada função de dados e transação que compõe a entrega coletiva. Mostre formulário/entrada e resultado/saída em contexto. Use dados de demonstração identificados. A captura deve corresponder à função descrita e ao comportamento atual; telas de funções parecidas não provam automaticamente todas as operações de manutenção.
6. Contagem completa sem duplicar funções: subtotais ALI, AIE, EE, SE e CE; PFB; soma das 14 notas de NI; FA; PFA. Mostre a fórmula e o resultado intermediário. Justifique cada nota NI com evidência do projeto, separando fatos verificados de premissas acadêmicas.
7. Estimativas de esforço, prazo e custo com fórmulas, unidades e premissas explícitas. Diferencie estimativa para desenvolver o sistema inteiro de esforço restante para concluir a atividade. Não apresente hipóteses de produtividade, jornada ou valor-hora como fatos medidos.

## Método e conferência

- Siga as definições, matrizes e pesos do material da disciplina em `docs/pdfs/` e explique qualquer divergência entre slides e modelo.
- Use os dados que atravessam a fronteira para contar TD/DER de uma transação; dados internos lidos/mantidos identificam arquivos referenciados e não viram automaticamente TD adicionais.
- Um ALI/AIE é grupo lógico reconhecido pelo usuário, não simplesmente uma tabela de banco. RL não é quantidade de linhas armazenadas.
- Classifique EE, SE ou CE pela intenção funcional e pelo processamento observável, não pelo nome da tela ou rota.
- Para CEs, documente a avaliação de entrada e saída conforme os slides, removendo itens repetidos antes de somar os TDs e usando a regra de complexidade indicada pelo material.
- Confirme no código se dados externos são mantidos fora da fronteira. Uma resposta HTTP, sozinha, não comprova um AIE.
- Confira que exemplos, prints, referências de arquivos, fórmulas e resultados correspondem ao sistema atual. Registre funções planejadas que não tenham implementação real.
- Caso implemente algo, execute verificações adequadas ao impacto da mudança e atualize as fichas e os cálculos afetados.

## Entregáveis

Produza um **único Excel coletivo**, pronto para conferência e apresentação, com capa e nomes completos dos integrantes; fronteira e visão geral; catálogo coletivo; fichas detalhadas; protótipos legíveis; justificativas de classificação; matrizes/contagens; NI e estimativas. Organize abas para que o professor possa seguir dados de entrada, regras de classificação, subtotais, PFB, NI, FA, PFA e planejamento. As células de cálculo devem conter fórmulas editáveis do Excel, e não resultados colados como valores. Preserve os estilos e a organização do modelo quando possível e mantenha `docs/exemploEntregaPf.xlsx` intacto. Gere PDF complementar apenas se for útil para apresentar o conteúdo impresso.

Antes de encerrar, faça uma lista de atendimento item a item do enunciado, indique caminhos dos arquivos finais, evidências consultadas, cálculos coletivos e pendências reais. Não alegue contribuições pessoais ou testes que não foram confirmados/executados.

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

---

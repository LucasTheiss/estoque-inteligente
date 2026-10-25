# Roteiro de apresentação — Artur Ribeiro de Souza

Documento explicado: [catalogo_produtos.xlsx](catalogo_produtos.xlsx). Recorte: catálogo e produtos.

Use este material como apoio de estudo. As falas sugeridas explicam o documento; não afirmam quem programou ou identificou cada função. Os exemplos marcados como ilustrativos servem para demonstrar as regras.

## 1. Abertura sugerida

“Meu documento analisa o recorte de catálogo e produtos do Estoque Inteligente usando Análise de Pontos de Função. Apresento dois ALIs, dois AIEs, duas EEs, duas SEs e duas CEs, com protótipos, dados referenciados e contagem. O resultado do recorte é 52 pontos de função brutos e 55,12 pontos ajustados.”

## 2. Conceitos para explicar antes das funções

- **Fronteira:** a aplicação Estoque Inteligente. A classificação depende de quem mantém os dados, não do computador ou contêiner em que eles estão.
- **ALI:** grupo lógico de dados reconhecido pelo usuário e mantido pela aplicação. Não significa simplesmente uma tabela de banco.
- **AIE:** grupo lógico consultado aqui e mantido por outra aplicação.
- **RL:** subgrupo lógico reconhecido dentro de um arquivo. Um RL não significa uma única linha ou um único produto cadastrado.
- **TD/DER:** tipo de dado reconhecido pelo usuário; conta-se o tipo, não o número de registros. Cem produtos não significam cem vezes os campos.
- **AR:** arquivo lógico referenciado por uma transação. Um arquivo lido e mantido na mesma função não é contado duas vezes.
- **EE:** processo cuja intenção principal é manter dados internos ou alterar o comportamento do sistema.
- **SE:** saída com processamento adicional; nas SEs escolhidas há dados calculados.
- **CE:** recuperação de dados sem derivar informação nova nem manter arquivos.

Nas transações, diferencie os campos que atravessam a interface dos campos internos lidos ou mantidos. Os campos internos explicam as referências; não são automaticamente TD adicionais. A contagem documentada inclui acionamento e mensagem de resultado. Os campos de negócio de cada função estão listados abaixo.

## 3. Meus dois ALIs

### A01 — Modelos de produto

É ALI porque o Estoque Inteligente mantém esse grupo de dados.

**Registro lógico:** Registro de modelos de produto. **Campos (5):** loja, código, nome, marca, categoria.

**Contagem:** 1 RL e 5 TD → complexidade simples → 7 PF.

Ao mostrar o protótipo, aponte os campos e explique o significado do grupo. Campos de contexto, identificações geradas e estados internos não precisam ser digitados manualmente.

### A02 — Produtos

É ALI porque o Estoque Inteligente mantém esse grupo de dados.

**Registro lógico:** Registro de produtos. **Campos (11):** loja, código, modelo, SKU, código de barras, cor, tamanho, unidade, preço, características, ativo.

**Contagem:** 1 RL e 11 TD → complexidade simples → 7 PF.

Ao mostrar o protótipo, aponte os campos e explique o significado do grupo. Campos de contexto, identificações geradas e estados internos não precisam ser digitados manualmente.

## 4. Meus dois AIEs

Os dois AIEs são compartilhados pelos três recortes. Seus provedores externos mantêm os registros; o Estoque Inteligente apenas os consulta. A tela de consulta não transforma o arquivo externo em ALI.

### X01 — Fornecedores externos

**Registro lógico:** Registro de fornecedores externos. **Campos (5):** código, nome, documento, e-mail, telefone.

**Contagem:** 1 RL e 5 TD → simples → 5 PF.

Mostre o protótipo e destaque que a manutenção ocorre fora da fronteira da aplicação. O arquivo de dados e o processo que o consulta são funções diferentes na APF.

### X02 — Ofertas externas

**Registro lógico:** Oferta comercial externa. **Campos (9):** GTIN, código do fornecedor, SKU externo, descrição, marca, unidade, preço cotado, disponibilidade, data de atualização.

**Contagem:** 1 RL e 9 TD → simples → 5 PF.

Mostre o protótipo e destaque que a manutenção ocorre fora da fronteira da aplicação. O arquivo de dados e o processo que o consulta são funções diferentes na APF.

## 5. Minhas duas EEs

### EE04 — Incluir modelo

O usuário informa nome, marca e categoria. O sistema mantém o modelo e registra a operação na auditoria. É EE porque recebe dados e mantém informação interna. O ALI A09 também entra na contagem de arquivos referenciados: a auditoria é um grupo lógico consultável no sistema.

**Entrada de negócio:** nome, marca, categoria. **Retorno:** mensagem de resultado.

**Arquivos e campos internos:** A01: loja, nome, marca e categoria; identificação do modelo gerada no cadastro. A09: loja, responsável, ação modelo.salvar, detalhe com a identificação do modelo e data gerada. Esses dados de auditoria são mantidos internamente e não são campos extras recebidos pela interface.

**Contagem:** 2 AR e 5 TD → moderada → 4 PF.

No protótipo, siga a sequência: dados informados → confirmação → arquivos mantidos e resposta.

### EE01 — Incluir produto, inclusive grade e saldo inicial

O cadastro pode criar produto, grade e saldo inicial. Por isso não acessa somente Produtos: também envolve modelo, posição, vínculo, movimento inicial e auditoria. Os arquivos usados nas alternativas do mesmo processo são considerados; não se exige que toda execução crie saldo. É uma EE porque sua intenção principal é cadastrar e manter dados.

**Entrada de negócio:** nome, marca, categoria, SKU, código de barras, cor, tamanho, unidade, preço, características, estrutura, tamanhos da grade, posição inicial, quantidades iniciais. **Retorno:** mensagem de resultado.

**Arquivos e campos internos:** A01: nome, marca e categoria do modelo. A02: modelo, SKU, código de barras, cor, tamanho, unidade, preço, características e ativo. A03: posição e unidade, na entrada inicial. A04: produto, posição e quantidade inicial. A05: produto, posição, responsável, tipo, variação, saldos anterior/posterior, motivo e data, quando há saldo inicial. A09: loja, responsável, ação, detalhe e data (gerada). Identificações dos registros são geradas pelo banco; leituras/manutenções internas não são TD adicionais da interface.

**Contagem:** 6 AR e 16 TD → complexa → 6 PF.

No protótipo, siga a sequência: dados informados → confirmação → arquivos mantidos e resposta.

### Como apresentar a contribuição pessoal

Antes da apresentação, confirme a participação real na aba Contribuição. Use uma frase como: “Na identificação da EE [código], minha contribuição foi [atividade que efetivamente realizei], conferindo [campos, arquivos, classificação ou protótipo].” Preencha apenas com fatos. A divisão de temas não comprova autoria da implementação nem participação em todas as etapas.

## 6. Minhas duas SEs

### SE06 — Pesquisar estoque e localizar produto

**Entrada de negócio:** busca, produto selecionado.

**Saída:** SKU, código de barras, nome, marca, categoria, cor, tamanho, unidade, preço, características, saldo total, posição, setor, quantidade na posição.

**Arquivos e dados utilizados:** A01: nome, marca e categoria. A02: identificação/SKU, código de barras, cor, tamanho, unidade, preço e características. A03: posição e setor. A04: produto, posição e quantidade usada no saldo total.

**Por que existe dado derivado:** Soma das posições para obter saldo. Dashboard, busca digitada/voz e lista de produtos reutilizam a mesma intenção; não contar canais novamente.

Exemplo ilustrativo: o mesmo produto tem 4 unidades em uma posição e 6 em outra. A busca mostra saldo total 10. Esse total é calculado somando posições, por isso a função é SE apesar de ser chamada de pesquisa. As várias telas que oferecem a mesma intenção não viram novas funções automaticamente.

**Contagem:** 4 AR e 18 TD → complexa → 7 PF.

Mostre o protótipo e aponte especificamente o valor calculado. Explique quais dados armazenados deram origem a ele.

### SE02 — Valor potencial do estoque

**Entrada de negócio:** sem filtro de negócio informado; o usuário aciona a função.

**Saída:** SKU, unidade, saldo, preço, valor por produto, total da loja.

**Arquivos e dados utilizados:** A02: produto/SKU, unidade e preço atual. A04: produto e quantidade para calcular o saldo. O processamento é restrito à loja do usuário.

**Por que existe dado derivado:** Saldo × preço atual, arredondado em centavos por produto; total = soma dos valores. Não é custo nem receita realizada.

Exemplo ilustrativo: saldo de 10 unidades e preço atual de R$ 25,00 geram valor potencial de R$ 250,00. O relatório soma os valores dos produtos para formar o total da loja. É dado derivado porque multiplica saldo por preço e agrega resultados. Não representa lucro, custo de aquisição nem receita já realizada.

**Contagem:** 2 AR e 8 TD → moderada → 5 PF.

Mostre o protótipo e aponte especificamente o valor calculado. Explique quais dados armazenados deram origem a ele.

## 7. Minhas duas CEs, com entrada e saída

### CE04 — Consultar fornecedores externos

Na entrada, informe um filtro por código, nome ou documento e acione a consulta. Na saída, são apresentados código, nome, documento, e-mail e telefone. A função referencia somente X01 e seus cinco campos. São dados já existentes: não há soma, percentual ou atualização.

**Contagem documentada:** 1 AR; 3 TD na entrada (filtro, acionamento e mensagem de resultado) e 5 na saída; ambas as partes simples; peso final 3 PF. Mostre os dois protótipos, primeiro o filtro e depois o resultado.

### CE12 — Consultar ofertas externas

Na entrada, informe GTIN e/ou código do fornecedor. Na saída, são apresentados os nove campos de X02: GTIN, código do fornecedor, SKU externo, descrição, marca, unidade, preço cotado, disponibilidade e data de atualização. Os filtros são opcionais e combinados. O preço exibido é a cotação armazenada pelo provedor; não é calculado nesta consulta.

**Contagem documentada:** 1 AR; 4 TD na entrada, incluindo os dois controles, e 7 na saída sem repetir GTIN e fornecedor já contados na entrada. Ambas as partes simples; peso final 3 PF.

Nas cópias do modelo, a entrada da CE usa a matriz EE e a saída usa a matriz SE; seleciona-se a maior complexidade e aplica-se o peso de CE. Isso segue a convenção dos slides usada no trabalho. O modelo original tinha uma matriz única e foi preservado; a correção foi autorizada para as entregas.

## 8. Como explicar o PFB

“Para dados, cruzo RL com TD; para transações, cruzo AR com TD. A matriz indica a complexidade, e o tipo de função determina o peso. Depois somo os pesos das funções do meu recorte.”

| Função | RL ou AR | TD | Complexidade | PF |
|---|---:|---:|---|---:|
| A01 — Modelos de produto | 1 | 5 | Simples | 7 |
| A02 — Produtos | 1 | 11 | Simples | 7 |
| X01 — Fornecedores externos | 1 | 5 | Simples | 5 |
| X02 — Ofertas externas | 1 | 9 | Simples | 5 |
| EE04 — Incluir modelo | 2 | 5 | Moderada | 4 |
| EE01 — Incluir produto, inclusive grade e saldo inicial | 6 | 16 | Complexa | 6 |
| SE06 — Pesquisar estoque e localizar produto | 4 | 18 | Complexa | 7 |
| SE02 — Valor potencial do estoque | 2 | 8 | Moderada | 5 |
| CE04 — Consultar fornecedores externos | 1 | 8 | Simples | 3 |
| CE12 — Consultar ofertas externas | 1 | 11 | Simples | 3 |

**PFB = 7 + 7 + 5 + 5 + 4 + 6 + 7 + 5 + 3 + 3 = 52.**

Os dois ALIs detalhados são o recorte individual de funções de dados. As transações podem referenciar outros ALIs do sistema; esses arquivos aparecem nas referências e influenciam a complexidade, sem serem adicionados como novas funções de dados à entrega individual. Não apresente este recorte como uma aplicação autônoma completa.

## 9. NI, FA e PFA

O NI é a soma das notas de 0 a 5 das 14 características gerais. Ele avalia a influência das características da aplicação; não é a quantidade de funções e não é a matriz de complexidade de uma EE. Os três documentos usam a mesma avaliação da aplicação.

| Característica | Nota | Justificativa para explicar |
|---|---:|---|
| Comunicação de dados | 5 | Aplicação on-line com HTTP e MQTT; novo cliente HTTP de fornecedores. Mais de um protocolo. |
| Processamento distribuído | 3 | Envia comandos para processamento no controlador IoT. Retorno de telemetria não demonstra transferência de processamento nas duas direções nem escolha dinâmica de CPU. |
| Desempenho | 1 | Há limites de conexão, paginação e timeouts, mas não foi fornecido SLA crítico nem evidência de análise de desempenho rigorosa. |
| Utilização de equipamento | 2 | Compose define limites de memória e pools; há considerações de recursos e segurança. Não exige CPU exclusiva. |
| Volume de transações | 1 | Premissa acadêmica: picos mensais de inventário. Volume real não medido; revisar se houver histórico operacional. |
| Entrada on-line | 5 | Mais de 30% dos processos de entrada são interativos: cadastros, movimentos e configurações. |
| Eficiência do usuário final | 4 | Menus, mouse, paginação, navegação, ajuda contextual e poucas telas; redução de digitação por código de barras, voz e balança. Sem medição formal de eficiência. |
| Atualização on-line | 4 | Maioria dos ALIs atualizada on-line, com transações, bloqueios e idempotência contra perda/duplicação. Não se assume recuperação de volumes extremos. |
| Processamento complexo | 2 | Dois critérios: auditoria/segurança específica e lógica de negócio de estorno, saldo e permissões. Não se considera matemática extensiva nem reprocessamento batch complexo. |
| Reutilização de código | 4 | Biblioteca de regras empacotável, documentada e com consumidor externo. Extensões exigem código; não se atribui customização geral por parâmetros. |
| Facilidade de implantação | 2 | Roteiros de execução e migrações preparados. Não há evidência de conversão automatizada de dados legados para justificar 4/5. |
| Facilidade operacional | 2 | Inicialização por scripts com intervenção (1); consultas e relatórios minimizam papel (1). Não se presume backup/recuperação automática. |
| Múltiplos locais | 2 | Premissa: lojas com navegadores e equipamentos similares. Multi-loja implementado; não há evidência de parque heterogêneo e plano de manutenção por local. |
| Facilidade de mudanças | 4 | Consulta flexível sobre vários ALIs (2) e controles em tabelas alteráveis on-line com efeito imediato, como permissões/recursos (2). |

**NI = 5 + 3 + 1 + 2 + 1 + 5 + 4 + 4 + 2 + 4 + 2 + 2 + 2 + 4 = 41.**

**FA = 0,65 + 0,01 × 41 = 1,06.**

**PFA = PFB × FA = 52 × 1,06 = 55,12.**

O ajuste aumenta o tamanho bruto em 6%. As notas de volume e múltiplos locais incluem premissas declaradas, não medições reais. Se perguntarem sobre os slides: foi adotada a constante 0,65 da Parte IV, página 11; a página 15 apresenta 0,35 e limites inconsistentes, divergência registrada na planilha.

## 10. Esforço, prazo e custo

As taxas são hipóteses didáticas de planejamento, não resultados medidos nem valores obrigatórios definidos pela APF: 10 horas por PFA, uma pessoa neste recorte, jornada de 8 horas com 75% de aproveitamento (6 horas produtivas por dia), R$ 25,00 por hora, reserva zero e custos adicionais zero.

- **Esforço:** 55,12 × 10 = **551,20 horas-pessoa**.
- **Prazo:** 551,20 ÷ 6 = 91,87 dias; arredondando para cima: **92 dias úteis**.
- **Custo:** 551,20 × R$ 25,00 = **R$ 13.780,00**.

“Pontos de função medem tamanho funcional. Converto esse tamanho em horas usando uma premissa de produtividade, divido as horas pela capacidade diária para estimar prazo e multiplico pelo valor-hora para estimar custo.”

## 11. Perguntas prováveis

**Por que os três documentos usam os mesmos AIEs e CEs?** Porque são recortes individuais do mesmo sistema. Há sobreposição explícita; não se somam os três totais para calcular o tamanho da aplicação. A contagem completa registrada é 282 PFB e 298,92 PFA.

**Por que duas funções com a mesma quantidade de campos podem ter pesos diferentes?** Porque tipo de função e número de arquivos/RLs também determinam complexidade e peso.

**Se uma EE faz contas, vira SE?** Não automaticamente. Registrar entrada calcula saldo, mas sua intenção principal é manter estoque e movimento, portanto é EE. A comparação de preços gera uma saída calculada, portanto é SE.

**Filtrar ou ordenar uma lista cria dado derivado?** Isso, sozinho, não cria um novo valor de negócio. Somar saldos, multiplicar saldo por preço ou calcular diferenças produz dado derivado nas SEs deste trabalho.

**Os protótipos são capturas reais?** Sim. As imagens incorporadas foram capturadas no navegador, com o sistema local em execução e dados de demonstração. Nas EEs, mostram formulários e detalhes de tela, sem enviar novos cadastros ou movimentos. Nas CEs, mostram os filtros antes do envio e os resultados após a consulta. Os exemplos numéricos ilustrativos deste roteiro continuam sendo exemplos didáticos, não necessariamente os valores dos prints.

**Como os cálculos foram conferidos?** Os três Excel têm 80 fórmulas cada, cujos 240 resultados coincidiram com recálculo independente no LibreOffice. Na versão atual, a formatação original foi preservada: 12 abas e 14 espaços de imagens, com 19 páginas na renderização. Os dois blocos adicionais de EE detalham resultados das funções já contadas, sem acrescentar PF. A validação da aplicação registrou 90 testes Java aprovados e 13 verificações HTTP aprovadas.

## 12. Ordem sugerida para apresentar

1. Abra a primeira aba (Contribuição), diga seu nome e delimite o recorte.
2. Explique os dois ALIs e seus RLs/campos, mostrando os protótipos.
3. Explique os dois AIEs e quem mantém os dados.
4. Apresente as EEs, suas referências e sua contribuição real.
5. Nas SEs, faça o exemplo de cálculo e aponte o dado derivado.
6. Nas CEs, mostre primeiro a entrada e depois a saída.
7. Mostre a soma dos pesos, NI, FA e PFA.
8. Explique as premissas de esforço, prazo e custo.

Fontes locais deste roteiro: planilha individual, docs/apf/contagem.json, docs/apf/resultados.json, docs/apf/NI.md e registro de validação. Os exemplos ilustrativos não alteram a contagem nem os arquivos Excel.

## 13. Leitura detalhada do meu Excel, aba por aba

Esta seção complementa o roteiro anterior e acompanha o arquivo final com prints reais. Aba é a guia dentro do Excel; página é uma folha na impressão. As 12 abas geraram 19 páginas na renderização de conferência. Outro programa ou configuração de impressão pode mudar a paginação.

Um grupo lógico descreve dados persistentes do negócio; uma transação descreve uma operação. Exemplo: Produtos é o que se mantém; Incluir produto é o que se faz. O mesmo arquivo pode participar de várias operações sem se tornar um arquivo diferente.

| Aba | Nome no meu Excel | Páginas de referência |
| --- | --- | --- |
| 1 | Contribuição | 1–2 |
| 2 | A01 ALI | 3 |
| 3 | A02 ALI | 4 |
| 4 | X01 AIE | 5 |
| 5 | X02 AIE | 6 |
| 6 | Entradas externas | 7–10 |
| 7 | SE06 SE | 11 |
| 8 | SE02 SE | 12 |
| 9 | CE04 Consulta | 13 |
| 10 | CE12 Consulta | 14 |
| 11 | Critérios e fontes | 15–16 |
| 12 | NI e estimativas | 17–19 |

### 1. Contribuição

Identifica o integrante e resume as funções atribuídas. A lista distingue tipo e peso; o total bruto está em D17. As duas linhas de continuação das EEs valem zero para não contar a mesma operação novamente. Na parte inferior, B20:B25 apenas trazem os resultados da aba NI e estimativas.

### 2. A01 ALI

Descreve o primeiro grupo lógico interno: responsabilidade de manutenção, registro lógico, campos, complexidade e peso. A imagem real mostra uma tela relacionada ao grupo, mas não substitui o dicionário de campos.

**O que os dados representam:** Modelos de produto. O registro lógico é “Registro de modelos de produto”.

**Todos os campos desta ficha:** loja, código, nome, marca, categoria.

Leia A como o subgrupo lógico, B como o nome dos campos, C como o tipo e D como quantos TD estão naquela linha. String é texto, Decimal é número com casas decimais, Date é data e Boolean representa sim/não. O tipo não multiplica os pontos.

**Exemplo de leitura de uma linha:** “loja; código; modelo” representa três campos diferentes. O ponto e vírgula só economiza espaço visual. A quantidade de linhas preenchidas não substitui a contagem de campos.

Campos gerados pelo sistema, como identificações ou contexto da loja, podem fazer parte do dicionário sem aparecer como uma caixa editável no print. Um registro lógico também não é a quantidade de produtos ou usuários cadastrados.

| Célula | Significado | Valor |
| --- | --- | --- |
| C27 | RL | 1 |
| C28 | TD/DER | 5 |
| B29 | Complexidade | Simples |
| B30 | PF | 7 |

### 3. A02 ALI

Descreve o segundo grupo lógico interno. Como o modelo tem menos linhas, alguns campos aparecem juntos separados por ponto e vírgula. A coluna D indica a quantidade de TD representados na linha.

**O que os dados representam:** Produtos. O registro lógico é “Registro de produtos”.

**Todos os campos desta ficha:** loja, código, modelo, SKU, código de barras, cor, tamanho, unidade, preço, características, ativo.

Leia A como o subgrupo lógico, B como o nome dos campos, C como o tipo e D como quantos TD estão naquela linha. String é texto, Decimal é número com casas decimais, Date é data e Boolean representa sim/não. O tipo não multiplica os pontos.

**Exemplo de leitura de uma linha:** “loja; código; modelo” representa três campos diferentes. O ponto e vírgula só economiza espaço visual. A quantidade de linhas preenchidas não substitui a contagem de campos.

Campos gerados pelo sistema, como identificações ou contexto da loja, podem fazer parte do dicionário sem aparecer como uma caixa editável no print. Um registro lógico também não é a quantidade de produtos ou usuários cadastrados.

| Célula | Significado | Valor |
| --- | --- | --- |
| C15 | RL | 1 |
| C16 | TD/DER | 11 |
| B17 | Complexidade | Simples |
| B18 | PF | 7 |

### 4. X01 AIE

Descreve fornecedores mantidos fora do Estoque Inteligente. Um cadastro externo é um AIE aqui; a operação que o consulta é outra função, a CE04.

**O que os dados representam:** Fornecedores externos. O registro lógico é “Registro de fornecedores externos”.

**Todos os campos desta ficha:** código, nome, documento, e-mail, telefone.

Leia A como o subgrupo lógico, B como o nome dos campos, C como o tipo e D como quantos TD estão naquela linha. String é texto, Decimal é número com casas decimais, Date é data e Boolean representa sim/não. O tipo não multiplica os pontos.

**Exemplo de leitura de uma linha:** “loja; código; modelo” representa três campos diferentes. O ponto e vírgula só economiza espaço visual. A quantidade de linhas preenchidas não substitui a contagem de campos.

Campos gerados pelo sistema, como identificações ou contexto da loja, podem fazer parte do dicionário sem aparecer como uma caixa editável no print. Um registro lógico também não é a quantidade de produtos ou usuários cadastrados.

| Célula | Significado | Valor |
| --- | --- | --- |
| C16 | RL | 1 |
| C17 | TD/DER | 5 |
| B18 | Complexidade | Simples |
| B19 | PF | 5 |

### 5. X02 AIE

Descreve ofertas mantidas pelo provedor externo. O arquivo possui preço cotado e disponibilidade armazenados. A CE12 recupera esses valores; a SE13 usa a cotação para calcular diferenças.

**O que os dados representam:** Ofertas externas. O registro lógico é “Oferta comercial externa”.

**Todos os campos desta ficha:** GTIN, código do fornecedor, SKU externo, descrição, marca, unidade, preço cotado, disponibilidade, data de atualização.

Leia A como o subgrupo lógico, B como o nome dos campos, C como o tipo e D como quantos TD estão naquela linha. String é texto, Decimal é número com casas decimais, Date é data e Boolean representa sim/não. O tipo não multiplica os pontos.

**Exemplo de leitura de uma linha:** “loja; código; modelo” representa três campos diferentes. O ponto e vírgula só economiza espaço visual. A quantidade de linhas preenchidas não substitui a contagem de campos.

Campos gerados pelo sistema, como identificações ou contexto da loja, podem fazer parte do dicionário sem aparecer como uma caixa editável no print. Um registro lógico também não é a quantidade de produtos ou usuários cadastrados.

| Célula | Significado | Valor |
| --- | --- | --- |
| C16 | RL | 1 |
| C17 | TD/DER | 9 |
| B18 | Complexidade | Simples |
| B19 | PF | 5 |

### 6. Entradas externas

Contém duas EEs contadas e dois blocos de detalhamento. Primeiro leia finalidade, arquivos e campos; depois a tabela de TD, a lista de AR e a complexidade. Os blocos inferiores mostram detalhes das mesmas operações e acrescentam zero PF.

**EE04: Incluir modelo.** Para apresentar, diga quais informações o usuário fornece, quais grupos são lidos ou mantidos e qual é a resposta. A intenção principal é manter informação interna.

**Referências internas:** A01: loja, nome, marca e categoria; identificação do modelo gerada no cadastro. A09: loja, responsável, ação modelo.salvar, detalhe com a identificação do modelo e data gerada. Esses dados de auditoria são mantidos internamente e não são campos extras recebidos pela interface.

| Célula | Significado | Valor |
| --- | --- | --- |
| C36 | Arquivos referenciados (AR) | 2 |
| C37 | TD/DER da interface | 5 |
| B38 | Complexidade | Moderado |
| B39 | PF | 4 |

**EE01: Incluir produto, inclusive grade e saldo inicial.** Para apresentar, diga quais informações o usuário fornece, quais grupos são lidos ou mantidos e qual é a resposta. A intenção principal é manter informação interna.

**Referências internas:** A01: nome, marca e categoria do modelo. A02: modelo, SKU, código de barras, cor, tamanho, unidade, preço, características e ativo. A03: posição e unidade, na entrada inicial. A04: produto, posição e quantidade inicial. A05: produto, posição, responsável, tipo, variação, saldos anterior/posterior, motivo e data, quando há saldo inicial. A09: loja, responsável, ação, detalhe e data (gerada). Identificações dos registros são geradas pelo banco; leituras/manutenções internas não são TD adicionais da interface.

| Célula | Significado | Valor |
| --- | --- | --- |
| C78 | Arquivos referenciados (AR) | 6 |
| C79 | TD/DER da interface | 16 |
| B80 | Complexidade | Complexo |
| B81 | PF | 6 |

**Por que aparecem A07, A08 ou A09 em algumas referências?** São outros grupos do sistema: permissões, lojas/configuração e auditoria. Eles podem apoiar uma transação mesmo sem serem um dos dois ALIs detalhados no recorte. Conte os arquivos efetivamente listados na EE, não todos os ALIs existentes no sistema.

**Como ler as continuações:** B106 e B135 valem zero. Não são EEs novas e não representam erro. As imagens mostram detalhes do formulário ou registros existentes; não se deve dizer que documentam uma inclusão efetuada durante a captura.

**Contribuição pessoal:** explique o que você realmente conferiu, identificou ou documentou. Estar responsável pelo tema não comprova autoria da programação.

### 7. SE06 SE

Explica a primeira saída externa: entradas, arquivos utilizados, resultados e regra que produz o dado derivado. A palavra saída não significa apenas mostrar uma tela; é necessário explicar o processamento descrito.

**Entrada:** busca, produto selecionado.

**Saída:** SKU, código de barras, nome, marca, categoria, cor, tamanho, unidade, preço, características, saldo total, posição, setor, quantidade na posição.

**Arquivos:** A01 = Modelos de produto, A02 = Produtos, A03 = Posições, A04 = Estoque por posição / vínculos.

**Transformação que deve ser explicada:** Soma das posições para obter saldo. Dashboard, busca digitada/voz e lista de produtos reutilizam a mesma intenção; não contar canais novamente.

Cada campo de saída é um tipo de informação, não um ponto por linha do relatório. Um relatório com cem produtos continua com os mesmos tipos de dados que teria com dois produtos.

| Célula | Significado | Valor |
| --- | --- | --- |
| C29 | AR | 4 |
| C30 | TD/DER | 18 |
| B31 | Complexidade | Complexo |
| B32 | PF | 7 |

### 8. SE02 SE

Explica a segunda saída externa e sua própria regra de cálculo. A classificação e o peso são da função inteira, e não de cada linha que aparece no relatório.

**Entrada:** não há filtro de negócio listado; existe acionamento da função.

**Saída:** SKU, unidade, saldo, preço, valor por produto, total da loja.

**Arquivos:** A02 = Produtos, A04 = Estoque por posição / vínculos.

**Transformação que deve ser explicada:** Saldo × preço atual, arredondado em centavos por produto; total = soma dos valores. Não é custo nem receita realizada.

Cada campo de saída é um tipo de informação, não um ponto por linha do relatório. Um relatório com cem produtos continua com os mesmos tipos de dados que teria com dois produtos.

| Célula | Significado | Valor |
| --- | --- | --- |
| C23 | AR | 2 |
| C24 | TD/DER | 8 |
| B25 | Complexidade | Moderado |
| B26 | PF | 5 |

### 9. CE04 Consulta

Documenta a consulta aos fornecedores: filtro de entrada e dados recuperados na saída. Os dois prints são etapas de uma única CE. A classificação separa entrada e saída conforme a convenção adotada nos slides.

**Entrada:** filtro código, nome ou documento.

**Saída:** código, nome, documento, e-mail, telefone.

**Arquivos:** X01 = Fornecedores externos.

**Por que continua sendo CE:** selecionar registros por um filtro não produz um novo valor calculado de negócio. A consulta não grava o cadastro e não calcula totais, diferenças ou percentuais.

Na ficha, a célula de TD mostra o total distinto documentado. A explicação de entrada/saída informa a divisão usada para classificar separadamente as duas partes. Na CE12, GTIN e fornecedor já aparecem na entrada; os demais sete campos compõem a saída sem repetição.

| Célula | Significado | Valor |
| --- | --- | --- |
| C23 | AR | 1 |
| C24 | TD/DER | 8 |
| B25 | Complexidade | Simples |
| B26 | PF | 3 |

### 10. CE12 Consulta

Documenta a consulta às ofertas: filtros por GTIN e fornecedor e resultado com os dados externos. GTIN e fornecedor aparecem nos dois lados, mas não são contados duas vezes no total documentado.

**Entrada:** GTIN, código do fornecedor.

**Saída:** GTIN, código do fornecedor, SKU externo, descrição, marca, unidade, preço cotado, disponibilidade, data de atualização.

**Arquivos:** X02 = Ofertas externas.

**Por que continua sendo CE:** selecionar registros por um filtro não produz um novo valor calculado de negócio. A consulta não grava o cadastro e não calcula totais, diferenças ou percentuais.

Na ficha, a célula de TD mostra o total distinto documentado. A explicação de entrada/saída informa a divisão usada para classificar separadamente as duas partes. Na CE12, GTIN e fornecedor já aparecem na entrada; os demais sete campos compõem a saída sem repetição.

| Célula | Significado | Valor |
| --- | --- | --- |
| C22 | AR | 1 |
| C23 | TD/DER | 11 |
| B24 | Complexidade | Simples |
| B25 | PF | 3 |

### 11. Critérios e fontes

Apresenta a fronteira, as convenções e as matrizes de complexidade. A matriz cruza a quantidade de campos com a quantidade de RL ou AR; depois o tipo da função determina o peso. Não é a tabela do NI.

**Exemplo de uso da matriz:** um ALI com 1 RL e 11 TD cai na faixa de 1 a 19 TD e é simples: 7 PF. Uma EE com 2 AR e 5 TD cai na faixa de 5 a 15 TD e é moderada: 4 PF. Não use a matriz de ALI para classificar uma EE.

As palavras simples, moderada e complexa indicam o enquadramento funcional da contagem, não uma avaliação do talento do programador ou da aparência da tela.

### 12. NI e estimativas

Concentra o ajuste e o planejamento: PFB, soma das 14 notas, FA, PFA, premissas de capacidade/produtividade e cálculos de horas, dias e reais. Taxas como 10 h/PFA e R$ 25/h são hipóteses de planejamento.

| Célula | Como interpretar | Valor |
| --- | --- | --- |
| B2 | PFB: soma dos pesos do recorte | 52 |
| B3 | NI: soma de B9:B22 | 41 |
| B4 | FA: 0,65 + 0,01 × NI | 1,06 |
| B5 | PFA: PFB × FA | 55,12 |
| B28 | Horas por ponto ajustado | 10 |
| B29 | Pessoas equivalentes | 1 |
| B30 | Jornada de referência, h/dia | 8 |
| B31 | Alocação produtiva: 0,75 equivale a 75% | 0,75 |
| B32 | Dias úteis por semana | 5 |
| B33 | Valor da hora, R$ | 25 |
| B34 | Reserva de esforço: zero nesta estimativa | 0 |
| B35 | Custos adicionais, R$ | 0 |
| B39 | Horas produtivas por pessoa/dia | 6 |
| B40 | Esforço base, horas-pessoa | 551,20 |
| B41 | Reserva em horas | 0 |
| B42 | Esforço total, horas-pessoa | 551,20 |
| B43 | Capacidade diária, h/dia | 6 |
| B44 | Prazo calculado, antes do arredondamento | 91,87 |
| B45 | Prazo planejado, dias úteis inteiros | 92 |
| B46 | Prazo em semanas úteis | 18,40 |
| B47 | Custo base de mão de obra, R$ | 13780 |
| B48 | Custo da reserva, R$ | 0 |
| B49 | Custo total, R$ | 13780 |

Nas linhas 9 a 22, leia a característica em A, a nota em B, a justificativa em C e a evidência em D. Nota alta significa maior influência segundo o critério adotado; não significa “software melhor”. Não aumente a nota só para aumentar o tamanho.

Esforço é trabalho total; prazo é tempo de calendário de trabalho; custo é valor monetário estimado. Não são a mesma coisa. A conta utiliza uma pessoa neste recorte, seis horas produtivas por dia e arredondamento do prazo para cima.

## 14. Exercício rápido para fixar

1. Localize um código Axx na tabela de uma EE e diga qual grupo ele representa.
2. Localize um TD e explique se vem da interface ou se é apenas um dado interno lido.
3. Mostre o resultado derivado de uma SE e refaça um exemplo do cálculo.
4. Mostre uma CE e explique por que o filtro não transforma a função em SE.
5. Aponte o PFB, explique por que o NI é 41 e calcule o FA.
6. Mostre onde as horas viram dias e onde as horas viram reais.

Ao apresentar, você pode consultar as tabelas: o objetivo é entender e defender as decisões, não decorar cada endereço de célula.

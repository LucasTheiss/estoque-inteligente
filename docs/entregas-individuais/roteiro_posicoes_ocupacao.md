# Roteiro de apresentação — Cristhian Ruan de Rezende

Documento explicado: [posicoes_ocupacao.xlsx](posicoes_ocupacao.xlsx). Recorte: posições e ocupação.

Use este material como apoio de estudo. As falas sugeridas explicam o documento; não afirmam quem programou ou identificou cada função. Os exemplos marcados como ilustrativos servem para demonstrar as regras.

## 1. Abertura sugerida

“Meu documento analisa o recorte de posições e ocupação do Estoque Inteligente usando Análise de Pontos de Função. Apresento dois ALIs, dois AIEs, duas EEs, duas SEs e duas CEs, com protótipos, dados referenciados e contagem. O resultado do recorte é 50 pontos de função brutos e 53,00 pontos ajustados.”

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

### A03 — Posições

É ALI porque o Estoque Inteligente mantém esse grupo de dados.

**Registro lógico:** Registro de posições. **Campos (11):** loja, identificador, código, setor, linha, coluna, capacidade, unidade, dispositivo, pino, ativo.

**Contagem:** 1 RL e 11 TD → complexidade simples → 7 PF.

Ao mostrar o protótipo, aponte os campos e explique o significado do grupo. Campos de contexto, identificações geradas e estados internos não precisam ser digitados manualmente.

### A04 — Estoque por posição / vínculos

É ALI porque o Estoque Inteligente mantém esse grupo de dados.

**Registro lógico:** Registro de estoque por posição / vínculos. **Campos (4):** loja, produto, posição, quantidade.

**Contagem:** 1 RL e 4 TD → complexidade simples → 7 PF.

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

### EE07 — Incluir posição

A posição representa um endereço físico do estoque, com capacidade e unidade. O cadastro mantém A03 e registra auditoria em A09. Dispositivo e pino permitem associar a posição à sinalização IoT. É EE porque recebe dados e cria um registro interno.

**Entrada de negócio:** código, setor, linha, coluna, capacidade, unidade, dispositivo, pino. **Retorno:** mensagem de resultado.

**Arquivos e campos internos:** A03: loja, código, setor, linha, coluna, capacidade, unidade, dispositivo, pino e ativo; identificação gerada no cadastro. A09: loja, responsável, ação posicao.salvar, detalhe com a identificação da posição e data gerada. Auditoria é manutenção interna, sem TD extras na interface.

**Contagem:** 2 AR e 10 TD → moderada → 4 PF.

No protótipo, siga a sequência: dados informados → confirmação → arquivos mantidos e resposta.

### EE10 — Incluir vínculo

O usuário escolhe produto e posição. O sistema verifica os cadastros e as unidades, cria o vínculo em A04 com quantidade zero e registra auditoria. Atenção: esta EE não recebe quantidade e não adiciona estoque. A quantidade muda pelas movimentações.

**Entrada de negócio:** produto, posição. **Retorno:** mensagem de resultado.

**Arquivos e campos internos:** A02: identificação do produto, unidade e ativo. A03: identificação da posição, unidade e ativo. A04: loja, produto, posição e quantidade inicial zero. A09: loja, responsável, ação vinculo.criar, detalhe produto:posição e data gerada. O usuário informa somente produto/posição; quantidade não é entrada desta EE.

**Contagem:** 4 AR e 4 TD → moderada → 4 PF.

No protótipo, siga a sequência: dados informados → confirmação → arquivos mantidos e resposta.

### Como apresentar a contribuição pessoal

Antes da apresentação, confirme a participação real na aba Contribuição. Use uma frase como: “Na identificação da EE [código], minha contribuição foi [atividade que efetivamente realizei], conferindo [campos, arquivos, classificação ou protótipo].” Preencha apenas com fatos. A divisão de temas não comprova autoria da implementação nem participação em todas as etapas.

## 6. Minhas duas SEs

### SE07 — Mapa operacional de posições

**Entrada de negócio:** sem filtro de negócio informado; o usuário aciona a função.

**Saída:** posição, setor, linha, coluna, capacidade, unidade, ocupado, nome do produto, SKU, quantidade do vínculo, alerta de excesso.

**Arquivos e dados utilizados:** A01: nome do modelo/produto exibido. A02: produto/SKU. A03: posição, setor, linha, coluna, capacidade e unidade. A04: produto, posição e quantidade dos vínculos.

**Por que existe dado derivado:** Soma as quantidades dos vínculos por posição e compara com a capacidade para apresentar alerta de excesso. Mapa e lista de ocupação são canais do mesmo processo.

Exemplo ilustrativo: posição com capacidade 10 e vínculos somando 12 unidades. O sistema calcula ocupado = 12 e apresenta alerta de excesso. Soma e comparação geram informação derivada. Mapa e lista de ocupação representam o mesmo processo, sem duplicar pontos apenas pelo formato de apresentação.

**Contagem:** 4 AR e 13 TD → complexa → 7 PF.

Mostre o protótipo e aponte especificamente o valor calculado. Explique quais dados armazenados deram origem a ele.

### SE04 — Fluxo de movimentações

**Entrada de negócio:** início, fim.

**Saída:** SKU, unidade, tipo, número de operações, variação líquida.

**Arquivos e dados utilizados:** A02: produto/SKU e unidade. A05: produto, tipo, variação e data; os registros do período são contados e suas variações são somadas por produto/tipo.

**Por que existe dado derivado:** COUNT(*) e SUM(delta) por produto/tipo. Período inclusivo em America/Sao_Paulo; estornos em categoria própria.

Exemplo ilustrativo: duas entradas de 5 e 3 unidades no período geram número de operações = 2 e variação líquida = +8 para aquele produto e tipo. O relatório agrupa por produto/tipo, conta operações e soma variações. Não soma unidades de produtos diferentes indiscriminadamente. Os estornos têm categoria própria; as datas seguem America/Sao_Paulo.

**Contagem:** 2 AR e 9 TD → moderada → 5 PF.

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
| A03 — Posições | 1 | 11 | Simples | 7 |
| A04 — Estoque por posição / vínculos | 1 | 4 | Simples | 7 |
| X01 — Fornecedores externos | 1 | 5 | Simples | 5 |
| X02 — Ofertas externas | 1 | 9 | Simples | 5 |
| EE07 — Incluir posição | 2 | 10 | Moderada | 4 |
| EE10 — Incluir vínculo | 4 | 4 | Moderada | 4 |
| SE07 — Mapa operacional de posições | 4 | 13 | Complexa | 7 |
| SE04 — Fluxo de movimentações | 2 | 9 | Moderada | 5 |
| CE04 — Consultar fornecedores externos | 1 | 8 | Simples | 3 |
| CE12 — Consultar ofertas externas | 1 | 11 | Simples | 3 |

**PFB = 7 + 7 + 5 + 5 + 4 + 4 + 7 + 5 + 3 + 3 = 50.**

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

**PFA = PFB × FA = 50 × 1,06 = 53,00.**

O ajuste aumenta o tamanho bruto em 6%. As notas de volume e múltiplos locais incluem premissas declaradas, não medições reais. Se perguntarem sobre os slides: foi adotada a constante 0,65 da Parte IV, página 11; a página 15 apresenta 0,35 e limites inconsistentes, divergência registrada na planilha.

## 10. Esforço, prazo e custo

As taxas são hipóteses didáticas de planejamento, não resultados medidos nem valores obrigatórios definidos pela APF: 10 horas por PFA, uma pessoa neste recorte, jornada de 8 horas com 75% de aproveitamento (6 horas produtivas por dia), R$ 25,00 por hora, reserva zero e custos adicionais zero.

- **Esforço:** 53,00 × 10 = **530,00 horas-pessoa**.
- **Prazo:** 530,00 ÷ 6 = 88,33 dias; arredondando para cima: **89 dias úteis**.
- **Custo:** 530,00 × R$ 25,00 = **R$ 13.250,00**.

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
| 2 | A03 ALI | 3 |
| 3 | A04 ALI | 4 |
| 4 | X01 AIE | 5 |
| 5 | X02 AIE | 6 |
| 6 | Entradas externas | 7–10 |
| 7 | SE07 SE | 11 |
| 8 | SE04 SE | 12 |
| 9 | CE04 Consulta | 13 |
| 10 | CE12 Consulta | 14 |
| 11 | Critérios e fontes | 15–16 |
| 12 | NI e estimativas | 17–19 |

### 1. Contribuição

Identifica o integrante e resume as funções atribuídas. A lista distingue tipo e peso; o total bruto está em D17. As duas linhas de continuação das EEs valem zero para não contar a mesma operação novamente. Na parte inferior, B20:B25 apenas trazem os resultados da aba NI e estimativas.

### 2. A03 ALI

Descreve o primeiro grupo lógico interno: responsabilidade de manutenção, registro lógico, campos, complexidade e peso. A imagem real mostra uma tela relacionada ao grupo, mas não substitui o dicionário de campos.

**O que os dados representam:** Posições. O registro lógico é “Registro de posições”.

**Todos os campos desta ficha:** loja, identificador, código, setor, linha, coluna, capacidade, unidade, dispositivo, pino, ativo.

Leia A como o subgrupo lógico, B como o nome dos campos, C como o tipo e D como quantos TD estão naquela linha. String é texto, Decimal é número com casas decimais, Date é data e Boolean representa sim/não. O tipo não multiplica os pontos.

**Exemplo de leitura de uma linha:** “loja; código; modelo” representa três campos diferentes. O ponto e vírgula só economiza espaço visual. A quantidade de linhas preenchidas não substitui a contagem de campos.

Campos gerados pelo sistema, como identificações ou contexto da loja, podem fazer parte do dicionário sem aparecer como uma caixa editável no print. Um registro lógico também não é a quantidade de produtos ou usuários cadastrados.

| Célula | Significado | Valor |
| --- | --- | --- |
| C27 | RL | 1 |
| C28 | TD/DER | 11 |
| B29 | Complexidade | Simples |
| B30 | PF | 7 |

### 3. A04 ALI

Descreve o segundo grupo lógico interno. Como o modelo tem menos linhas, alguns campos aparecem juntos separados por ponto e vírgula. A coluna D indica a quantidade de TD representados na linha.

**O que os dados representam:** Estoque por posição / vínculos. O registro lógico é “Registro de estoque por posição / vínculos”.

**Todos os campos desta ficha:** loja, produto, posição, quantidade.

Leia A como o subgrupo lógico, B como o nome dos campos, C como o tipo e D como quantos TD estão naquela linha. String é texto, Decimal é número com casas decimais, Date é data e Boolean representa sim/não. O tipo não multiplica os pontos.

**Exemplo de leitura de uma linha:** “loja; código; modelo” representa três campos diferentes. O ponto e vírgula só economiza espaço visual. A quantidade de linhas preenchidas não substitui a contagem de campos.

Campos gerados pelo sistema, como identificações ou contexto da loja, podem fazer parte do dicionário sem aparecer como uma caixa editável no print. Um registro lógico também não é a quantidade de produtos ou usuários cadastrados.

| Célula | Significado | Valor |
| --- | --- | --- |
| C15 | RL | 1 |
| C16 | TD/DER | 4 |
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

**EE07: Incluir posição.** Para apresentar, diga quais informações o usuário fornece, quais grupos são lidos ou mantidos e qual é a resposta. A intenção principal é manter informação interna.

**Referências internas:** A03: loja, código, setor, linha, coluna, capacidade, unidade, dispositivo, pino e ativo; identificação gerada no cadastro. A09: loja, responsável, ação posicao.salvar, detalhe com a identificação da posição e data gerada. Auditoria é manutenção interna, sem TD extras na interface.

| Célula | Significado | Valor |
| --- | --- | --- |
| C36 | Arquivos referenciados (AR) | 2 |
| C37 | TD/DER da interface | 10 |
| B38 | Complexidade | Moderado |
| B39 | PF | 4 |

**EE10: Incluir vínculo.** Para apresentar, diga quais informações o usuário fornece, quais grupos são lidos ou mantidos e qual é a resposta. A intenção principal é manter informação interna.

**Referências internas:** A02: identificação do produto, unidade e ativo. A03: identificação da posição, unidade e ativo. A04: loja, produto, posição e quantidade inicial zero. A09: loja, responsável, ação vinculo.criar, detalhe produto:posição e data gerada. O usuário informa somente produto/posição; quantidade não é entrada desta EE.

| Célula | Significado | Valor |
| --- | --- | --- |
| C78 | Arquivos referenciados (AR) | 4 |
| C79 | TD/DER da interface | 4 |
| B80 | Complexidade | Moderado |
| B81 | PF | 4 |

**Por que aparecem A07, A08 ou A09 em algumas referências?** São outros grupos do sistema: permissões, lojas/configuração e auditoria. Eles podem apoiar uma transação mesmo sem serem um dos dois ALIs detalhados no recorte. Conte os arquivos efetivamente listados na EE, não todos os ALIs existentes no sistema.

**Como ler as continuações:** B106 e B135 valem zero. Não são EEs novas e não representam erro. As imagens mostram detalhes do formulário ou registros existentes; não se deve dizer que documentam uma inclusão efetuada durante a captura.

**Contribuição pessoal:** explique o que você realmente conferiu, identificou ou documentou. Estar responsável pelo tema não comprova autoria da programação.

### 7. SE07 SE

Explica a primeira saída externa: entradas, arquivos utilizados, resultados e regra que produz o dado derivado. A palavra saída não significa apenas mostrar uma tela; é necessário explicar o processamento descrito.

**Entrada:** não há filtro de negócio listado; existe acionamento da função.

**Saída:** posição, setor, linha, coluna, capacidade, unidade, ocupado, nome do produto, SKU, quantidade do vínculo, alerta de excesso.

**Arquivos:** A01 = Modelos de produto, A02 = Produtos, A03 = Posições, A04 = Estoque por posição / vínculos.

**Transformação que deve ser explicada:** Soma as quantidades dos vínculos por posição e compara com a capacidade para apresentar alerta de excesso. Mapa e lista de ocupação são canais do mesmo processo.

Cada campo de saída é um tipo de informação, não um ponto por linha do relatório. Um relatório com cem produtos continua com os mesmos tipos de dados que teria com dois produtos.

| Célula | Significado | Valor |
| --- | --- | --- |
| C29 | AR | 4 |
| C30 | TD/DER | 13 |
| B31 | Complexidade | Complexo |
| B32 | PF | 7 |

### 8. SE04 SE

Explica a segunda saída externa e sua própria regra de cálculo. A classificação e o peso são da função inteira, e não de cada linha que aparece no relatório.

**Entrada:** início, fim.

**Saída:** SKU, unidade, tipo, número de operações, variação líquida.

**Arquivos:** A02 = Produtos, A05 = Movimentações.

**Transformação que deve ser explicada:** COUNT(*) e SUM(delta) por produto/tipo. Período inclusivo em America/Sao_Paulo; estornos em categoria própria.

Cada campo de saída é um tipo de informação, não um ponto por linha do relatório. Um relatório com cem produtos continua com os mesmos tipos de dados que teria com dois produtos.

| Célula | Significado | Valor |
| --- | --- | --- |
| C23 | AR | 2 |
| C24 | TD/DER | 9 |
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
| B2 | PFB: soma dos pesos do recorte | 50 |
| B3 | NI: soma de B9:B22 | 41 |
| B4 | FA: 0,65 + 0,01 × NI | 1,06 |
| B5 | PFA: PFB × FA | 53 |
| B28 | Horas por ponto ajustado | 10 |
| B29 | Pessoas equivalentes | 1 |
| B30 | Jornada de referência, h/dia | 8 |
| B31 | Alocação produtiva: 0,75 equivale a 75% | 0,75 |
| B32 | Dias úteis por semana | 5 |
| B33 | Valor da hora, R$ | 25 |
| B34 | Reserva de esforço: zero nesta estimativa | 0 |
| B35 | Custos adicionais, R$ | 0 |
| B39 | Horas produtivas por pessoa/dia | 6 |
| B40 | Esforço base, horas-pessoa | 530 |
| B41 | Reserva em horas | 0 |
| B42 | Esforço total, horas-pessoa | 530 |
| B43 | Capacidade diária, h/dia | 6 |
| B44 | Prazo calculado, antes do arredondamento | 88,33 |
| B45 | Prazo planejado, dias úteis inteiros | 89 |
| B46 | Prazo em semanas úteis | 17,80 |
| B47 | Custo base de mão de obra, R$ | 13250 |
| B48 | Custo da reserva, R$ | 0 |
| B49 | Custo total, R$ | 13250 |

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

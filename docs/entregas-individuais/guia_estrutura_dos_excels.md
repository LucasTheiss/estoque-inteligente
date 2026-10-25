# Guia de leitura dos três Excel

Estrutura, siglas, códigos e páginas — Estoque Inteligente

Este guia é comum aos documentos de Artur Ribeiro de Souza, Cristhian Ruan de Rezende e Lucas Andriani Theiss. Ele explica como navegar, interpretar as tabelas e relacionar as funções. Os roteiros individuais detalham as funções e os exemplos de cada integrante.

## 1. Primeiro: o que é A01? E X01?

**A01 é o identificador do grupo “Modelos de produto”. X01 é o identificador do grupo “Fornecedores externos”.** São nomes curtos usados para fazer referências nas tabelas do trabalho.

A letra A foi usada no catálogo deste projeto para os arquivos lógicos internos; X foi usada para os arquivos de interface externa. Os números identificam cada grupo. Essa escolha de letras é uma convenção do projeto, não uma exigência universal da APF.

O 01 de A01 não significa 1 ponto, 1 campo, 1 tabela ou 1 registro. A01 vale 7 PF neste recorte, tem 5 campos e 1 RL. X01 vale 5 PF, também com 5 campos e 1 RL, porque seu tipo de função é diferente.

**A01 não é a célula A1.** No texto da análise, A01 identifica uma função de dados. Já B30 é um endereço da planilha: coluna B, linha 30. Uma referência como “A01 ALI!B30” significa ler B30 da aba chamada A01 ALI.

**EE04, SE06 e CE12 também são identificadores.** O prefixo informa o tipo de transação e o número identifica a função no catálogo completo. Os números não representam ordem de execução, sequência de páginas ou quantidade de pontos. Por isso há saltos na numeração dos recortes.

## 2. Dicionário das siglas principais

| Sigla | Significado | Como entender |
| --- | --- | --- |
| APF | Análise de Pontos de Função | Método de medir tamanho funcional a partir dos dados e operações reconhecidos pelo usuário. |
| PF | Ponto de função | Unidade de tamanho funcional; não é linha de código nem hora. |
| ALI | Arquivo Lógico Interno | Grupo lógico de dados que a aplicação mantém. |
| AIE | Arquivo de Interface Externa | Grupo lógico consultado aqui e mantido por outra aplicação. |
| EE | Entrada Externa | Operação cuja intenção principal é manter dados internos ou alterar o comportamento do sistema. |
| SE | Saída Externa | Saída com processamento adicional; nas selecionadas, há valores derivados. |
| CE | Consulta Externa | Operação que recupera dados sem derivação nem manutenção de arquivos. |
| RL | Registro Lógico | Subgrupo lógico reconhecido dentro de um ALI/AIE; não é uma linha cadastrada no banco. |
| AR | Arquivo Referenciado | ALI/AIE lido ou mantido pela transação; um mesmo arquivo conta uma vez por função. |
| TD | Tipo de Dado | Campo distinto reconhecido pelo usuário, conforme a ficha da função. |
| DER | Dado Elementar Referenciado | Termo usado no material/modelo para a contagem de campos; nas planilhas, corresponde ao TD contado. |
| NI | Nível de Influência | Nota de uma característica; NI total é a soma das 14 notas, cada uma de 0 a 5. |
| PFB | Pontos de Função Brutos | Soma dos pesos das funções antes do fator de ajuste. |
| FA | Fator de Ajuste | Multiplicador obtido a partir do NI: 0,65 + 0,01 × NI. |
| PFA | Pontos de Função Ajustados | PFB multiplicado pelo FA. |

## 3. Catálogo dos códigos de dados

| Código | Grupo lógico | Papel no sistema |
| --- | --- | --- |
| A01 | Modelos de produto | Mantido internamente |
| A02 | Produtos | Mantido internamente |
| A03 | Posições | Mantido internamente |
| A04 | Estoque por posição / vínculos | Mantido internamente |
| A05 | Movimentações | Mantido internamente |
| A06 | Usuários | Mantido internamente |
| A07 | Permissões por perfil | Mantido internamente |
| A08 | Lojas e configuração | Mantido internamente |
| A09 | Auditoria consultável | Mantido internamente |
| X01 | Fornecedores externos | Mantido externamente e consultado |
| X02 | Ofertas externas | Mantido externamente e consultado |

A01 é o modelo do produto (nome, marca, categoria); A02 é o produto/variação (SKU, tamanho, cor, preço). A03 é a posição física (por exemplo, A1); A04 é o vínculo de um produto com uma posição e sua quantidade. Não confunda o identificador A01 da análise com o código A1 de uma posição física do estoque.

A05 registra eventos de movimentação; A06 guarda usuários; A07 guarda permissões; A08 identifica a loja e suas configurações; A09 registra auditoria. Os três últimos podem aparecer nas referências das transações sem ter uma aba de ALI individual nas entregas.

X01 reúne dados do fornecedor; X02 reúne as ofertas desse fornecedor. A mesma empresa pode ter várias ofertas. Consultar um cadastro por HTTP não o torna ALI: a classificação depende de quem é responsável por mantê-lo.

## 4. Códigos das transações utilizadas

| Código | Função | Por que tem esse tipo |
| --- | --- | --- |
| EE01 | Incluir produto, inclusive grade e saldo inicial | Mantém dados internos |
| EE04 | Incluir modelo | Mantém dados internos |
| EE07 | Incluir posição | Mantém dados internos |
| EE10 | Incluir vínculo | Mantém dados internos |
| EE13 | Incluir usuário | Mantém dados internos |
| EE21 | Registrar entrada | Mantém dados internos |
| SE02 | Valor potencial do estoque | Produz dados derivados |
| SE04 | Fluxo de movimentações | Produz dados derivados |
| SE06 | Pesquisar estoque e localizar produto | Produz dados derivados |
| SE07 | Mapa operacional de posições | Produz dados derivados |
| SE13 | Comparar preços internos e ofertas externas | Produz dados derivados |
| CE04 | Consultar fornecedores externos | Recupera dados sem cálculo novo |
| CE12 | Consultar ofertas externas | Recupera dados sem cálculo novo |

Uma função pode ler um arquivo que pertence ao recorte de outro integrante. Por exemplo, o relatório de fluxo usa A02 e A05, mesmo no recorte de posições. O recorte é uma seleção acadêmica dentro do sistema completo, não um aplicativo separado.

## 5. Quem explica qual documento?

| Integrante | Arquivo | Funções atribuídas |
| --- | --- | --- |
| Artur Ribeiro de Souza | catalogo_produtos.xlsx | A01, A02, X01, X02, EE04, EE01, SE06, SE02, CE04, CE12 |
| Cristhian Ruan de Rezende | posicoes_ocupacao.xlsx | A03, A04, X01, X02, EE07, EE10, SE07, SE04, CE04, CE12 |
| Lucas Andriani Theiss | movimentacoes_usuarios.xlsx | A05, A06, X01, X02, EE21, EE13, SE04, SE13, CE04, CE12 |

Os dois AIEs e as duas CEs aparecem nos três recortes. SE04 aparece em dois. A repetição é intencional para as entregas individuais; não some 52 + 50 + 52 para obter o tamanho do sistema. O catálogo completo possui 282 PFB e 298,92 PFA.

## 6. As 12 abas, uma por uma

### Aba 1 — Contribuição

Identifica o integrante e resume as funções atribuídas. A lista distingue tipo e peso; o total bruto está em D17. As duas linhas de continuação das EEs valem zero para não contar a mesma operação novamente. Na parte inferior, B20:B25 apenas trazem os resultados da aba NI e estimativas.

**Na impressão de referência:** página(s) 1–2.

### Aba 2 — A01 ALI / A03 ALI / A05 ALI

Descreve o primeiro grupo lógico interno: responsabilidade de manutenção, registro lógico, campos, complexidade e peso. A imagem real mostra uma tela relacionada ao grupo, mas não substitui o dicionário de campos.

**Na impressão de referência:** página(s) 3.

### Aba 3 — A02 ALI / A04 ALI / A06 ALI

Descreve o segundo grupo lógico interno. Como o modelo tem menos linhas, alguns campos aparecem juntos separados por ponto e vírgula. A coluna D indica a quantidade de TD representados na linha.

**Na impressão de referência:** página(s) 4.

### Aba 4 — X01 AIE

Descreve fornecedores mantidos fora do Estoque Inteligente. Um cadastro externo é um AIE aqui; a operação que o consulta é outra função, a CE04.

**Na impressão de referência:** página(s) 5.

### Aba 5 — X02 AIE

Descreve ofertas mantidas pelo provedor externo. O arquivo possui preço cotado e disponibilidade armazenados. A CE12 recupera esses valores; a SE13 usa a cotação para calcular diferenças.

**Na impressão de referência:** página(s) 6.

### Aba 6 — Entradas externas

Contém duas EEs contadas e dois blocos de detalhamento. Primeiro leia finalidade, arquivos e campos; depois a tabela de TD, a lista de AR e a complexidade. Os blocos inferiores mostram detalhes das mesmas operações e acrescentam zero PF.

**Na impressão de referência:** página(s) 7–10.

### Aba 7 — SE06 SE / SE07 SE / SE04 SE

Explica a primeira saída externa: entradas, arquivos utilizados, resultados e regra que produz o dado derivado. A palavra saída não significa apenas mostrar uma tela; é necessário explicar o processamento descrito.

**Na impressão de referência:** página(s) 11.

### Aba 8 — SE02 SE / SE04 SE / SE13 SE

Explica a segunda saída externa e sua própria regra de cálculo. A classificação e o peso são da função inteira, e não de cada linha que aparece no relatório.

**Na impressão de referência:** página(s) 12.

### Aba 9 — CE04 Consulta

Documenta a consulta aos fornecedores: filtro de entrada e dados recuperados na saída. Os dois prints são etapas de uma única CE. A classificação separa entrada e saída conforme a convenção adotada nos slides.

**Na impressão de referência:** página(s) 13.

### Aba 10 — CE12 Consulta

Documenta a consulta às ofertas: filtros por GTIN e fornecedor e resultado com os dados externos. GTIN e fornecedor aparecem nos dois lados, mas não são contados duas vezes no total documentado.

**Na impressão de referência:** página(s) 14.

### Aba 11 — Critérios e fontes

Apresenta a fronteira, as convenções e as matrizes de complexidade. A matriz cruza a quantidade de campos com a quantidade de RL ou AR; depois o tipo da função determina o peso. Não é a tabela do NI.

**Na impressão de referência:** página(s) 15–16.

### Aba 12 — NI e estimativas

Concentra o ajuste e o planejamento: PFB, soma das 14 notas, FA, PFA, premissas de capacidade/produtividade e cálculos de horas, dias e reais. Taxas como 10 h/PFA e R$ 25/h são hipóteses de planejamento.

**Na impressão de referência:** página(s) 17–19.

## 7. As 19 páginas da impressão

Este mapa corresponde à renderização conferida no LibreOffice das planilhas atuais. A contagem de páginas pertence à impressão do Excel, não à paginação deste guia. O modelo original mantém blocos que continuam na página seguinte; não trate uma continuação como nova função.

| Página | O que aparece | Como explicar |
| --- | --- | --- |
| 1 | Identificação e tabela de funções | Nome do integrante, códigos, tipos, pesos e soma do PFB. |
| 2 | Resumo do ajuste e estimativas | NI, FA, PFA, esforço, prazo e custo referenciados da última aba. |
| 3 | Primeiro ALI | A01 para Artur, A03 para Cristhian, A05 para Lucas. |
| 4 | Segundo ALI | A02 para Artur, A04 para Cristhian, A06 para Lucas. |
| 5 | X01 — Fornecedores | Arquivo externo, campos, RL, classificação e print real da consulta. |
| 6 | X02 — Ofertas | Arquivo externo de ofertas, campos, RL e print real. |
| 7 | Início da aba de EEs | Primeira EE e início do bloco seguinte, conforme a quebra do modelo. |
| 8 | Continuação das EEs | Segunda EE e início dos detalhes; acompanhe o código no título do bloco. |
| 9 | Detalhes das mesmas EEs | Campos, referências e telas; não acrescentam novas funções. |
| 10 | Fim dos detalhes de EE | Continuação das referências e zero PF adicionais. |
| 11 | Primeira SE | SE06 para Artur, SE07 para Cristhian, SE04 para Lucas. |
| 12 | Segunda SE | SE02 para Artur, SE04 para Cristhian, SE13 para Lucas. |
| 13 | CE04 | Consulta a fornecedores: filtros e dados recuperados. |
| 14 | CE12 | Consulta a ofertas: filtros, resultado e contagem sem repetir campos. |
| 15 | Critérios e primeiras matrizes | Fronteira, convenções, matrizes de ALI/AIE e início de EE. |
| 16 | Continuação das matrizes | Conclusão dos pesos de EE e matrizes/pesos de SE e CE. |
| 17 | Resumo de tamanho e notas de NI | PFB, NI, FA, PFA e início das características gerais. |
| 18 | Fim das notas e premissas | Características restantes e parâmetros de horas, pessoas, valor-hora e reserva; o ponto da quebra pode variar. |
| 19 | Resultados do planejamento | Horas produtivas, esforço, prazo e custos. Leia as unidades ao lado de cada resultado. |

## 8. Como ler uma ficha de dados (ALI/AIE)

Comece pelo título e pela justificativa: quem mantém esse grupo? Depois leia o registro lógico, os campos, os tipos e a quantidade representada por linha. Por último, confira RL, TD, complexidade e PF.

Nas colunas da tabela: A identifica o registro lógico, B lista os campos, C informa tipos como String/Decimal/Date/Boolean e D informa quantos TD existem naquela linha. Por causa do espaço fixo do modelo, uma linha pode listar dois ou três campos separados por ponto e vírgula. Isso não os transforma em um campo composto.

Os zeros nas linhas não utilizadas são espaçadores de contagem, não valores reais do cadastro. Um grupo com 1 RL e 11 TD não possui necessariamente 11 registros: possui 11 tipos de informação na ficha.

## 9. Como ler uma ficha de transação (EE/SE/CE)

Leia nesta ordem: intenção da operação; entrada; arquivos utilizados; processamento; saída; contagem. A tabela de TD descreve o que atravessa a interface. A lista de AR descreve os grupos lógicos envolvidos. São contagens diferentes.

Exemplo EE04: entram nome, marca e categoria. A contagem inclui também acionamento e mensagem de resultado, totalizando 5 TD. São referenciados A01 e A09: 2 AR. Na matriz de EE, 2 AR e 5 TD resultam em complexidade moderada e 4 PF.

Exemplo EE10: entram produto e posição. Não entra quantidade; o vínculo nasce com zero. A quantidade é mantida pelas movimentações. A análise considera A02, A03, A04 e A09: 4 AR; os dois campos mais os dois controles formam 4 TD; o peso é 4 PF.

Para uma SE, mostre a conta que cria a informação: saldo somado, valor multiplicado, quantidade de operações ou diferença de preços. Para uma CE, mostre o filtro e os campos devolvidos sem cálculo novo. Uma EE pode fazer contas internamente e continuar EE, porque a classificação considera a intenção principal.

## 10. Como a complexidade vira pontos

| Tipo | Simples | Moderada | Complexa |
| --- | --- | --- | --- |
| ALI | 7 | 10 | 15 |
| AIE | 5 | 7 | 10 |
| EE | 3 | 4 | 6 |
| SE | 4 | 5 | 7 |
| CE | 3 | 4 | 6 |

Primeiro use a matriz de complexidade adequada. Para ALI/AIE, cruze RL e TD; para transações, cruze AR e TD. Só então aplique o peso da linha do tipo. No trabalho, a CE é avaliada separando entrada e saída e escolhendo a maior complexidade, com pesos de CE.

Exemplo: 1 RL e 5 TD é simples tanto para A01 quanto para X01. Mesmo assim, A01 é ALI e vale 7 PF, enquanto X01 é AIE e vale 5 PF. Os campos sozinhos não determinam o peso.

## 11. Como ler os cálculos finais

PFB soma os pesos das funções. NI soma as 14 notas de influência. FA = 0,65 + 0,01 × NI. PFA = PFB × FA. Neste projeto, NI = 41 e FA = 1,06: o ajuste aumenta o tamanho bruto em 6%.

| Recorte | PFB | PFA | Esforço | Prazo | Custo |
| --- | --- | --- | --- | --- | --- |
| Artur | 52 | 55,12 | 551,20 h | 92 dias úteis | R$ 13.780,00 |
| Cristhian | 50 | 53,00 | 530,00 h | 89 dias úteis | R$ 13.250,00 |
| Lucas | 52 | 55,12 | 551,20 h | 92 dias úteis | R$ 13.780,00 |

As premissas são 10 horas/PFA, uma pessoa por recorte, 8 horas de jornada × 75% = 6 horas produtivas/dia, R$ 25/h, reserva zero e custos adicionais zero. São hipóteses didáticas, não produtividade medida ou preços de mercado.

Para Artur: 55,12 × 10 = 551,20 horas; 551,20 ÷ 6 = 91,866... dias, arredondados para 92; 551,20 × 25 = R$ 13.780,00. Para Cristhian: 53 × 10 = 530 horas; 530 ÷ 6 = 88,333... dias, arredondados para 89; 530 × 25 = R$ 13.250,00.

Não confunda complexidade funcional com NI. A complexidade define o peso de cada função. O NI avalia características da aplicação como comunicação, atualização on-line e facilidade de mudanças. Os três recortes usam o mesmo NI porque analisam a mesma aplicação.

## 12. Outras palavras e siglas que aparecem

| Termo | Explicação |
| --- | --- |
| SKU | Código interno que identifica o produto ou sua variação no estoque. |
| GTIN | Identificador comercial global de um item; é usado no cruzamento com o código de barras cadastrado. |
| IoT | Internet das Coisas: integração com equipamentos, como sinalização e balança. |
| HTTP | Protocolo usado na comunicação web e nas consultas aos provedores externos. |
| MQTT | Protocolo de mensagens usado na integração com dispositivos. |
| QoS | Qualidade de serviço da entrega de mensagens MQTT; é uma configuração técnica. |
| CLI | Interface de linha de comando; forma de manter os dados dos provedores externos. |
| CRUD | Criar, consultar, alterar e excluir dados. |
| Hash da senha | Representação técnica usada para verificar a senha sem armazená-la como texto legível. |
| Tenant / loja | Contexto que separa dados e permissões de cada loja. |
| SLA | Acordo ou requisito formal de nível de serviço, como tempo de resposta. |
| COUNT / SUM | Operações de contar registros e somar valores. |
| h/PFA | Horas de esforço assumidas para cada ponto de função ajustado. |
| Horas-pessoa | Quantidade de trabalho: uma pessoa trabalhando uma hora corresponde a uma hora-pessoa. |
| Idempotência | Proteção para não executar duas vezes a mesma operação quando uma requisição se repete. |
| Auditoria | Registro consultável de quem realizou uma ação, qual foi a ação e quando ocorreu. |
| String / Decimal / Date / Boolean | Texto / número decimal / data / verdadeiro ou falso; são tipos dos dados. |
| IFPUG | International Function Point Users Group; organização associada ao método de pontos de função tratado no material. |

## 13. Como interpretar os prints reais

Todas as 42 posições de imagem das planilhas contêm screenshots reais do navegador, com o sistema local funcionando e dados de demonstração. Há 29 arquivos de captura porque algumas telas são compartilhadas entre recortes. O fato de os dados serem demonstrativos não torna a captura uma simulação visual.

Nas EEs, os prints mostram formulários, campos expandidos e registros existentes; nenhum cadastro ou movimento foi enviado para produzir as capturas. Nas CEs, os filtros foram preenchidos e a consulta foi executada: a primeira imagem mostra a entrada, e a segunda, o resultado. Na imagem de entrada, a tabela ainda pode ser a consulta anterior.

Uma captura não precisa exibir cada campo interno ou técnico. As tabelas de dados e referências documentam esses campos. O print da consulta externa também não prova que o Estoque Inteligente mantém o arquivo externo.

## 14. Dúvidas comuns na apresentação

**Por que a aba de EEs tem quatro blocos?** O modelo tinha quatro espaços. Dois descrevem as duas EEs contadas; dois detalham as mesmas funções. Estes últimos valem zero PF adicionais.

**Por que só há dois ALIs detalhados, mas uma EE cita mais arquivos?** Porque a entrega é um recorte. Os arquivos de apoio entram na quantidade de AR da transação, sem serem acrescentados automaticamente como novas funções de dados atribuídas ao integrante.

**Por que SE06 é pesquisa, mas foi classificada como saída?** Porque calcula saldo somando quantidades. O nome da tela não determina a classificação.

**Por que CE12 não é a mesma coisa que SE13?** CE12 recupera cotações; SE13 compara a cotação com o preço interno e calcula diferenças. As intenções e os processamentos são diferentes.

**Tenho que decorar as letras e números?** Entenda o prefixo e use a tabela do catálogo para localizar a função. Explique o significado e as referências; os números são identificadores.

**O que falta preencher pessoalmente?** Cada integrante deve confirmar a contribuição efetiva na identificação das EEs. O documento não inventa autoria a partir da divisão dos temas.

## 15. Fontes e leitura recomendada dentro do projeto

Este guia foi conferido contra os três Excel finais, os roteiros individuais, docs/apf/contagem.json, docs/apf/resultados.json, docs/apf/NI.md e os registros de captura/validação. Para regras e matrizes da disciplina, consulte docs/pdfs. Há uma divergência nos slides da Parte IV: foi adotado 0,65 da página 11 para o FA; a página 15 apresenta 0,35 e limites inconsistentes, já registrados nas planilhas.

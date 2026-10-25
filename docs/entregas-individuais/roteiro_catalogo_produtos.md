# Roteiro de apresentação — Arthur Ribeiro

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

**Os protótipos são capturas reais?** Não. São imagens ilustrativas de baixa fidelidade baseadas nas funções implementadas. Mostram campos, interação e resultados esperados; os valores ilustrativos não devem ser apresentados como dados de produção.

**Como os cálculos foram conferidos?** Os três Excel têm 80 fórmulas cada, cujos 240 resultados coincidiram com recálculo independente no LibreOffice. Cada documento foi inspecionado em 28 páginas, com 12 protótipos completos. A validação da aplicação registrou 90 testes Java aprovados e 13 verificações HTTP aprovadas.

## 12. Ordem sugerida para apresentar

1. Abra a capa, diga seu nome e delimite o recorte.
2. Explique os dois ALIs e seus RLs/campos, mostrando os protótipos.
3. Explique os dois AIEs e quem mantém os dados.
4. Apresente as EEs, suas referências e sua contribuição real.
5. Nas SEs, faça o exemplo de cálculo e aponte o dado derivado.
6. Nas CEs, mostre primeiro a entrada e depois a saída.
7. Mostre a soma dos pesos, NI, FA e PFA.
8. Explique as premissas de esforço, prazo e custo.

Fontes locais deste roteiro: planilha individual, docs/apf/contagem.json, docs/apf/resultados.json, docs/apf/NI.md e registro de validação. Os exemplos ilustrativos não alteram a contagem nem os arquivos Excel.

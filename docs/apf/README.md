# Entrega de APF — sistema e recortes individuais

Atualização de 24/10/2026: a base coletiva foi ampliada para atender aos três recortes individuais de `divisao_entregas_individuais.pdf`. Cada integrante apresenta dois ALIs, os dois AIEs compartilhados, duas EEs identificadas, duas SEs e duas CEs. O código acrescenta X02 (ofertas externas), CE12 (consulta de ofertas) e SE13 (comparação de preços).

As [três planilhas individuais](../entregas-individuais/README.md) contêm capa, nomes, protótipos incorporados, contagens e estimativas. O PDF de divisão é o plano anterior e ainda chama X02/CE12/SE13 de propostas; consulte as planilhas e a contagem atual para o estado implementado. A declaração de contribuição real ainda deve ser confirmada por cada integrante.

## O que foi aproveitado e acrescentado

| Exigência | Atendimento |
|---|---|
| ALI, itens de dados e registros lógicos | 9 ALIs existentes, descritos em FICHAS.md |
| AIE | 2 grupos externos demonstrativos: fornecedores (5 TD, 1 RL, 5 PF) e ofertas (9 TD, 1 RL, 5 PF) |
| EE: incluir, alterar e excluir | Cadastros existentes; exemplo para apresentação: modelos (EE04, EE05, EE06) |
| Pelo menos 5 SEs com dados derivados | 3 existentes + 2 novas, detalhadas abaixo |
| 4 CEs, cada uma com entrada e saída | 3 existentes + consulta de fornecedores, detalhadas abaixo |
| PFs de todas as funções atuais | FICHAS.md, contagem.json e CALCULOS.md |
| NI e 14 justificativas | NI.md |
| PFB, FA, PFA, esforço, prazo e custo | Cálculos abaixo e resultados.json |
| Explicação para apresentação | explicacao.pdf |

Foram retirados os acréscimos de saldo consolidado, relatório adicional de ocupação, curva ABC e consultas adicionais de produtos, posições e movimentos.
Não foram removidas funcionalidades originais. Não foram criados novos ALIs nem EEs: já existiam.

## Saídas derivadas disponíveis

| ID / função | Origem e acesso | ALIs | Por que é dado derivado? | PF |
|---|---|---|---|---:|
| SE06 - Pesquisa de estoque | Existente: `/`, busca de produto | A01, A02, A03, A04 | Soma as quantidades das posições para obter saldo total | 7 |
| SE07 - Mapa de ocupação | Existente: `/mapa` ou `/ocupacao` | A01, A02, A03, A04 | Soma ocupação por posição e compara com a capacidade | 7 |
| SE08 - Peso líquido e subtotal | Existente: baixa rápida de produto em kg, integração com balança | A02, A08 | Peso líquido = bruto - tara; subtotal = líquido × preço | 5 |
| SE02 - Valor do estoque | Nova: `/relatorios?tipo=valor` | A02, A04 | Saldo × preço atual por produto e soma monetária da loja | 5 |
| SE04 - Movimentações no período | Nova: `/relatorios?tipo=fluxo` | A02, A05 | Conta operações e soma variações por produto e tipo | 5 |
| SE13 - Comparação de preços | Nova: `/comparacao-precos` | A02 e AIE X02 | Diferença monetária e percentual entre cotação e preço interno | 5 |

As seis saídas calculadas somam 34 PF. A exportação preexistente do histórico (SE09, 7 PF) permanece na contagem completa: **7 SEs, 41 PF**. Sua atualização de auditoria justifica SE, mas ela não é usada para completar as saídas calculadas dos recortes individuais.

Itens utilizados: pesquisa e mapa leem identificação de modelo/produto/posição, quantidade e capacidade; peso usa produto, preço, configuração e telemetria; valor usa SKU, unidade, preço e quantidade; fluxo usa SKU, unidade, tipo, data e variação. Entradas, saídas e referências completas estão nas fichas individuais.
Para demonstrar a balança, use a loja `granel`, um produto em kg e a leitura recente do simulador MQTT. Não se exige comprar hardware. A telemetria transitória não é contada como AIE.
O valor do estoque usa preço de venda cadastrado, não custo histórico. O fluxo inclui os dias inteiros no horário de Brasília e separa estornos, respeitando seus sinais. Quantidades em kg e unidades não são somadas entre produtos.

## Consultas disponíveis para apresentação

| ID / função | Origem e entrada | Saída (itens recuperados) | Arquivos | PF |
|---|---|---|---|---:|
| CE05 - Modelos | Existente: `/modelos?id=...`; seleção do modelo | Nome, marca e categoria | A01 | 3 |
| CE07 - Equipe e permissões | Existente: `/usuarios?id=...`; seleção do usuário | Nome de usuário, perfil e permissões | A06, A07 | 3 |
| CE08 - Lojas | Existente: `/global?id=...`; seleção da loja, com acesso global | Identificador público, nome e recursos habilitados | A08 | 3 |
| CE04 - Fornecedores | Nova: `/fornecedores?q=...`; filtro de código, nome ou documento | Código, nome, documento, e-mail e telefone | X01 (AIE) | 3 |
| CE12 - Ofertas | Nova: `/ofertas?gtin=...&fornecedor=...`; filtros exatos | Nove campos da oferta, incluindo cotação e disponibilidade | X02 (AIE) | 3 |

Cada linha é uma função com entrada e saída; não são oito funções. A consulta por GET apenas recupera dados. Salvar ou excluir pelo formulário é uma EE separada. Use uma conta autorizada para cada tela; a consulta global exige administração global.
Essas cinco somam 15 PF. As demais consultas já existentes continuam catalogadas porque a medição abrange a aplicação atual: **9 CEs, 33 PF**. Os recortes individuais usam CE04 e CE12.

## ALI, AIE e EE

Os ALIs são Modelos, Produtos, Posições, Estoque por posição, Movimentações, Usuários, Permissões, Lojas/configuração e Auditoria: um registro lógico por grupo e 63 PF no total. Registro lógico é um subgrupo de dados, não a quantidade de linhas cadastradas.
O cadastro de fornecedores pertence ao provedor independente em `external/fornecedores`. É mantido por essa aplicação e somente lido pelo estoque. Seus cinco itens são código, nome, documento, e-mail e telefone: 1 RL e 5 PF. A manutenção do provedor fica fora da contagem do estoque.
As 28 EEs existentes somam 135 PF. Para evidenciar incluir/alterar/excluir, basta demonstrar o cadastro de modelos. As fichas identificam os ALIs e dados de cada EE, inclusive a auditoria mantida pelas operações.

## Critérios de contagem

1. Arquivos lógicos são grupos de negócio, não contagem automática de tabelas.
2. Registros lógicos são tipos/subgrupos, não a quantidade de registros no banco.
3. Cada campo repetido em várias linhas é contado uma vez por tipo. Grade não multiplica PF por tamanho.
4. Campos técnicos de CSRF, sessão, hash de requisição, versão de autenticação e idempotência não são itens de negócio.
5. O conjunto de comandos da transação vale um item de controle. O campo de feedback vale um item, independentemente dos textos alternativos de erro/sucesso. Essa convenção está explícita em cada ficha; não se contam traduções ou cada texto alternativo como campos independentes.
6. A validação técnica transversal de sessão/permissão não adiciona automaticamente A06/A07/A08 a todas as funções; referências de negócio explícitas estão nas fichas. Auditoria persistente consultável é contada quando mantida.
7. Para CE, conforme a Parte III do material, avaliam-se entrada e saída separadamente e adota-se o maior resultado. Dados repetidos são removidos da saída para não duplicar. Os arquivos referenciados pela função são considerados em ambas as avaliações, sem duplicá-los no total da função.
8. PDFs e CSVs da mesma exportação não geram duas funções. A exportação antiga é SE por atualizar a auditoria; as cinco saídas selecionadas (SE02, SE04, SE06, SE07 e SE08) atendem à exigência específica de dados derivados.
9. Endpoints de saúde, polling de confirmação/status MQTT, paginação, seletor de tema e formulários auxiliares de uma operação não são processos de negócio independentes nesta contagem. A confirmação integra o comando de sinalização.
10. A tela de histórico contém consulta de movimentos e consulta de auditoria; são grupos consultáveis com objetivos distintos. Não contar o container HTML como uma terceira função.

O exemplo de EE do professor soma duas mensagens específicas. Aqui existe um único campo
de feedback reutilizado, contado uma vez. Caso a disciplina exija contar cada condição
de erro separadamente mesmo no mesmo campo, essa convenção deve ser alterada nas fichas
antes de comparar números; não é uma alteração na funcionalidade do sistema.

### Matrizes utilizadas

ALI/AIE: colunas TD 1–19 / 20–50 / 51+; linhas RL 1 / 2–5 / 6+:
`S S M; S M C; M C C`. Pesos ALI: 7/10/15; AIE: 5/7/10.

EE e entrada de CE: colunas TD 1–4 / 5–15 / 16+; linhas AR 0–1 / 2 / 3+:
`S S M; S M C; M C C`. Pesos EE/CE: 3/4/6.

SE e saída de CE: colunas TD 1–5 / 6–19 / 20+; linhas AR 1 / 2–3 / 4+:
`S S M; S M C; M C C`. Pesos SE: 4/5/7; saída CE: 3/4/6.

## Cálculos e estimativa

| Tipo | Quantidade | PF |
|---|---:|---:|
| ALI | 9 | 63 |
| AIE | 2 | 10 |
| EE | 28 | 135 |
| SE | 7 | 41 |
| CE | 9 | 33 |
| **Total** | **55** | **282** |

```text
PFB = 63 + 10 + 135 + 41 + 33 = 282
NI = soma das 14 notas justificadas em NI.md = 41
FA = 0,65 + 0,01 × 41 = 1,06
PFA = 282 × 1,06 = 298,92
Esforço = 298,92 × 10 horas/PF = 2.989,20 horas-pessoa
Capacidade = 3 integrantes × 6 horas produtivas/dia = 18 horas-pessoa/dia
Prazo = 2.989,20 / 18 = 166,07 dias úteis; arredondado: 167 dias úteis
Custo = 2.989,20 × R$ 25,00/hora = R$ 74.730,00
```

As notas de NI foram reavaliadas e mantidas: a remoção das consultas e relatórios excedentes não altera os protocolos, a distribuição IoT nem os demais fatores justificados em NI.md.
A fórmula do FA usa a constante 0,65 da Parte IV, página 11. A constante 0,35 da página 15 é inconsistente com o exemplo da própria aula.
10 horas/PF, 6 horas produtivas/dia e R$ 25/h são premissas acadêmicas, não medição histórica da equipe. A estimativa mede desenvolver a aplicação inteira, não o tempo restante para esta entrega. Considera capacidade constante e não inclui infraestrutura, impostos ou contingência.

## Executar, demonstrar e conferir

```powershell
docker compose --env-file .env.demo up -d --build --wait
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/calcular-apf.ps1
```

Use `http://localhost:8080` e as credenciais documentadas no README principal. Menus: **Fornecedores externos**, **Ofertas externas**, **Comparação de preços** e **Relatórios gerenciais** (valor/fluxo). A comparação exige permissão de relatórios e recurso `reports` habilitado.
Apresente o CRUD de modelos, as cinco SEs e as quatro CEs indicadas nas tabelas. Mostre o dicionário de dados, as fichas e os cálculos. As permissões e os recursos habilitados de cada loja continuam sendo respeitados.
O [provedor](../../external/fornecedores/README.md) documenta como manter os fornecedores fora do estoque.

Os testes são executados com JDK 21; as integrações exigem PostgreSQL e MQTT. Consulte [VALIDACAO.md](VALIDACAO.md) para as verificações realizadas. IDs das fichas foram preservados; os intervalos correspondem às funções novas removidas.

## Material-base

- [Parte I — ALI/AIE](../../pdfs/PF%20-%20Parte%20I.pdf), tabelas das páginas 19 e 28.
- [Dicas de ALI](../../pdfs/PF%20-%20Parte%20I%20-%20Dicas%20ALI.pdf), agrupamento lógico.
- [Parte II — EE](../../pdfs/PF%20-%20Parte%20II%20-%20EE.pdf), tabela da página 9.
- [Dicas de EE](../../pdfs/PF%20-%20Parte%20II%20-%20Dicas%20EE.pdf), transações de manutenção.
- [Parte II — SE](../../pdfs/PF%20-%20Parte%20II%20-%20SE.pdf), tabela da página 9.
- [Parte III — CE](../../pdfs/PF%20-%20Parte%20III%20-%20CE.pdf), páginas 8–13.
- [Parte IV — cálculos](../../pdfs/PF%20-%20Parte%20IV%20-%20V2.pdf), páginas 11 e 14.
- [Características gerais](../../pdfs/Características%20Gerais%20do%20Software.pdf).

Os nomes dos três integrantes não foram informados e nenhuma contribuição individual
foi inventada. O catálogo e as justificativas estão disponíveis para revisão da equipe.

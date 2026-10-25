# Fichas de contagem funcional

Escopo atualizado em 24/10/2026: além de SE02, SE04 e CE04, foram implementados X02, CE12 e SE13 para os três recortes individuais. IDs preservados para rastreabilidade.

Fonte editável: [contagem.json](contagem.json). Pontuações e complexidades: [CALCULOS.md](CALCULOS.md).

Os itens abaixo são tipos de dados, não quantidades de linhas. Referências A01–A09, X01 e X02 são resolvidas no dicionário. As fichas identificam dados que cruzam a fronteira; leituras internas para validação são descritas separadamente e não duplicam esses itens.

## Acréscimos para os recortes individuais

### X02 — Ofertas externas (AIE)

- Origem: `external/ofertas`, provedor independente com manutenção CLI e consulta HTTP.
- Um registro lógico: oferta comercial externa.
- Nove TD: GTIN, código do fornecedor, SKU externo, descrição, marca, unidade, preço cotado, disponibilidade e data de atualização.
- Não se confunde com X01: este identifica o fornecedor; X02 registra suas ofertas. O estoque não mantém nenhum dos dois grupos.
- 1 RL e 9 TD: simples, 5 PF. Protótipo: `/ofertas`, incorporado nas três planilhas.

### CE12 — Consultar ofertas externas

- Referência: X02. Entrada: GTIN e código do fornecedor opcionais, com correspondência exata; ambos preenchidos são combinados por E.
- Saída: os nove TD de X02. Mensagem de ausência/validação e conjunto de comandos são controles.
- Entrada: 4 TD e 1 AR; saída sem repetir GTIN/fornecedor: 7 TD e 1 AR. Ambas simples; maior complexidade simples, 3 PF.
- Recupera dados sem derivação, sem alterar ALIs/AIEs. Falhas do provedor geram erro, não lista fictícia ou vazia.
- Evidência: `OfertaController`, `OfertaService.consultar`, `OfertaGateway`, template `ofertas.html`.

### SE13 — Comparar preços internos e ofertas externas

- Referências: A02 e X02. A02 fornece loja/ativo, código de barras, SKU, unidade e preço. X02 fornece GTIN, fornecedor, SKU externo, unidade, cotação e atualização; os demais campos recebidos são validados pelo cliente.
- Entrada: GTIN e código do fornecedor. Saída: GTIN, SKU interno, código do fornecedor, SKU externo, unidade, preço interno, preço cotado, diferença monetária, diferença percentual e data de atualização.
- Dois controles: acionamento e mensagem de resultado. Dados de entrada repetidos na saída contam uma vez: 12 TD distintos, 2 AR, moderada, 5 PF.
- Diferença = cotação − preço interno; percentual = diferença / preço interno × 100. Duas casas decimais. Preço interno zero: percentual não aplicável.
- Apenas produtos ativos da loja autenticada, com mesmo GTIN e unidade. Ofertas sem correspondência ou unidade compatível não entram. Cotação não é custo histórico; diferença não é margem de lucro.
- Evidência: `/comparacao-precos`, `OfertaService.comparar`, template `ofertas.html`. Protótipo incorporado à entrega de Lucas.

## Dicionário de ALIs e AIE

### A01 — Modelos de produto (ALI)

- Origem: `models`.
- Registro lógico: Registro de modelos de produto. Total: 1.
- Itens de dados (5): loja; código; nome; marca; categoria.
- Manutenção: dentro do Estoque Inteligente, pelos processos de cadastro ou movimentação identificados nas fichas.

### A02 — Produtos (ALI)

- Origem: `products`.
- Registro lógico: Registro de produtos. Total: 1.
- Itens de dados (11): loja; código; modelo; SKU; código de barras; cor; tamanho; unidade; preço; características; ativo.
- Manutenção: dentro do Estoque Inteligente, pelos processos de cadastro ou movimentação identificados nas fichas.

### A03 — Posições (ALI)

- Origem: `positions`.
- Registro lógico: Registro de posições. Total: 1.
- Itens de dados (11): loja; identificador; código; setor; linha; coluna; capacidade; unidade; dispositivo; pino; ativo.
- Manutenção: dentro do Estoque Inteligente, pelos processos de cadastro ou movimentação identificados nas fichas.

### A04 — Estoque por posição / vínculos (ALI)

- Origem: `occupancy`.
- Registro lógico: Registro de estoque por posição / vínculos. Total: 1.
- Itens de dados (4): loja; produto; posição; quantidade.
- Manutenção: dentro do Estoque Inteligente, pelos processos de cadastro ou movimentação identificados nas fichas.

### A05 — Movimentações (ALI)

- Origem: `movements`.
- Registro lógico: Registro de movimentações. Total: 1.
- Itens de dados (12): loja; número; produto; posição; responsável; tipo; variação; saldo anterior; saldo posterior; motivo; data; movimento original.
- Manutenção: dentro do Estoque Inteligente, pelos processos de cadastro ou movimentação identificados nas fichas.

### A06 — Usuários (ALI)

- Origem: `users`.
- Registro lógico: Registro de usuários. Total: 1.
- Itens de dados (6): loja; código; nome de usuário; senha; perfil; ativo.
- Manutenção: dentro do Estoque Inteligente, pelos processos de cadastro ou movimentação identificados nas fichas.

### A07 — Permissões por perfil (ALI)

- Origem: `role_permissions`.
- Registro lógico: Registro de permissões por perfil. Total: 1.
- Itens de dados (3): loja; perfil; permissão.
- Manutenção: dentro do Estoque Inteligente, pelos processos de cadastro ou movimentação identificados nas fichas.

### A08 — Lojas e configuração (ALI)

- Origem: `tenants`.
- Registro lógico: Registro de lojas e configuração. Total: 1.
- Itens de dados (12): código; identificador público; nome; IoT; relatórios; ocupação; fracionados; host MQTT; porta MQTT; QoS; tempo LED; ativo.
- Manutenção: dentro do Estoque Inteligente, pelos processos de cadastro ou movimentação identificados nas fichas.

### A09 — Auditoria consultável (ALI)

- Origem: `audit`.
- Registro lógico: Registro de auditoria consultável. Total: 1.
- Itens de dados (6): loja; número; responsável; ação; detalhe; data.
- Manutenção: dentro do Estoque Inteligente, pelos processos de cadastro ou movimentação identificados nas fichas.

### X01 — Fornecedores externos (AIE)

- Origem: `external/fornecedores`.
- Registro lógico: Registro de fornecedores externos. Total: 1.
- Itens de dados (5): código; nome; documento; e-mail; telefone.
- Manutenção: somente pelo provedor externo; o Estoque Inteligente consulta por HTTP.

## EE

### EE01 — Incluir produto, inclusive grade e saldo inicial

- Arquivos referenciados: A01, A02, A03, A04, A05, A09.
- Entrada: nome; marca; categoria; SKU; código de barras; cor; tamanho; unidade; preço; características; estrutura; tamanhos da grade; posição inicial; quantidades iniciais.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `ProdutoService.salvar`.
- Dados internos lidos/mantidos: A01: nome, marca e categoria do modelo. A02: modelo, SKU, código de barras, cor, tamanho, unidade, preço, características e ativo. A03: posição e unidade, na entrada inicial. A04: produto, posição e quantidade inicial. A05: produto, posição, responsável, tipo, variação, saldos anterior/posterior, motivo e data, quando há saldo inicial. A09: loja, responsável, ação, detalhe e data (gerada). Identificações dos registros são geradas pelo banco; leituras/manutenções internas não são TD adicionais da interface.

### EE02 — Alterar produto

- Arquivos referenciados: A01, A02, A04, A09.
- Entrada: produto; nome; marca; categoria; SKU; código de barras; cor; tamanho; unidade; preço; características.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `ProdutoService.salvar`.
- Dados internos lidos/mantidos: A01: modelo; A02: unidade anterior; A04: vínculos para validar mudança de unidade; A09: registro da alteração.

### EE03 — Excluir produto logicamente

- Arquivos referenciados: A02, A04, A09.
- Entrada: produto.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `ProdutoService.excluir`.
- Dados internos lidos/mantidos: A02: ativo; A04: quantidade e vínculos removidos se vazios; A09: registro da exclusão.

### EE04 — Incluir modelo

- Arquivos referenciados: A01, A09.
- Entrada: nome; marca; categoria.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `ModeloProdutoService.salvar`.
- Dados internos lidos/mantidos: A01: loja, nome, marca e categoria; identificação do modelo gerada no cadastro. A09: loja, responsável, ação modelo.salvar, detalhe com a identificação do modelo e data gerada. Esses dados de auditoria são mantidos internamente e não são campos extras recebidos pela interface.

### EE05 — Alterar modelo

- Arquivos referenciados: A01, A09.
- Entrada: modelo; nome; marca; categoria.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `ModeloProdutoService.salvar`.
- Dados internos lidos/mantidos: A01: código e cadastro anterior; A09: registro de alteração.

### EE06 — Excluir modelo

- Arquivos referenciados: A01, A02, A09.
- Entrada: modelo.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `ModeloProdutoService.excluir`.
- Dados internos lidos/mantidos: A02: referência ao modelo bloqueia exclusão; A01: código; A09: registro da exclusão.

### EE07 — Incluir posição

- Arquivos referenciados: A03, A09.
- Entrada: código; setor; linha; coluna; capacidade; unidade; dispositivo; pino.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `PosicaoService.salvar`.
- Dados internos lidos/mantidos: A03: loja, código, setor, linha, coluna, capacidade, unidade, dispositivo, pino e ativo; identificação gerada no cadastro. A09: loja, responsável, ação posicao.salvar, detalhe com a identificação da posição e data gerada. Auditoria é manutenção interna, sem TD extras na interface.

### EE08 — Alterar posição

- Arquivos referenciados: A03, A04, A09.
- Entrada: posição; código; setor; linha; coluna; capacidade; unidade; dispositivo; pino.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `PosicaoService.salvar`.
- Dados internos lidos/mantidos: A03: unidade anterior; A04: vínculos existentes; A09: registro da alteração.

### EE09 — Excluir posição logicamente

- Arquivos referenciados: A03, A04, A09.
- Entrada: posição.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `PosicaoService.excluir`.
- Dados internos lidos/mantidos: A03: ativo; A04: quantidade e vínculos vazios; A09: registro da exclusão.

### EE10 — Incluir vínculo

- Arquivos referenciados: A02, A03, A04, A09.
- Entrada: produto; posição.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `VinculoService.salvar`.
- Dados internos lidos/mantidos: A02: identificação do produto, unidade e ativo. A03: identificação da posição, unidade e ativo. A04: loja, produto, posição e quantidade inicial zero. A09: loja, responsável, ação vinculo.criar, detalhe produto:posição e data gerada. O usuário informa somente produto/posição; quantidade não é entrada desta EE.

### EE11 — Alterar vínculo vazio

- Arquivos referenciados: A02, A03, A04, A09.
- Entrada: produto original; posição original; produto destino; posição destino.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `VinculoService.editar`.
- Dados internos lidos/mantidos: A02/A03: unidades do destino; A04: saldo original e chave do destino; A09: registro.

### EE12 — Excluir vínculo vazio

- Arquivos referenciados: A02, A03, A04, A09.
- Entrada: produto; posição.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `VinculoService.salvar`.
- Dados internos lidos/mantidos: A02/A03: identificação; A04: saldo; A09: registro.

### EE13 — Incluir usuário

- Arquivos referenciados: A06, A08, A09.
- Entrada: nome de usuário; perfil; senha.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `UsuarioService.salvar`.
- Dados internos lidos/mantidos: A08: identificação da loja e ativo. A06: loja, nome de usuário, senha (persistida como hash), perfil e ativo; identificação gerada. A09: loja, responsável, ação usuario.salvar, detalhe nome/perfil e data gerada. Hash é representação técnica da senha, não um segundo TD.

### EE14 — Alterar usuário

- Arquivos referenciados: A06, A08, A09.
- Entrada: usuário; nome de usuário; perfil; senha.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `UsuarioService.salvar`.
- Dados internos lidos/mantidos: A06: perfil, senha anterior e administradores ativos; A08: loja; A09: registro.

### EE15 — Excluir usuário logicamente

- Arquivos referenciados: A06, A08, A09.
- Entrada: usuário; nome de usuário; perfil.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `UsuarioService.salvar(action=delete)`.
- Dados internos lidos/mantidos: O formulário envia nome/perfil para validação; A06: ativo/perfil/administradores; A08: loja; A09: registro.

### EE16 — Incluir loja e administrador

- Arquivos referenciados: A08, A06, A07, A09.
- Entrada: identificador público; nome; IoT; relatórios; ocupação; fracionados; usuário administrador; senha administrador.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `LojaService.salvar`.
- Dados internos lidos/mantidos: Mantém A08, cria A06, inicializa A07, registra A09.

### EE17 — Alterar loja e recursos

- Arquivos referenciados: A08, A02, A09.
- Entrada: loja; identificador público; nome; IoT; relatórios; ocupação; fracionados.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `LojaService.salvar`.
- Dados internos lidos/mantidos: A08: configuração; A02: unidade/ativo para impedir desabilitar fracionados em uso; A09: registro.

### EE18 — Excluir loja logicamente

- Arquivos referenciados: A08, A09.
- Entrada: loja.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `LojaService.excluir`.
- Dados internos lidos/mantidos: A08: nome/ativo; A09: registro.

### EE19 — Alterar matriz de permissões

- Arquivos referenciados: A07, A09.
- Entrada: perfil; permissão; concedida.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `PermissaoService.atualizar`.
- Dados internos lidos/mantidos: A07: perfil/permissão; cada par repetido é ocorrência, não novo tipo de dado; A09: registro.

### EE20 — Alterar configuração IoT

- Arquivos referenciados: A08, A09.
- Entrada: host MQTT; porta MQTT; QoS; tempo LED.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `ConfiguracaoIoTService.salvar`.
- Dados internos lidos/mantidos: A08: configuração da loja; A09: registro.

### EE21 — Registrar entrada

- Arquivos referenciados: A02, A03, A04, A05, A09.
- Entrada: produto; posição; tipo; quantidade; motivo.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `EstoqueService.movimentar`.
- Dados internos lidos/mantidos: A02: identificação, unidade e ativo do produto. A03: identificação, unidade e ativo da posição. A04: loja, produto, posição e quantidade anterior/posterior. A05: produto, posição, responsável, tipo, variação, saldo anterior, saldo posterior, motivo e data; número gerado e movimento original vazio na entrada normal. A09: loja, responsável, ação, detalhe e data. Chave técnica de idempotência excluída dos TD.

### EE22 — Registrar saída

- Arquivos referenciados: A02, A03, A04, A05, A09.
- Entrada: produto; posição; tipo; quantidade; motivo.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `EstoqueService.movimentar`.
- Dados internos lidos/mantidos: A02/A03: unidades; A04: saldo; A05: movimento e saldos; A09: registro. Chave técnica de idempotência excluída.

### EE23 — Ajustar saldo

- Arquivos referenciados: A02, A03, A04, A05, A09.
- Entrada: produto; posição; tipo; quantidade; motivo.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `EstoqueService.movimentar`.
- Dados internos lidos/mantidos: A02/A03: unidades; A04: saldo; A05: movimento e saldos; A09: registro. Chave técnica de idempotência excluída.

### EE24 — Estornar movimentação

- Arquivos referenciados: A02, A03, A04, A05, A09.
- Entrada: movimento original; motivo.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `EstoqueService.estornar`.
- Dados internos lidos/mantidos: A05: tipo/produto/posição/variação/original; A04: saldo; A02/A03: unidades; A09: registro.

### EE25 — Autenticar usuário

- Arquivos referenciados: A06, A08, A09.
- Entrada: loja; nome de usuário; senha.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `AutenticacaoService.autenticar`.
- Dados internos lidos/mantidos: A06: credencial/perfil/ativo; A08: identificador público/ativo via consulta de autenticação; A09: login. Tentativas temporárias excluídas.

### EE26 — Selecionar loja de trabalho

- Arquivos referenciados: A08, A09.
- Entrada: loja.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `LojaService.selecionar`.
- Dados internos lidos/mantidos: A08: loja selecionada; A09: registro do acesso global.

### EE27 — Sinalizar posição por LED

- Arquivos referenciados: A03, A08, A09.
- Entrada: posição.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `IoTService.sinalizar`.
- Dados internos lidos/mantidos: A03: dispositivo/pino; A08: host/porta/QoS/tempo LED; A09: registro. Intenção primária: enviar controle para ligar LED.

### EE28 — Encerrar sessão

- Arquivos referenciados: nenhum arquivo lógico; sessão temporária.
- Entrada: solicitação pelo acionamento da função; contexto da loja autenticada.
- Saída: mensagem de resultado.
- Controles contados: acionamento.
- Evidência: `SegurancaConfig /logout`.
- Dados internos lidos/mantidos: Altera o comportamento de acesso; sessão temporária não é ALI.

## SE


### SE02 — Valor potencial do estoque

- Arquivos referenciados: A02, A04.
- Entrada: solicitação pelo acionamento da função; contexto da loja autenticada.
- Saída: SKU; unidade; saldo; preço; valor por produto; total da loja.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `AnaliseService.relatorio(valor)`.
- Derivação/classificação: Saldo × preço atual, arredondado em centavos por produto; total = soma dos valores. Não é custo nem receita realizada.


### SE04 — Fluxo de movimentações

- Arquivos referenciados: A02, A05.
- Entrada: início; fim.
- Saída: SKU; unidade; tipo; número de operações; variação líquida.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `AnaliseService.relatorio(fluxo)`.
- Derivação/classificação: COUNT(*) e SUM(delta) por produto/tipo. Período inclusivo em America/Sao_Paulo; estornos em categoria própria.


### SE06 — Pesquisar estoque e localizar produto

- Arquivos referenciados: A01, A02, A03, A04.
- Entrada: busca; produto selecionado.
- Saída: SKU; código de barras; nome; marca; categoria; cor; tamanho; unidade; preço; características; saldo total; posição; setor; quantidade na posição.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `ProdutoService.pesquisar/buscar; DashboardController; BuscaAssistidaController`.
- Derivação/classificação: Soma das posições para obter saldo. Dashboard, busca digitada/voz e lista de produtos reutilizam a mesma intenção; não contar canais novamente.

### SE07 — Mapa operacional de posições

- Arquivos referenciados: A01, A02, A03, A04.
- Entrada: solicitação pelo acionamento da função; contexto da loja autenticada.
- Saída: posição; setor; linha; coluna; capacidade; unidade; ocupado; nome do produto; SKU; quantidade do vínculo; alerta de excesso.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `MapaController /mapa e /ocupacao`.
- Derivação/classificação: Soma as quantidades dos vínculos por posição e compara com a capacidade para apresentar alerta de excesso. Mapa e lista de ocupação são canais do mesmo processo.

### SE08 — Ler peso líquido e subtotal

- Arquivos referenciados: A02, A08.
- Entrada: produto; dispositivo.
- Saída: peso bruto; tara; peso líquido; subtotal; indicador de simulação.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `IoTService.peso`.
- Derivação/classificação: Peso líquido=bruto−tara; subtotal=peso líquido×preço. Telemetria transitória não é AIE.

### SE09 — Exportar histórico

- Arquivos referenciados: A01, A02, A03, A05, A06, A09.
- Entrada: tipo; início; fim; formato.
- Saída: número; data; SKU; posição; variação; responsável; motivo.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `RelatorioService.exportar; RelatorioCsv; RelatorioPdf`.
- Derivação/classificação: SE por atualizar A09 com a exportação; não há cálculo financeiro no arquivo. CSV/PDF são formatos do mesmo processo. As saídas SE02, SE04, SE06, SE07 e SE08 demonstram os cinco dados derivados exigidos.

## CE




### CE04 — Consultar fornecedores externos

- Arquivos referenciados: X01.
- Entrada: filtro código, nome ou documento.
- Saída: código; nome; documento; e-mail; telefone.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `GET /fornecedores?q=; AnaliseService.consultar; FornecedorGateway`.
- Apenas recuperação de dados; não atualiza ALIs/AIEs nem calcula resultados. Entrada e saída formam uma única função.

### CE05 — Consultar modelos

- Arquivos referenciados: A01.
- Entrada: modelo.
- Saída: nome; marca; categoria.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `ModeloProdutoService.listar/buscar`.
- Apenas recuperação de dados; não atualiza ALIs/AIEs nem calcula resultados. Entrada e saída formam uma única função.

### CE06 — Consultar vínculos

- Arquivos referenciados: A01, A02, A03, A04.
- Entrada: produto; posição.
- Saída: SKU; código de barras; nome; tamanho; cor; setor; unidade; dispositivo; pino; quantidade.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `VinculoService.listar/buscar`.
- Apenas recuperação de dados; não atualiza ALIs/AIEs nem calcula resultados. Entrada e saída formam uma única função.

### CE07 — Consultar equipe e permissões

- Arquivos referenciados: A06, A07.
- Entrada: usuário.
- Saída: nome de usuário; perfil; permissão.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `UsuarioController.listar`.
- Apenas recuperação de dados; não atualiza ALIs/AIEs nem calcula resultados. Entrada e saída formam uma única função.

### CE08 — Consultar lojas

- Arquivos referenciados: A08.
- Entrada: loja.
- Saída: identificador público; nome; IoT; relatórios; ocupação; fracionados.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `LojaController /global; LojaService.listar/editar`.
- Apenas recuperação de dados; não atualiza ALIs/AIEs nem calcula resultados. Entrada e saída formam uma única função.

### CE09 — Consultar configuração IoT

- Arquivos referenciados: A08.
- Entrada: solicitação pelo acionamento da função; contexto da loja autenticada.
- Saída: host MQTT; porta MQTT; QoS; tempo LED.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `ConfiguracaoIoTService.buscar`.
- Apenas recuperação de dados; não atualiza ALIs/AIEs nem calcula resultados. Entrada e saída formam uma única função.

### CE10 — Consultar histórico operacional

- Arquivos referenciados: A01, A02, A03, A05, A06.
- Entrada: tipo; início; fim.
- Saída: data; SKU; nome; posição; variação; saldo posterior; responsável; motivo.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `HistoricoService.consultar`.
- Apenas recuperação de dados; não atualiza ALIs/AIEs nem calcula resultados. Entrada e saída formam uma única função.

### CE11 — Consultar auditoria

- Arquivos referenciados: A06, A09.
- Entrada: solicitação pelo acionamento da função; contexto da loja autenticada.
- Saída: data; responsável; ação; detalhe.
- Controles contados: acionamento; mensagem de resultado.
- Evidência: `HistoricoService.auditoria`.
- Apenas recuperação de dados; não atualiza ALIs/AIEs nem calcula resultados. Entrada e saída formam uma única função.

## Justificativas de agrupamento de dados

Modelos têm manutenção independente e podem existir sem produto; produtos representam SKUs e referenciam modelos. Posições existem antes dos vínculos. O estoque por posição tem sua própria manutenção de vínculos e saldo. Movimentações são eventos históricos, distintos do saldo atual. Usuários e permissões por perfil têm manutenções distintas; a matriz pode existir sem usuário naquele perfil. Lojas contêm sua configuração, que não foi artificialmente desmembrada em outro ALI. Auditoria é consultável pelo usuário no histórico e não se reduz a log técnico. Cada grupo tem um único formato lógico reconhecido; tipos entrada/saída/ajuste/estorno compartilham campos e não foram tratados como quatro registros lógicos. Produto em grade produz vários SKUs, não outro registro lógico.

Campos técnicos excluídos: hash/fingerprint de requisição, chave de idempotência, versão de autenticação, chaves de sessão/CSRF, conexões, tentativas temporárias de login e metadados Flyway. A senha é contada uma vez pelo significado de negócio, não por sua representação em hash. O contexto loja aparece no dicionário por identificar a propriedade dos dados, mas não é multiplicado em cada transação quando apenas obtido da sessão.


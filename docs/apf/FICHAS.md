# Fichas da contagem coletiva

Revisão final de 25/10/2026. As 55 fichas e suas capturas estão incorporadas em [entrega-coletiva-apf.xlsx](coletiva/entrega-coletiva-apf.xlsx). ALI/AIE: TD e RL. Transações: TD e AR. A união de TD elimina repetições; acionamento e feedback são campos de controle. A leitura de arquivos não transforma automaticamente seus campos internos em TD de transação.

## X02 — Ofertas externas (AIE)

Origem: external/ofertas.

Registros lógicos: Oferta comercial externa.

Itens de dados: GTIN; código do fornecedor; SKU externo; descrição; marca; unidade; preço cotado; disponibilidade; data de atualização.

Contagem: 9 TD; 1 RL; complexidade Simples; **5 PF**.

Protótipo real: aba X02 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## A01 — Modelos de produto (ALI)

Origem: models.

Registros lógicos: Registro de modelos de produto.

Itens de dados: loja; código; nome; marca; categoria.

Contagem: 5 TD; 1 RL; complexidade Simples; **7 PF**.

Protótipo real: aba A01 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## A02 — Produtos (ALI)

Origem: products.

Registros lógicos: Registro de produtos.

Itens de dados: loja; código; modelo; SKU; código de barras; cor; tamanho; unidade; preço; características; ativo.

Contagem: 11 TD; 1 RL; complexidade Simples; **7 PF**.

Protótipo real: aba A02 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## A03 — Posições (ALI)

Origem: positions.

Registros lógicos: Registro de posições.

Itens de dados: loja; identificador; código; setor; linha; coluna; capacidade; unidade; dispositivo; pino; ativo.

Contagem: 11 TD; 1 RL; complexidade Simples; **7 PF**.

Protótipo real: aba A03 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## A04 — Estoque por posição / vínculos (ALI)

Origem: occupancy.

Registros lógicos: Registro de estoque por posição / vínculos.

Itens de dados: loja; produto; posição; quantidade.

Contagem: 4 TD; 1 RL; complexidade Simples; **7 PF**.

Protótipo real: aba A04 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## A05 — Movimentações (ALI)

Origem: movements.

Registros lógicos: Registro de movimentações.

Itens de dados: loja; número; produto; posição; responsável; tipo; variação; saldo anterior; saldo posterior; motivo; data; movimento original.

Contagem: 12 TD; 1 RL; complexidade Simples; **7 PF**.

Protótipo real: aba A05 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## A06 — Usuários (ALI)

Origem: users.

Registros lógicos: Registro de usuários.

Itens de dados: loja; código; nome de usuário; senha; perfil; ativo.

Contagem: 6 TD; 1 RL; complexidade Simples; **7 PF**.

Protótipo real: aba A06 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## A07 — Permissões por perfil (ALI)

Origem: role_permissions.

Registros lógicos: Registro de permissões por perfil.

Itens de dados: loja; perfil; permissão.

Contagem: 3 TD; 1 RL; complexidade Simples; **7 PF**.

Protótipo real: aba A07 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## A08 — Lojas e configuração (ALI)

Origem: tenants.

Registros lógicos: Registro de lojas e configuração.

Itens de dados: código; identificador público; nome; IoT; relatórios; ocupação; fracionados; host MQTT; porta MQTT; QoS; tempo LED; ativo.

Contagem: 12 TD; 1 RL; complexidade Simples; **7 PF**.

Protótipo real: aba A08 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## A09 — Auditoria consultável (ALI)

Origem: audit.

Registros lógicos: Registro de auditoria consultável.

Itens de dados: loja; número; responsável; ação; detalhe; data.

Contagem: 6 TD; 1 RL; complexidade Simples; **7 PF**.

Protótipo real: aba A09 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## X01 — Fornecedores externos (AIE)

Origem: external/fornecedores.

Registros lógicos: Registro de fornecedores externos.

Itens de dados: código; nome; documento; e-mail; telefone.

Contagem: 5 TD; 1 RL; complexidade Simples; **5 PF**.

Protótipo real: aba X01 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## CE12 — Consultar ofertas externas (CE)

Entrada: GTIN; código do fornecedor.

Saída: GTIN; código do fornecedor; SKU externo; descrição; marca; unidade; preço cotado; disponibilidade; data de atualização.

Controle: acionamento; mensagem de resultado.

Arquivos: X02.

Evidência: GET /ofertas; OfertaService.consultar; OfertaGateway; external/ofertas/Provedor.java.

Dados internos lidos/mantidos: X02: nove campos da oferta. Filtros exatos opcionais combinados por GTIN e código do fornecedor. Não mantém arquivos nem deriva dados.

Entrada: 4 TD / 1 AR. Saída exclusiva: 7 TD / 1 AR. X02 aplica GTIN/fornecedor e fornece os campos recuperados da oferta. Na função, cada arquivo conta uma vez na união; as avaliações de entrada/saída usam suas referências próprias.

Contagem: 11 TD; 1 AR na união; complexidade Simples; **3 PF**.

Protótipo real: aba CE12 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## SE13 — Comparar preços internos e ofertas externas (SE)

Entrada: GTIN; código do fornecedor.

Saída: GTIN; SKU interno; código do fornecedor; SKU externo; unidade; preço interno; preço cotado; diferença monetária; diferença percentual; data de atualização.

Controle: acionamento; mensagem de resultado.

Arquivos: A02; X02.

Evidência: GET /comparacao-precos; OfertaService.comparar.

Dados internos lidos/mantidos: A02: loja, ativo, código de barras, SKU, unidade e preço. X02: GTIN, fornecedor, SKU externo, unidade, preço cotado e atualização; demais campos recebidos são validados pelo cliente.

Classificação / derivação: Diferença = cotação - preço interno. Percentual = diferença / preço interno x 100, arredondado a duas casas. Preço interno zero: percentual não aplicável. Cruza GTIN e unidade dos produtos ativos da loja, sem gravar dados.

Contagem: 12 TD; 2 AR na união; complexidade Moderada; **5 PF**.

Protótipo real: aba SE13 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE01 — Incluir produto, inclusive grade e saldo inicial (EE)

Entrada: nome; marca; categoria; SKU; código de barras; cor; tamanho; unidade; preço; características; estrutura; tamanhos da grade; posição inicial; quantidades iniciais.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A01; A02; A03; A04; A05; A09.

Evidência: ProdutoService.salvar.

Dados internos lidos/mantidos: A01: nome, marca e categoria do modelo. A02: modelo, SKU, código de barras, cor, tamanho, unidade, preço, características e ativo. A03: posição e unidade, na entrada inicial. A04: produto, posição e quantidade inicial. A05: produto, posição, responsável, tipo, variação, saldos anterior/posterior, motivo e data, quando há saldo inicial. A09: loja, responsável, ação, detalhe e data (gerada). Identificações dos registros são geradas pelo banco; leituras/manutenções internas não são TD adicionais da interface.

Contagem: 16 TD; 6 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE01 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE02 — Alterar produto (EE)

Entrada: produto; nome; marca; categoria; SKU; código de barras; cor; tamanho; unidade; preço; características.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A01; A02; A04; A09.

Evidência: ProdutoService.salvar.

Dados internos lidos/mantidos: A01: modelo; A02: unidade anterior; A04: vínculos para validar mudança de unidade; A09: registro da alteração.

Contagem: 13 TD; 4 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE02 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE03 — Excluir produto logicamente (EE)

Entrada: produto.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A02; A04; A09.

Evidência: ProdutoService.excluir.

Dados internos lidos/mantidos: A02: ativo; A04: quantidade e vínculos removidos se vazios; A09: registro da exclusão.

Contagem: 3 TD; 3 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE03 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE04 — Incluir modelo (EE)

Entrada: nome; marca; categoria.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A01; A09.

Evidência: ModeloProdutoService.salvar.

Dados internos lidos/mantidos: A01: loja, nome, marca e categoria; identificação do modelo gerada no cadastro. A09: loja, responsável, ação modelo.salvar, detalhe com a identificação do modelo e data gerada. Esses dados de auditoria são mantidos internamente e não são campos extras recebidos pela interface.

Contagem: 5 TD; 2 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE04 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE05 — Alterar modelo (EE)

Entrada: modelo; nome; marca; categoria.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A01; A09.

Evidência: ModeloProdutoService.salvar.

Dados internos lidos/mantidos: A01: código e cadastro anterior; A09: registro de alteração.

Contagem: 6 TD; 2 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE05 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE06 — Excluir modelo (EE)

Entrada: modelo.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A01; A02; A09.

Evidência: ModeloProdutoService.excluir.

Dados internos lidos/mantidos: A02: referência ao modelo bloqueia exclusão; A01: código; A09: registro da exclusão.

Contagem: 3 TD; 3 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE06 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE07 — Incluir posição (EE)

Entrada: código; setor; linha; coluna; capacidade; unidade; dispositivo; pino.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A03; A09.

Evidência: PosicaoService.salvar.

Dados internos lidos/mantidos: A03: loja, código, setor, linha, coluna, capacidade, unidade, dispositivo, pino e ativo; identificação gerada no cadastro. A09: loja, responsável, ação posicao.salvar, detalhe com a identificação da posição e data gerada. Auditoria é manutenção interna, sem TD extras na interface.

Contagem: 10 TD; 2 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE07 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE08 — Alterar posição (EE)

Entrada: posição; código; setor; linha; coluna; capacidade; unidade; dispositivo; pino.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A03; A04; A09.

Evidência: PosicaoService.salvar.

Dados internos lidos/mantidos: A03: unidade anterior; A04: vínculos existentes; A09: registro da alteração.

Contagem: 11 TD; 3 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE08 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE09 — Excluir posição logicamente (EE)

Entrada: posição.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A03; A04; A09.

Evidência: PosicaoService.excluir.

Dados internos lidos/mantidos: A03: ativo; A04: quantidade e vínculos vazios; A09: registro da exclusão.

Contagem: 3 TD; 3 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE09 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE10 — Incluir vínculo (EE)

Entrada: produto; posição.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A02; A03; A04; A09.

Evidência: VinculoService.salvar.

Dados internos lidos/mantidos: A02: identificação do produto, unidade e ativo. A03: identificação da posição, unidade e ativo. A04: loja, produto, posição e quantidade inicial zero. A09: loja, responsável, ação vinculo.criar, detalhe produto:posição e data gerada. O usuário informa somente produto/posição; quantidade não é entrada desta EE.

Contagem: 4 TD; 4 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE10 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE11 — Alterar vínculo vazio (EE)

Entrada: produto original; posição original; produto destino; posição destino.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A02; A03; A04; A09.

Evidência: VinculoService.editar.

Dados internos lidos/mantidos: A02/A03: unidades do destino; A04: saldo original e chave do destino; A09: registro.

Contagem: 6 TD; 4 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE11 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE12 — Excluir vínculo vazio (EE)

Entrada: produto; posição.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A02; A03; A04; A09.

Evidência: VinculoService.salvar.

Dados internos lidos/mantidos: A02/A03: identificação; A04: saldo; A09: registro.

Contagem: 4 TD; 4 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE12 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE13 — Incluir usuário (EE)

Entrada: nome de usuário; perfil; senha.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A06; A08; A09.

Evidência: UsuarioService.salvar.

Dados internos lidos/mantidos: A08: identificação da loja e ativo. A06: loja, nome de usuário, senha (persistida como hash), perfil e ativo; identificação gerada. A09: loja, responsável, ação usuario.salvar, detalhe nome/perfil e data gerada. Hash é representação técnica da senha, não um segundo TD.

Contagem: 5 TD; 3 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE13 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE14 — Alterar usuário (EE)

Entrada: usuário; nome de usuário; perfil; senha.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A06; A08; A09.

Evidência: UsuarioService.salvar.

Dados internos lidos/mantidos: A06: perfil, senha anterior e administradores ativos; A08: loja; A09: registro.

Contagem: 6 TD; 3 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE14 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE15 — Excluir usuário logicamente (EE)

Entrada: usuário; nome de usuário; perfil.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A06; A08; A09.

Evidência: UsuarioService.salvar(action=delete).

Dados internos lidos/mantidos: O formulário envia nome/perfil para validação; A06: ativo/perfil/administradores; A08: loja; A09: registro.

Contagem: 5 TD; 3 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE15 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE16 — Incluir loja e administrador (EE)

Entrada: identificador público; nome; IoT; relatórios; ocupação; fracionados; usuário administrador; senha administrador.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A08; A06; A07; A09.

Evidência: LojaService.salvar.

Dados internos lidos/mantidos: Mantém A08, cria A06, inicializa A07, registra A09.

Contagem: 10 TD; 4 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE16 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE17 — Alterar loja e recursos (EE)

Entrada: loja; identificador público; nome; IoT; relatórios; ocupação; fracionados.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A08; A02; A09.

Evidência: LojaService.salvar.

Dados internos lidos/mantidos: A08: configuração; A02: unidade/ativo para impedir desabilitar fracionados em uso; A09: registro.

Contagem: 9 TD; 3 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE17 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE18 — Excluir loja logicamente (EE)

Entrada: loja.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A08; A09.

Evidência: LojaService.excluir.

Dados internos lidos/mantidos: A08: nome/ativo; A09: registro.

Contagem: 3 TD; 2 AR na união; complexidade Simples; **3 PF**.

Protótipo real: aba EE18 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE19 — Alterar matriz de permissões (EE)

Entrada: perfil; permissão; concedida.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A07; A09.

Evidência: PermissaoService.atualizar.

Dados internos lidos/mantidos: A07: perfil/permissão; cada par repetido é ocorrência, não novo tipo de dado; A09: registro.

Contagem: 5 TD; 2 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE19 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE20 — Alterar configuração IoT (EE)

Entrada: host MQTT; porta MQTT; QoS; tempo LED.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A08; A09.

Evidência: ConfiguracaoIoTService.salvar.

Dados internos lidos/mantidos: A08: configuração da loja; A09: registro.

Contagem: 6 TD; 2 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE20 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE21 — Registrar entrada (EE)

Entrada: produto; posição; tipo; quantidade; motivo.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A02; A03; A04; A05; A09.

Evidência: EstoqueService.movimentar.

Dados internos lidos/mantidos: A02: identificação, unidade e ativo do produto. A03: identificação, unidade e ativo da posição. A04: loja, produto, posição e quantidade anterior/posterior. A05: produto, posição, responsável, tipo, variação, saldo anterior, saldo posterior, motivo e data; número gerado e movimento original vazio na entrada normal. A09: loja, responsável, ação, detalhe e data. Chave técnica de idempotência excluída dos TD.

Contagem: 7 TD; 5 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE21 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE22 — Registrar saída (EE)

Entrada: produto; posição; tipo; quantidade; motivo.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A02; A03; A04; A05; A09.

Evidência: EstoqueService.movimentar.

Dados internos lidos/mantidos: A02/A03: unidades; A04: saldo; A05: movimento e saldos; A09: registro. Chave técnica de idempotência excluída.

Contagem: 7 TD; 5 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE22 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE23 — Ajustar saldo (EE)

Entrada: produto; posição; tipo; quantidade; motivo.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A02; A03; A04; A05; A09.

Evidência: EstoqueService.movimentar.

Dados internos lidos/mantidos: A02/A03: unidades; A04: saldo; A05: movimento e saldos; A09: registro. Chave técnica de idempotência excluída.

Contagem: 7 TD; 5 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE23 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE24 — Estornar movimentação (EE)

Entrada: movimento original; motivo.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A02; A03; A04; A05; A09.

Evidência: EstoqueService.estornar.

Dados internos lidos/mantidos: A05: tipo/produto/posição/variação/original; A04: saldo; A02/A03: unidades; A09: registro.

Contagem: 4 TD; 5 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE24 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE25 — Autenticar usuário (EE)

Entrada: loja; nome de usuário; senha.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A06; A08; A09.

Evidência: AutenticacaoService.autenticar.

Dados internos lidos/mantidos: A06: credencial/perfil/ativo; A08: identificador público/ativo via consulta de autenticação; A09: login. Tentativas temporárias excluídas.

Contagem: 5 TD; 3 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba EE25 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE26 — Selecionar loja de trabalho (EE)

Entrada: loja.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A08; A09.

Evidência: LojaService.selecionar.

Dados internos lidos/mantidos: A08: loja selecionada; A09: registro do acesso global.

Contagem: 3 TD; 2 AR na união; complexidade Simples; **3 PF**.

Protótipo real: aba EE26 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE27 — Sinalizar posição por LED (EE)

Entrada: posição.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: A03; A08; A09.

Evidência: IoTService.sinalizar.

Dados internos lidos/mantidos: A03: dispositivo/pino; A08: host/porta/QoS/tempo LED; A09: registro. Intenção primária: enviar controle para ligar LED.

Contagem: 3 TD; 3 AR na união; complexidade Moderada; **4 PF**.

Protótipo real: aba EE27 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## EE28 — Encerrar sessão (EE)

Entrada: acionamento; sem filtro de negócio obrigatório.

Saída: mensagem de resultado.

Controle: acionamento.

Arquivos: .

Evidência: SegurancaConfig /logout.

Dados internos lidos/mantidos: Altera o comportamento de acesso; sessão temporária não é ALI.

Contagem: 2 TD; 0 AR na união; complexidade Simples; **3 PF**.

Protótipo real: aba EE28 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## SE02 — Valor potencial do estoque (SE)

Entrada: acionamento; sem filtro de negócio obrigatório.

Saída: SKU; unidade; saldo; preço; valor por produto; total da loja.

Controle: acionamento; mensagem de resultado.

Arquivos: A02; A04.

Evidência: AnaliseService.relatorio(valor).

Dicionário dos arquivos referenciados: A02: loja; código; modelo; SKU; código de barras; cor; tamanho; unidade; preço; características; ativo | A04: loja; produto; posição; quantidade. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Classificação / derivação: Saldo × preço atual, arredondado em centavos por produto; total = soma dos valores. Não é custo nem receita realizada.

Contagem: 8 TD; 2 AR na união; complexidade Moderada; **5 PF**.

Protótipo real: aba SE02 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## SE04 — Fluxo de movimentações (SE)

Entrada: início; fim.

Saída: SKU; unidade; tipo; número de operações; variação líquida.

Controle: acionamento; mensagem de resultado.

Arquivos: A02; A05.

Evidência: AnaliseService.relatorio(fluxo).

Dicionário dos arquivos referenciados: A02: loja; código; modelo; SKU; código de barras; cor; tamanho; unidade; preço; características; ativo | A05: loja; número; produto; posição; responsável; tipo; variação; saldo anterior; saldo posterior; motivo; data; movimento original. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Classificação / derivação: COUNT(*) e SUM(delta) por produto/tipo. Período inclusivo em America/Sao_Paulo; estornos em categoria própria.

Contagem: 9 TD; 2 AR na união; complexidade Moderada; **5 PF**.

Protótipo real: aba SE04 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## SE06 — Pesquisar estoque e localizar produto (SE)

Entrada: busca; produto selecionado.

Saída: SKU; nome; marca; categoria; cor; tamanho; unidade; preço; saldo total; posição; quantidade na posição; quantidade de produtos exibidos.

Controle: acionamento; mensagem de resultado.

Arquivos: A01; A02; A03; A04.

Evidência: ProdutoService.pesquisar/buscar; DashboardController; BuscaAssistidaController.

Dicionário dos arquivos referenciados: A01: loja; código; nome; marca; categoria | A02: loja; código; modelo; SKU; código de barras; cor; tamanho; unidade; preço; características; ativo | A03: loja; identificador; código; setor; linha; coluna; capacidade; unidade; dispositivo; pino; ativo | A04: loja; produto; posição; quantidade. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Classificação / derivação: Soma das posições para obter saldo. Dashboard, busca digitada/voz e lista de produtos reutilizam a mesma intenção; não contar canais novamente.

Contagem: 16 TD; 4 AR na união; complexidade Complexa; **7 PF**.

Protótipo real: aba SE06 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## SE07 — Mapa operacional de posições (SE)

Entrada: acionamento; sem filtro de negócio obrigatório.

Saída: posição; setor; linha; coluna; capacidade; unidade; ocupado; nome do produto; SKU; quantidade do vínculo; alerta de excesso.

Controle: acionamento; mensagem de resultado.

Arquivos: A01; A02; A03; A04.

Evidência: MapaController /mapa e /ocupacao.

Dicionário dos arquivos referenciados: A01: loja; código; nome; marca; categoria | A02: loja; código; modelo; SKU; código de barras; cor; tamanho; unidade; preço; características; ativo | A03: loja; identificador; código; setor; linha; coluna; capacidade; unidade; dispositivo; pino; ativo | A04: loja; produto; posição; quantidade. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Classificação / derivação: Soma as quantidades dos vínculos por posição e compara com a capacidade para apresentar alerta de excesso. Mapa e lista de ocupação são canais do mesmo processo.

Contagem: 13 TD; 4 AR na união; complexidade Complexa; **7 PF**.

Protótipo real: aba SE07 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## SE08 — Ler peso líquido e subtotal (SE)

Entrada: produto; dispositivo.

Saída: peso bruto; tara; peso líquido; subtotal; indicador de simulação.

Controle: acionamento; mensagem de resultado.

Arquivos: A02; A08.

Evidência: IoTService.peso; movimento.html em /movimentar; DispositivosDemo na loja granel.

Dicionário dos arquivos referenciados: A02: loja; código; modelo; SKU; código de barras; cor; tamanho; unidade; preço; características; ativo | A08: código; identificador público; nome; IoT; relatórios; ocupação; fracionados; host MQTT; porta MQTT; QoS; tempo LED; ativo. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Classificação / derivação: Peso líquido=bruto−tara; subtotal=peso líquido×preço. Telemetria transitória não é AIE.

Contagem: 9 TD; 2 AR na união; complexidade Moderada; **5 PF**.

Protótipo real: aba SE08 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## SE09 — Exportar histórico (SE)

Entrada: tipo; início; fim; formato.

Saída: número; data; SKU; tipo; posição; variação; responsável; motivo.

Controle: acionamento; mensagem de resultado.

Arquivos: A01; A02; A03; A05; A06; A09.

Evidência: RelatorioService.exportar; RelatorioCsv; RelatorioPdf.

Dicionário dos arquivos referenciados: A01: loja; código; nome; marca; categoria | A02: loja; código; modelo; SKU; código de barras; cor; tamanho; unidade; preço; características; ativo | A03: loja; identificador; código; setor; linha; coluna; capacidade; unidade; dispositivo; pino; ativo | A05: loja; número; produto; posição; responsável; tipo; variação; saldo anterior; saldo posterior; motivo; data; movimento original | A06: loja; código; nome de usuário; senha; perfil; ativo | A09: loja; número; responsável; ação; detalhe; data. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Classificação / derivação: SE por atualizar A09 com a exportação; CSV/PDF são formatos do mesmo processo. Função preexistente, fora das cinco saídas escolhidas para demonstrar dados calculados.

Contagem: 13 TD; 6 AR na união; complexidade Complexa; **7 PF**.

Protótipo real: aba SE09 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## CE04 — Consultar fornecedores externos (CE)

Entrada: filtro código, nome ou documento.

Saída: código; nome; documento; e-mail; telefone.

Controle: acionamento; mensagem de resultado.

Arquivos: X01.

Evidência: GET /fornecedores?q=; AnaliseService.consultar; FornecedorGateway.

Dicionário dos arquivos referenciados: X01: código; nome; documento; e-mail; telefone. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Entrada: 3 TD / 1 AR. Saída exclusiva: 5 TD / 1 AR. X01 aplica o filtro por código/nome/documento e fornece os dados recuperados. Na função, cada arquivo conta uma vez na união; as avaliações de entrada/saída usam suas referências próprias.

Contagem: 8 TD; 1 AR na união; complexidade Simples; **3 PF**.

Protótipo real: aba CE04 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## CE05 — Consultar modelos (CE)

Entrada: modelo.

Saída: nome; marca; categoria.

Controle: acionamento; mensagem de resultado.

Arquivos: A01.

Evidência: ModeloProdutoService.listar/buscar.

Dicionário dos arquivos referenciados: A01: loja; código; nome; marca; categoria. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Entrada: 3 TD / 1 AR. Saída exclusiva: 3 TD / 1 AR. A01 localiza o modelo pelo ID e fornece nome, marca e categoria. Na função, cada arquivo conta uma vez na união; as avaliações de entrada/saída usam suas referências próprias.

Contagem: 6 TD; 1 AR na união; complexidade Simples; **3 PF**.

Protótipo real: aba CE05 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## CE06 — Consultar vínculos (CE)

Entrada: produto; posição.

Saída: SKU; nome; código da posição; setor; unidade; quantidade.

Controle: acionamento; mensagem de resultado.

Arquivos: A01; A02; A03; A04.

Evidência: VinculoService.listar/buscar.

Dicionário dos arquivos referenciados: A01: loja; código; nome; marca; categoria | A02: loja; código; modelo; SKU; código de barras; cor; tamanho; unidade; preço; características; ativo | A03: loja; identificador; código; setor; linha; coluna; capacidade; unidade; dispositivo; pino; ativo | A04: loja; produto; posição; quantidade. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Entrada: 4 TD / 4 AR. Saída exclusiva: 6 TD / 4 AR. A01/A02/A03/A04 participam da busca do vínculo pela chave produto/posição e da apresentação do vínculo. A busca q do seletor de produtos reutiliza SE06 e não é nova função CE. Na função, cada arquivo conta uma vez na união; as avaliações de entrada/saída usam suas referências próprias.

Contagem: 10 TD; 4 AR na união; complexidade Complexa; **6 PF**.

Protótipo real: aba CE06 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## CE07 — Consultar equipe e permissões (CE)

Entrada: usuário.

Saída: nome de usuário; perfil; permissão.

Controle: acionamento; mensagem de resultado.

Arquivos: A06; A07.

Evidência: UsuarioController.listar.

Dicionário dos arquivos referenciados: A06: loja; código; nome de usuário; senha; perfil; ativo | A07: loja; perfil; permissão. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Entrada: 3 TD / 1 AR. Saída exclusiva: 3 TD / 2 AR. A06 localiza o usuário selecionado. A saída consulta A06 e A07 para apresentar usuários, perfis e a matriz de permissões; A07 não valida a entrada do usuário. Na função, cada arquivo conta uma vez na união; as avaliações de entrada/saída usam suas referências próprias.

Contagem: 6 TD; 2 AR na união; complexidade Simples; **3 PF**.

Protótipo real: aba CE07 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## CE08 — Consultar lojas (CE)

Entrada: loja.

Saída: identificador público; nome; IoT; relatórios; ocupação; fracionados.

Controle: acionamento; mensagem de resultado.

Arquivos: A08.

Evidência: LojaController /global; LojaService.listar/editar.

Dicionário dos arquivos referenciados: A08: código; identificador público; nome; IoT; relatórios; ocupação; fracionados; host MQTT; porta MQTT; QoS; tempo LED; ativo. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Entrada: 3 TD / 1 AR. Saída exclusiva: 6 TD / 1 AR. A08 localiza a loja selecionada e fornece seus dados e recursos. Na função, cada arquivo conta uma vez na união; as avaliações de entrada/saída usam suas referências próprias.

Contagem: 9 TD; 1 AR na união; complexidade Simples; **3 PF**.

Protótipo real: aba CE08 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## CE09 — Consultar configuração IoT (CE)

Entrada: acionamento; sem filtro de negócio obrigatório.

Saída: host MQTT; porta MQTT; QoS; tempo LED.

Controle: acionamento; mensagem de resultado.

Arquivos: A08.

Evidência: ConfiguracaoIoTService.buscar.

Dicionário dos arquivos referenciados: A08: código; identificador público; nome; IoT; relatórios; ocupação; fracionados; host MQTT; porta MQTT; QoS; tempo LED; ativo. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Entrada: 2 TD / 0 AR. Saída exclusiva: 4 TD / 1 AR. Sem seleção de registro nem validação de dado de negócio na entrada; o acionamento usa a loja da sessão. A08 é lido para apresentar a configuração. Na função, cada arquivo conta uma vez na união; as avaliações de entrada/saída usam suas referências próprias.

Contagem: 6 TD; 1 AR na união; complexidade Simples; **3 PF**.

Protótipo real: aba CE09 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## SE10 — Consultar histórico operacional (SE)

Entrada: tipo; início; fim.

Saída: data; SKU; posição; tipo; variação; saldo posterior; responsável; motivo; indicador de estorno.

Controle: acionamento; mensagem de resultado.

Arquivos: A01; A02; A03; A05; A06.

Evidência: HistoricoService.consultar.

Dicionário dos arquivos referenciados: A01: loja; código; nome; marca; categoria | A02: loja; código; modelo; SKU; código de barras; cor; tamanho; unidade; preço; características; ativo | A03: loja; identificador; código; setor; linha; coluna; capacidade; unidade; dispositivo; pino; ativo | A05: loja; número; produto; posição; responsável; tipo; variação; saldo anterior; saldo posterior; motivo; data; movimento original | A06: loja; código; nome de usuário; senha; perfil; ativo. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Classificação / derivação: EXISTS de movimento reversor gera indicador Estornado. Exemplo didático: movimento 10 sem reversor = falso; movimento 11 com reverse_of=10 torna o indicador do 10 verdadeiro. Não altera arquivos.

Contagem: 13 TD; 5 AR na união; complexidade Complexa; **7 PF**.

Protótipo real: aba SE10 do Excel coletivo; origem em capturas/manifesto-incorporado.json.

## CE11 — Consultar auditoria (CE)

Entrada: acionamento; sem filtro de negócio obrigatório.

Saída: data; responsável; ação; detalhe.

Controle: acionamento; mensagem de resultado.

Arquivos: A06; A09.

Evidência: HistoricoService.auditoria.

Dicionário dos arquivos referenciados: A06: loja; código; nome de usuário; senha; perfil; ativo | A09: loja; número; responsável; ação; detalhe; data. Esses campos documentam os arquivos; a lista de TD da transação acima determina a contagem.

Entrada: 2 TD / 0 AR. Saída exclusiva: 4 TD / 2 AR. Sem filtro de negócio na entrada; o acionamento usa a loja da sessão. A09 e A06 são lidos na saída para listar eventos e responsáveis. Na função, cada arquivo conta uma vez na união; as avaliações de entrada/saída usam suas referências próprias.

Contagem: 6 TD; 2 AR na união; complexidade Simples; **3 PF**.

Protótipo real: aba CE11 do Excel coletivo; origem em capturas/manifesto-incorporado.json.


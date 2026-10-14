# Rastreabilidade e aceite

Fonte: especificacao.pdf, pp. 9–19. Não considerar a meta completa somente pela
existência de classes ou testes unitários. A implementação atual usa Java MVC;
evidências da antiga aplicação Python não comprovam a refatoração.

| Requisito | Implementação atual | Evidência / próximo aceite |
| --- | --- | --- |
| RF001 | Posicao, PosicaoRepository, PosicaoService, /posicoes | Templates de criação/edição renderizados; CRUD real pendente |
| RF002 | Produto, ModeloProduto, repositórios e ProdutoService, /produtos | Templates de criação/edição renderizados; CRUD real pendente |
| RF003–004 | OcupacaoRepository e VinculoService, /vinculos | View renderizada; vínculos e transações exigem PostgreSQL |
| RF005–006 | UsuarioRepository, UsuarioService, PermissaoService, Spring Security | Equipe e matriz renderizadas; revalidar persistência, revogação e RBAC |
| RF007 | HistoricoService, MovimentacaoRepository, /historico | Histórico e auditoria renderizados com dados de teste |
| RF008 | ProdutoRepository.pesquisar | Busca por termos implementada; revalidar consultas PostgreSQL |
| RF009 | BuscaAssistidaController, voz.html | View renderizada; Alexa externa pendente |
| RF010 | IoTService e MqttGateway | Rotas JSON e proxy conectados; repetir teste de broker real após refatoração |
| RF011 | Resultados textuais e botão Ouvir localização por posição | Template renderizado; reprodução sonora, testes JavaScript e Alexa pendentes |
| RF012 | EstoqueService, /movimentar, /baixa-rapida | Views renderizadas; validar fluxo completo de baixa no navegador e banco |
| RF013 | ModeloProduto e grade no ProdutoService | Grade e saldo inicial por tamanho implementados, com posição comum, validação e transação; testes novos ainda não compilados, aceite real pendente |
| RF014 | EstoqueService.estornar | View renderizada; revalidar repetição, saldo e concorrência em PostgreSQL |
| RF015 | LoginController, SegurancaConfig, AutenticacaoService | Login renderizado; acesso anônimo e CSRF verificados na camada MVC. Corrigida recuperação de sessão revogada; duas regressões novas ainda não executadas |
| RF016 | RegistroAuditoria e AuditoriaRepository | SQL restrito ao repositório; revalidar atomicidade das mutações |
| RF017 | LojaController e LojaService, superadmin | Painel global renderizado; revalidar fluxo real de criação/seleção |
| RF018 | DemonstracaoService e DispositivosDemo | Provisionamento e simulador Java implementados; repetir validação Docker |
| RNF001 | Espera MQTT limitada e índices | Carga e medição ponta a ponta pendentes |
| RNF002 | BCrypt no Spring Security | Configuração presente; login real depende de PostgreSQL |
| RNF003 | Templates semânticos e CSS responsivo | Renderização aprovada; revisão visual desktop/mobile pendente |
| RNF004 | Perfis, autorização por loja e revogação por versão | Serviços implementados; repetir testes integrados |
| RNF005 | Healthcheck e reinício | Disponibilidade de 99% não comprovada |
| RNF006 | Locks, transações, constraints e idempotência | Testes PostgreSQL adaptados; execução atual pendente |
| RNF007 | Limites de memória no Compose | Consumo real sob carga não medido |
| RNF008 | Modelos, repositórios, serviços, controladores e views | Inspeção confirma SQL apenas nos repositórios da aplicação |
| RNF009 | Configuração de recursos e posições por loja | Forms renderizados; revalidar isolamento no banco |
| RNF010 | MQTT para LED e balança | Simulação não comprova hardware; homologação pendente |
| RNF011 | NGINX com allowlist e restrição de métodos | /api/iot incluído; validação runtime do proxy pendente |
| RNF012 | Java MVC, Singleton, Template Method e Strategy | Classes e participantes no README; build integral ainda exige validação |
| RNF013 | Vidro claro, fundo perolado e movimento reduzido | CSS e templates implementados; inspeção visual pendente |

Checkpoint de 08/10/2026: 23 testes MVC de templates e respostas, dois testes
unitários de estoque e dois de endereço MQTT passaram. Sete testes integrados foram ignorados
por ausência de TEST_DATABASE_URL. Não houve comprovação atual de startup com
banco, requisições HTTP em socket, hardware, áudio ou responsividade visual.
Consulte VALIDACAO.md para comandos, limites e alterações posteriores aos testes.

Atualização HTTP de 09/10/2026: a aplicação Docker respondeu health e login.
O registro evidence/2026-10-09/http-live.json comprova fluxos de cadastro,
vínculos, estoque, concorrência, estorno, permissões, isolamento, CSRF, busca
JSON, logout e exportações na versão publicada. O MQTT confirmou leitura e
ACK simulados. A grade com saldo inicial ainda não está nessa imagem; o retorno
ao login após revogação foi reprovado nela e está corrigido apenas no fonte.
Edição completa de todos os cadastros, reinício com persistência, testes
Playwright e aceite das alterações novas continuam pendentes. Consulte a seção
HTTP real em VALIDACAO.md para distinguir a versão publicada do worktree.

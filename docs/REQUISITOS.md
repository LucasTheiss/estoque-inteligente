# Rastreabilidade e aceite

Fonte: especificacao.pdf, pp. 9–18. Não considerar a meta completa somente pela
existência de classes ou testes unitários.

| Requisito | Implementação atual | Evidência / próximo aceite |
| --- | --- | --- |
| RF001 | /posicoes, exclusão lógica sem saldo | CRUD PostgreSQL testado |
| RF002 | /produtos/novo, /produtos/<id> | CRUD e grade testados |
| RF003–004 | /vinculos, saldo por produto/posição | FK composta, movimentação e remoção vazia testadas |
| RF005–006 | /usuarios, matriz RBAC, require | Hash, revogação, perfis e bloqueios testados |
| RF007 | Histórico e consulta /historico | Saldo e logs na mesma transação; rollback testado |
| RF008 | Busca por termos em catalog.products | Campos e JSON pesquisáveis; característica testada |
| RF009 | Busca assistida voz/texto | Interface presente; Alexa e homologação externa pendentes |
| RF010 | MqttAdapter.signal | Publicação implementada; broker/dispositivo real pendentes |
| RF011 | Texto e síntese de voz no navegador | Interface presente; validação áudio/Alexa pendente |
| RF012 | Dashboard, /movimentar, /baixa-rapida | Formulários e idempotência testados |
| RF013 | Modelo e variantes por grade | Três variantes com mesmo modelo testadas |
| RF014 | Estorno e movimento inverso | Repetição e saldo verificados em PostgreSQL |
| RF015 | Login, sessão e CSRF | Anônimo e revogação testados |
| RF016 | Audit em mutações críticas | Auditoria e rollback provocado testados |
| RNF001 | Limites de espera MQTT; consultas indexadas | Medição ponta a ponta e carga de referência pendentes |
| RNF002 | Hash scrypt Werkzeug | Hash persistido testado |
| RNF003 | HTML semântico e CSS responsivo | Revisão visual desktop/mobile pendente |
| RNF004 | Usuário, hash, perfil; menor privilégio padrão | RBAC e isolamento testados |
| RNF005 | Healthcheck e reinício | 99% exige observação em produção, não comprovada |
| RNF006 | Locks, transação, constraints, idempotência | 38 chamadas concorrentes; 20 baixas únicas e saldo zero |
| RNF007 | Limites de memória no Compose | Consumo real sob carga não medido |
| RNF008 | Módulos domínio, persistência, segurança, web, integrações | Regras transacionais compartilhadas |
| RNF009 | Configuração por loja e posições cadastráveis | Duas lojas isoladas; cadastro sem alteração de código |
| RNF010 | MQTT para LED e balança | Adapter implementado; broker real pendente |
| RNF011 | Proxy NGINX com allowlist e restrição de métodos | Arquivo presente; validação runtime pendente |

As telas 1–11 possuem templates próprios. Checkpoint: 11 testes Python aprovados
com PostgreSQL 17 local; 17 templates compilados e telas renderizadas por teste
HTTP. Isso não comprova hardware, áudio, responsividade visual ou disponibilidade.
O exemplo Java está preservado e não é o backend operacional.

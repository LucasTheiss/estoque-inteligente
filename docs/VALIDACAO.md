# Validação da refatoração Java

## Estado atual — 15/10/2026

A validação mais recente é a da [Opção 01 em 14/10/2026](../evidence/2026-10-14/opcao-01/README.md).
O log registra 68 testes Java aprovados, zero falhas, zero erros e zero ignorados;
nove testes JavaScript também passaram. Build e inicialização Docker, PostgreSQL,
MQTT simulado e revisão de telas estão registrados nas evidências dessa data.
Os registros anteriores neste arquivo são checkpoints históricos e não devem
ser lidos como estado atual quando forem contraditos por essa validação posterior.

Limites atuais: o MQTT foi exercitado com simulador, sem hardware físico; Alexa
externa, carga prolongada e disponibilidade operacional não foram comprovadas.

## Evidências de 08/10/2026

| Verificação | Resultado observado | Limite |
| --- | --- | --- |
| Testes unitários de estoque | 2 testes, 0 falhas, 0 erros | Não exercitam banco ou HTTP |
| TelasTest | 23 testes, 0 falhas, 0 erros | MockMvc, templates reais e serviços simulados; não é teste por socket HTTP |
| MqttGatewayTest | 2 testes, 0 falhas, 0 erros | Tradução do nome mqtt para localhost; não conecta ao broker |
| AplicacaoTest | 7 testes ignorados | TEST_DATABASE_URL ausente; não são testes aprovados |
| SQL da aplicação | Ocorrências de JdbcTemplate e comandos SQL somente em repository | Migrações e preparação de testes são fronteiras próprias |
| Diretório de templates | login.html e demais views presentes em src/main/resources/templates | Inclusão no JAR final ainda não verificada |
| Docker | Não executado nesta etapa | Acesso negado à configuração Docker do usuário; Compose indisponível no processo |
| PostgreSQL local | Inicialização recusada | pg_ctl falhou ao criar token restrito, código 87; dados preservados |
| Maven incremental offline | mvn -o test terminou com sucesso: 27 executados, 7 ignorados | Compiladores informaram classes atualizadas; não comprova reconstrução do zero ou empacotamento |
| Reconstrução e empacotamento | Não certificados | Tentativa anterior de compilação falhou ao fechar snakeyaml-2.4.jar; downloads de plugins também receberam Permission denied |
| Classes legadas no classpath | Nenhuma classe sem fonte Java correspondente encontrada em target/classes | Inspeção dos nomes; não substitui build do zero |
| Cartographer | Skill e mapa lidos; código real inspecionado | Runtime Bun indisponível; grafo automático ainda desatualizado |
| Navegador | Indisponível | Inventário CUA sem navegadores; inspeção visual não executada |

Os testes de telas inicialmente detectaram StackOverflowError na inclusão do
cabeçalho. O fragmento chamado head selecionava também a tag que o incluía.
Renomear o fragmento para documentHead eliminou a recursão. A rodada seguinte
executou as 18 rotas/variações e os quatro contratos adicionais sem falhas.

A execução de testes sobre artefatos gerados foi feita com Maven
`resources:resources surefire:test -Dtest=TelasTest`. Isso valida esses artefatos
e os recursos atuais; não transforma a falha anterior de compilação em build
integral aprovado. Os relatórios estão em target/surefire-reports.

A rodada posterior executou MqttGatewayTest (2), OperacaoEstoqueTest (2) e
TelasTest (23): 27 testes passaram. A API solicitada com Accept: text/html
retornou 406, sem tentar serializar um Map como HTML. O perfil local e a tradução
do endereço MQTT estão presentes; a conexão real do perfil ainda não foi testada.
A sequência mvn -o test concluiu sem falhas com as classes atualizadas, mas os
sete testes PostgreSQL continuaram ignorados.

O teste integrado agora inicia Spring em porta aleatória e usa HttpClient para
login com sessão e CSRF, cadastro, vínculo, entrada de estoque, exportação CSV
e logout. Ele não executou: TEST_DATABASE_URL permanece ausente. A última
rodada incremental terminou com 27 testes aprovados e 7 ignorados.

A busca assistida ganhou resposta falada por localização, reprodução iniciada
pelo usuário, interrupção e alternativa textual. Após alterar o template,
TelasTest voltou a passar os 23 casos. Cinco testes JavaScript foram adicionados;
o comando Node não iniciou por EPERM ao resolver o caminho de usuário. Não há
evidência de reprodução sonora nem de aprovação desses cinco testes.

## Próximo aceite registrado em 08/10/2026

1. Executar mvn test e mvn package em Java 21+, sem falhas de acesso ao cache.
2. Executar o serviço verify do Compose, incluindo PostgreSQL e MQTT reais.
3. Iniciar app e proxy; verificar /health e /login por HTTP real.
4. Entrar nos perfis, cadastrar produto/posição/vínculo, movimentar, estornar
   e confirmar persistência após reinício, sem remover volumes.
5. Conferir respostas CSV/PDF e recursos estáticos no JAR.
6. Inspecionar desktop/mobile com Impeccable e concluir a revisão visual.
7. Atualizar o módulo alterado no Cartographer, revisar e publicar os commits.

O mapa complementar manual em .cartographer/modules/java-web.md registra o
módulo refatorado com suas fontes. Não é apresentado como índice gerado ou
verificado pelo Cartographer. Nenhum teste antigo da implementação Python foi
reaproveitado como prova de funcionamento da aplicação Java atual.

## Evidências de 09/10/2026

O runtime Node voltou a permitir leitura, escrita no workspace e processos.
O terminal integrado ainda falhou antes de iniciar o comando. Os relatórios
e logs desta rodada estão em evidence/2026-10-09.

Antes da alteração de grade, Maven incremental executou 27 testes aprovados e
sete ignorados. Depois da alteração, a compilação principal e a compilação de
testes falharam ao fechar snakeyaml-2.4.jar com AccessDeniedException. A classe
principal ProdutoService foi atualizada, mas ProdutoServiceTest.class não foi
gerada e TelasTest.class permaneceu datada de 08/10/2026.

A execução direta de surefire:test passou novamente nos 27 testes anteriores
e ignorou sete integrações. Esse resultado não valida os testes novos da grade
nem certifica build integral. O código-fonte agora contém dez casos de
ProdutoServiceTest, um novo contrato MVC e um novo teste PostgreSQL para saldo
inicial e rollback por posição de outra loja; todos aguardam compilação.
AplicacaoTest possui oito testes no fonte, embora o artefato antigo tenha sete.

Docker não encontrou o daemon e não pôde ler a configuração do usuário.
pg_ctl informou ausência de servidor; a tentativa de iniciar PostgreSQL falhou
ao criar token restrito, código 87. O teste JavaScript não iniciou por EPERM ao
resolver o diretório do usuário. Não houve teste Playwright, HTTP real, broker
real ou comprovação de inicialização da aplicação nesta rodada.

O cadastro de grade passou a receber posição e saldo inicial de cada tamanho,
com validação de quantidades, permissão de entrada e coordenação transacional
dos serviços existentes. A confirmação de persistência e rollback ainda depende
do teste integrado. Não houve alteração do esquema nem remoção de dados.

A política atual monta .git como somente leitura. Nenhum commit ou push foi
feito nesta rodada. O índice automático Cartographer continua desatualizado;
apenas o complemento manual do módulo recebeu as novas dependências.

## Recuperação de sessão e Docker — continuação de 09/10/2026

Após o usuário iniciar Docker, o erro mudou de daemon ausente para permission denied no pipe docker_engine. O log está em evidence/2026-10-09/docker-after-start.log. A sessão ainda não consegue administrar o daemon.

A inspeção encontrou uma falha de recuperação: ContextoViewAdvice revalidava a sessão também na tela pública de login. Uma versão de autenticação revogada impedia abrir essa tela. O contexto agora deixa /login público sem carregar o usuário anterior; as rotas protegidas continuam revalidando sessão e permissões. Foram acrescentadas duas regressões MVC: login com sessão revogada (incluindo context path) e recusa de acesso a produtos. Os testes novos continuam sem execução devido à falha de compilação já registrada.

## HTTP real com a aplicação Docker — 09/10/2026

Após iniciar Docker, as portas 5433 e 1883 ficaram acessíveis e /health em 8080
retornou 200. Foi possível testar a aplicação Java atrás do NGINX por HTTP,
apesar de a sessão continuar sem permissão para administrar o daemon.

O arquivo evidence/2026-10-09/http-live.json contém 86 registros de observação
das requisições e verificações. Esses registros não são 86 testes JUnit nem
testes Playwright. A loja isolada e2e-mv1djzc9 foi criada com quatro perfis.
Os registros de teste foram preservados; a exclusão de produto e posição foi
lógica e o histórico continuou disponível.

Foram verificados login global/local, criação de loja e usuários, criação de
produtos inteiros/fracionados e três variantes de grade, posições e vínculos,
recusa de unidades incompatíveis, entradas/saídas/ajuste, precisão decimal,
idempotência, saldo insuficiente, estorno e recusa de repetição, proteção de
exclusões com saldo, permissões por perfil, concessão e retirada de permissão,
isolamento de produto entre lojas, CSRF, busca por característica JSON, logout,
concorrência de duas saídas para uma unidade e preservação do histórico após
exclusão lógica. CSV e PDF foram salvos no mesmo diretório de evidências.

O broker respondeu conectado. A balança simulada retornou bruto 1,440 kg,
tara 0,015 kg, líquido 1,425 kg e subtotal 42,61. O comando de sinalização recebeu
ACK confirmado com simulated=true. Isso valida transporte MQTT e simulador,
não hardware físico.

A aplicação publicada ainda não contém os campos initialPositionId e
initialQuantities. Nela, a recuperação de sessão revogada foi reprovada:
GET /login retornou 403. A correção correspondente existe no fonte e aguarda
reconstrução/publicação e repetição do teste. Não usar estes resultados como
aceite das alterações novas da grade ou da recuperação de sessão.

O redirecionamento observado no login usou URL absoluta. A asserção do teste
Java foi corrigida para comparar o caminho da URI, aceitando Location absoluto
ou relativo para o mesmo destino. O teste Java alterado ainda não executou.

Playwright não produziu evidência: a importação ESM falhou e a tentativa de
carregamento CommonJS/inicialização expirou, reiniciando o runtime de ferramentas.
Não houve handle de navegador utilizável; a consulta posterior de processos foi
negada. Não houve inspeção visual ou teste de interação por navegador.

## Compilação e demonstração recuperadas — 13/10/2026

A falha anterior foi isolada ao ambiente restrito Windows: AccessDeniedException no caminho de snakeyaml-2.4.jar durante o fechamento de ZipFileSystem pelo compilador. Fora do sandbox, a execução Maven Windows concluiu com BUILD SUCCESS (40 testes aprovados e oito integrações ignoradas por ausência das variáveis de teste). Nenhuma troca de dependência foi necessária.

A execução docker compose --env-file .env.demo --profile check run --rm verify recompilou os 84 fontes e seis arquivos de teste com Java 21 e PostgreSQL/MQTT reais: 48 testes, zero falhas, zero erros, zero ignorados. O teste de provisionamento foi ampliado para comprovar flags de variabilidade, quatro usuários por loja, oito posições, quatro produtos com vínculos/saldos e entradas, permissões, auditoria e preservação de produtos, saldos, hashes de senha e versões de autenticação ao executar a seed novamente. O banco de integração usa um schema novo e é removido ao fim do teste.

Docker recompilou e empacotou o JAR e substituiu a imagem app: BUILD SUCCESS. A fase de package da imagem executa 40 testes e ignora as oito integrações, pois não possui conexão de teste; estas foram aprovadas separadamente pelo serviço verify. Node executou os cinco testes de speech.test.mjs: todos aprovados.

A imagem atual respondeu login nas três lojas demo e administração global. Requisições HTTP verificaram dashboard, produtos (incluindo os novos campos de saldo inicial de grade), posições, usuários, vínculos, movimentação, baixa rápida, mapa, histórico e estorno. Ocupação, busca assistida, configuração IoT e exportações CSV/PDF retornaram 200 nas variantes habilitadas e 403 em essencial. A balança retornou 1,425 kg líquido e subtotal 42,61; o LED recebeu confirmação simulated=true. Esses são testes HTTP, não inspeção visual de navegador ou prova de hardware físico.

O banco existente manteve as três lojas demo com quatro usuários, quatro produtos, oito posições e quatro vínculos em cada uma; a loja antiga de testes também foi preservada. Após reiniciar app, contagens e hashes de todos os registros de products, occupancy e movements permaneceram idênticos; /health e /login voltaram a responder 200.

Evidências em evidence/2026-10-13: docker-verify-final.log (trecho final da execução), docker-build-final.log (trecho final da construção), windows-maven-test.log, http-demo-current.json e restart-preservation.log. O roteiro com contas públicas autorizadas está em DEMONSTRACAO_MVP.md. **Naquele checkpoint, os CRUDs e exemplos de padrões da atividade ainda não estavam concluídos.** Essa conclusão foi superada em 14/10/2026 pela implementação da Opção 01 e pela validação documentada acima; consulte `docs/OPCAO_01.md` e `evidence/2026-10-14/opcao-01/` para o aceite atual.

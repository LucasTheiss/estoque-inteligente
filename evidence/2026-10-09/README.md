# Evidências — 09/10/2026

## O que foi executado

- Relatórios Surefire: 23 casos MVC, dois de estoque e dois de endereço MQTT aprovados; sete integrações ignoradas.
- grade-maven-test.log: falha da compilação principal após alterar a grade.
- grade-maven-diagnostic.log: falha de testCompile com AccessDeniedException em snakeyaml-2.4.jar.
- grade-surefire.log: execução direta dos artefatos de teste anteriores, sem validar os testes novos.
- grade-source-manifest.json: hashes dos sete arquivos da implementação/regressão atual.

Os casos MVC usam serviços simulados. Não há evidência de persistência, HTTP por socket, áudio, hardware ou navegador. Nenhum E2E Playwright foi executado.

## Pendências para retomar

1. Resolver acesso do compilador ao cache e executar as novas regressões, build integral e empacotamento.
2. Disponibilizar PostgreSQL e MQTT: Docker não encontrou daemon; pg_ctl falhou com token restrito (87). Confirmar startup Spring, Flyway e health por HTTP real.
3. Executar oito testes integrados do fonte, incluindo saldo inicial por grade e rollback.
4. Executar Playwright por loja/perfil: login/logout, usuários/permissões, produtos/grades, posições/vínculos, entrada/saída/ajuste/estorno, busca/mapa, CSV/PDF, IoT e isolamento; guardar evidências por funcionalidade.
5. Validar migração de dados e hashes de senha do legado, sem excluir registros.
6. Concluir requisitos externos Alexa e homologação de hardware; conferir lacunas de mapa 2D e edição de características na especificação.
7. Executar os testes JavaScript de voz; o processo Node recebeu EPERM antes de carregar o teste.
8. Atualizar o índice Cartographer com Bun disponível. O complemento do módulo foi atualizado manualmente.
9. Commit e push quando .git permitir escrita. A política desta sessão o monta como somente leitura.

A etapa posterior de frontend/UX permanece separada do aceite funcional, conforme orientação do usuário.

## Continuação após iniciar Docker

O daemon passou a existir, mas docker version recebeu permission denied no pipe docker_engine. Consulte docker-after-start.log. A correção de recuperação de sessão em ContextoViewAdvice e duas regressões em TelasTest foram acrescentadas após os testes registrados acima; não foram executadas. Os hashes desse checkpoint estão em sessao-source-manifest.json.

## HTTP real após a infraestrutura ficar acessível

http-live.json contém 86 registros da aplicação Java em http://127.0.0.1:8080.
Os testes usaram a loja isolada e2e-mv1djzc9, com quatro perfis, além das contas
demo para isolamento e MQTT. Os dados da loja de teste foram preservados.
http-history.csv e http-history.pdf são exportações reais obtidas durante o fluxo.

Cadastros, grade de variantes, vínculos, estoque inteiro/fracionado, ajuste,
idempotência, estorno, proteções de saldo, permissões, isolamento, CSRF, busca
JSON, concorrência, exclusão lógica, histórico e logout tiveram verificações
bem-sucedidas. O transporte MQTT retornou peso líquido 1,425 kg e ACK simulado.

A imagem publicada é anterior às alterações novas: os campos de saldo inicial
da grade estão ausentes. O retorno a /login com sessão revogada retornou 403,
reproduzindo o defeito corrigido no fonte. Não apresentar a imagem como contendo
as correções nem os 86 registros como suíte JUnit ou Playwright.

Playwright não forneceu um navegador utilizável: tentativa de inicialização
expirou e o runtime reiniciou. A inspeção posterior dos processos foi negada.
Os testes feitos nesta etapa foram HTTP, sem renderização visual/interação JS.

Próximo passo: reconstruir e validar a imagem com as alterações atuais, repetir
a recuperação de sessão e o saldo inicial de grade, então concluir Playwright,
migração legada e demais pendências acima. O acesso ao daemon e a escrita em
.git continuam restritos nesta sessão.

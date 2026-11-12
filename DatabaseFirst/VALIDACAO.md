# Validação executada em 06/11/2026

## Resultado

| Verificação executada | Resultado real |
|---|---|
| PostgreSQL Docker isolado | PostgreSQL 17.11 iniciado, healthcheck aprovado |
| Flyway | 3 migrations originais aplicadas; versão final 3 |
| jOOQ gratuito 3.19.26 | Conexão JDBC real, 10 tabelas e 45 arquivos Java gerados |
| Compilação da demonstração | 49 fontes: 45 geradas + 4 escritas manualmente; Java 21.0.9 |
| Testes Database First, esquema inicial | 6 aprovados, zero falhas/erros/ignorados |
| CRUD HTTP real com login | CREATE, READ, UPDATE e DELETE aprovados; cada etapa conferida via psql |
| Autenticação e CSRF | Redirecionamento ao login; POST sem token rejeitado com 403; perfil sem papel DEMO rejeitado |
| Proteção no startup | Execução com perfil `invalid` ou banco `postgres` recusada com exit code 1 e mensagem específica |
| Regras do CRUD | Nome/limite, unicidade, isolamento de loja, bloqueio de exclusão vinculada e auditoria verificados |
| Adição de `demo_note` e regeneração | `DEMO_NOTE`, `getDemoNote()` e `setDemoNote(...)` gerados |
| Compilação e CRUD após adicionar coluna | 6 testes aprovados |
| Restauração do esquema e regeneração | 6 testes aprovados; SHA-256 do Record igual ao inicial |
| Estado final do banco | Zero modelos da loja de demonstração; zero colunas `demo_note` |
| Build e testes Java do sistema original | 75 aprovados, zero falhas/erros/ignorados, PostgreSQL e MQTT reais isolados |
| Testes JavaScript originais | 13 aprovados, zero falhas |
| Documento Word | DOCX gerado; integridade ZIP e XML dos componentes validada |

Os seis testes da demonstração foram executados três vezes: esquema inicial, com coluna nova e restaurado. Não são 18 testes diferentes. O teste HTTP adicional usa servidor e sessão reais; os testes JUnit usam MockMvc com PostgreSQL real e rollback de fixtures.

## Evidências

- `evidence/http-crud.json`: resultados HTTP e consultas independentes por `psql`, incluindo três entradas de auditoria.
- `evidence/ModelsRecord.before.java.txt` e `evidence/ModelsRecord.after.java.txt`: cópias integrais dos arquivos efetivamente produzidos pelo gerador.
- `evidence/schema-after.txt`: saída real do PostgreSQL, com histórico Flyway e coluna adicional.
- `evidence/regeneration.json`: confirmações e hashes antes/restaurado.
- `evidence/tests-baseline.txt`, `tests-added-column.txt`, `tests-restored.txt`: relatórios reais Surefire da demonstração.
- `evidence/main-tests/`: relatórios reais Surefire dos oito conjuntos de testes originais.
- Relatórios completos locais: `DatabaseFirst/target/surefire-reports` e `target/surefire-reports`.

O hash do `ModelsRecord.java` inicial e restaurado foi:

```text
5A774C5A1A650D8346D26B663FB8505FB7946A84B6FDDA30A79243152F9859C1
```

## Arquitetura analisada e decisão

O projeto raiz é um único artefato Maven, Java 21, Spring Boot 3.5.6. Os pacotes `controller/web`, `controller/api`, `service`, `repository`, `model`, `dto`, `config`, `security`, `integration`, `strategy` e `view` separam MVC, JDBC, regras de estoque, autenticação, MQTT e relatórios. Os controllers de modelos, produtos e posições usam serviços e templates Thymeleaf; a segurança original exige autenticação e usa CSRF.

O PostgreSQL original usa `postgres:17-alpine`, porta local 5433 e volume `java-database`. A demonstração usa outro projeto Compose, outro volume, porta 5544 e banco `estoque_databasefirst`. Nenhum container do Compose raiz foi iniciado para esta tarefa e nenhuma conexão foi direcionada ao banco original.

O esquema original contém dez tabelas: `tenants`, `users`, `role_permissions`, `models`, `products`, `positions`, `occupancy`, `movements`, `audit`, `login_attempts`. Há chaves compostas por loja, restrições de quantidades, unidades, preços, posições e histórico. V2 adiciona ativação de lojas; V3 separa relatórios e ocupação. Essas migrations são referenciadas diretamente pelo Maven da demonstração.

`ModeloProdutoService` permite exclusão física apenas sem produtos vinculados. `ProdutoService` desativa produtos e protege estoque/histórico. Por isso o CRUD de demonstração usa `models`, em uma loja exclusiva, e preserva verificação de vínculos, isolamento, normalização dos campos, unicidade e auditoria. Não reutiliza o serviço principal porque ele exige o contexto de usuários e permissões do sistema de lojas. A autenticação da demonstração é separada, com papel DEMO e usuário em memória; o ator da auditoria é nulo por não corresponder a um usuário de `public.users`.

A versão jOOQ 3.19.26 foi obtida da propriedade `jooq.version` do Spring Boot 3.5.6 e fixada no módulo. Gerador e runtime usam a mesma versão. A compilação e execução em Java 21 e PostgreSQL 17 confirmaram a compatibilidade. O namespace gratuito é `org.jooq`.

O gerador inclui todo o esquema `public`, exceto `flyway_schema_history`, para preservar metadados dos relacionamentos. Na apresentação, os arquivos de `models`, `products`, `positions` e `tenants` são os mais relevantes. A tabela de CRUD é `models`.

## Arquivos entregues

Todo o trabalho desta tarefa fica em `DatabaseFirst/`:

- `pom.xml`, `compose.yaml`, `.gitignore`: módulo independente, infraestrutura e exclusão de segredos/artefatos.
- `demo.ps1`: preparação, geração, execução, inspeção, testes, alteração, restauração e parada.
- `sql/`: seed da loja exclusiva, inspeção, coluna de exemplo e restauração.
- `src/main/java/br/com/estoque/databasefirst/`: aplicação, segurança, Service e Controller.
- `src/main/resources/`: configuração, template manual e CSS.
- `src/test/java/br/com/estoque/databasefirst/DatabaseFirstTest.java`: testes de integração e segurança.
- `target/generated-sources/jooq/`: classes produzidas pelo gerador, presentes localmente, ignoradas no Git.
- `scripts/`: teste HTTP, teste de regeneração, configuração do broker de testes e geração do DOCX.
- `ROTEIRO_DEMONSTRACAO.md`, `DatabaseFirst_ParteIII.docx`, `VALIDACAO.md`, `evidence/`: roteiro, entrega e comprovação.

Nenhum arquivo fonte, POM, Compose ou migration original foi alterado. As exclusões e pastas de outras entregas que já apareciam no Git no início da tarefa foram preservadas.

## Limites e ocorrências

- Docker estava desligado; foi iniciado. Maven/Node/Docker exigiram execução fora do sandbox para acesso ao cache, subprocessos e daemon. As verificações foram repetidas com o acesso necessário.
- A primeira tentativa HTTP criou um registro e encontrou bloqueio de acesso ao Docker na conferência SQL. A repetição autorizada completou o CRUD; o registro daquela tentativa foi removido pela restauração da loja exclusiva.
- A primeira execução da suíte principal ignorou MQTT por falta do broker. Após iniciar o broker isolado, a suíte inteira passou sem ignorados. O teste usa dispositivos simulados: não verifica hardware físico.
- jOOQ emite um aviso de nome ambíguo para o caminho de join implícito da autorreferência de `movements`. Isso não afeta as chaves em `Keys.java`, a compilação, nem o CRUD de `models`; nenhuma estratégia customizada foi necessária.
- A interface foi validada por renderização Thymeleaf nos testes e por HTTP real. Não foi feita inspeção visual em navegador nem inventada captura de tela.
- O Word teve validação estrutural; não foi aberto visualmente no Microsoft Word.
- A skill Cartographer referenciava caminhos de outro computador e não havia mapa/CLI local. O mapeamento foi feito diretamente nos fontes. O carregador da skill Impeccable não pôde usar seu cache; PRODUCT.md e DESIGN.md foram lidos diretamente.
- A aplicação não aplica migrations no startup: somente `prepare` faz isso. O startup exige o perfil `databasefirst`, o banco exclusivo e a loja preparada. O Compose publica a página apenas em `127.0.0.1:8082`; credenciais aleatórias ficam no `.env` local ignorado pelo Git.

## Execucao dos scripts da apresentacao em 12/11/2026

O fluxo foi executado pelos novos scripts PowerShell com Docker Desktop ativo:

- `iniciar-banco.ps1` subiu o PostgreSQL 17.11 isolado, aplicou as tres migrations Flyway com sucesso e criou a loja `databasefirst`.
- `gerar-e-iniciar-demo.ps1` conectou o jOOQ ao banco e gerou 45 arquivos Java: tabelas, Records, POJOs, DAOs e chaves.
- O comando `export-bootify` gravou o SQL de `tenants` e `models` em `DatabaseFirst/target/bootify-schema.sql`.
- Maven compilou 49 fontes e executou 6 testes: zero falhas, erros ou ignorados.
- A aplicacao web iniciou e `/login` respondeu HTTP 200.

O PostgreSQL e a aplicacao continuaram ativos ao concluir a verificacao HTTP. O SQL exportado e as classes geradas ficam sob `target/` e nao sao versionados. A importacao e a geracao do projeto Bootify nao foram automatizadas: essa etapa acontece no site, conforme `BOOTIFY_AUTOMATICO.md`.

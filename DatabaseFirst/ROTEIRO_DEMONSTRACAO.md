# Demonstração presencial — Database First

Execute os comandos em **PowerShell, na raiz do repositório**. Requisitos: Docker Desktop iniciado com containers Linux. Java 21 e Maven 3.9.11 são fornecidos pelo container; o Java 24 instalado no Windows não é utilizado. A primeira execução baixa imagens e dependências: faça a preparação antes da aula, com internet.

```powershell
cd C:\Users\cristhian.ruan\Documents\estoque-inteligente
```

## 1. Preparação

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 prepare
```

O script cria o projeto Compose `estoque-databasefirst`, banco `estoque_databasefirst`, volume próprio e credenciais aleatórias em `DatabaseFirst/.env` (ignorado pelo Git). Guarde esse arquivo para reutilizar o volume. Não há leitura do `.env` do sistema principal. O PostgreSQL fica em `127.0.0.1:5544`, separado da porta 5433 original.

Flyway aplica **os arquivos originais**, sem cópia nem alteração:

- `src/main/resources/db/migration/V1__estoque.sql`
- `src/main/resources/db/migration/V2__lojas_ativas.sql`
- `src/main/resources/db/migration/V3__relatorios_ocupacao_independentes.sql`

Mostre que o banco existe antes de gerar Java:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 inspect
```

Esse comando exibe o banco, a versão PostgreSQL, histórico Flyway, colunas de `models` e registros da loja exclusiva. Para abrir um console SQL:

```powershell
docker compose --env-file DatabaseFirst/.env -f DatabaseFirst/compose.yaml exec db psql -U df_demo -d estoque_databasefirst
```

Dentro do console: `\d public.models`, `\d public.products`, `\d public.positions`. Use `\q` para sair.

## 2. Geração automática

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 generate
```

Comando Maven equivalente, executado no container com conexão configurada:

```powershell
docker compose --env-file DatabaseFirst/.env -f DatabaseFirst/compose.yaml run --rm maven mvn -B -ntp -Pcodegen generate-sources
```

Mostre as mensagens `Generating table`, `Generating record`, `Generating POJO` e `Generating DAO`. O plugin oficial `org.jooq:jooq-codegen-maven:3.19.26` conecta via JDBC ao **PostgreSQL em execução** e consulta os metadados de `public`. Não lê DDL para simular banco nem usa classes escritas à mão.

O gerador fica em `DatabaseFirst/pom.xml`, perfil `codegen`. `mvn verify` normal não gera novamente. Após `clean`, execute `generate` antes de compilar, ou `mvn -Pcodegen clean verify` com banco preparado.

## 3. Código gerado

Abra `DatabaseFirst/pom.xml` como projeto Maven no IntelliJ ou VS Code. Os arquivos só existem após gerar. Caminhos relativos à raiz do repositório:

- `DatabaseFirst/target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/Models.java` — tabela, colunas e tipos.
- `DatabaseFirst/target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/records/ModelsRecord.java` — Record com getters, setters e persistência herdada do jOOQ.
- `DatabaseFirst/target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/pojos/Models.java` — POJO.
- `DatabaseFirst/target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/daos/ModelsDao.java` — DAO com operações de acesso herdadas e consultas geradas.
- `DatabaseFirst/target/generated-sources/jooq/br/com/estoque/databasefirst/generated/Keys.java` — chaves primárias, únicas e estrangeiras, inclusive relacionamentos compostos por loja.
- Na pasta `tables`, mostre também `Products.java`, `Positions.java` e `Tenants.java`.

O esquema inteiro é gerado, exceto o histórico Flyway, para conservar referências entre tabelas. A apresentação se concentra nessas quatro entidades. Não edite arquivos em `target`: eles são descartáveis e regeneráveis.

## 4. CRUD

Primeiro, execute a validação automatizada:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 test
```

Inicie a aplicação (esse terminal fica ocupado):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 run
```

Em **outro PowerShell**, na raiz, consulte somente as credenciais da tela:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 credentials
```

Abra **http://127.0.0.1:8082/** e entre com o usuário e a senha exibidos. A senha é local e aleatória; evite projetá-la para a turma. Faça as quatro operações:

1. **CREATE:** cadastre `Modelo aula`, marca `Equipe`, categoria `Database First`.
2. **READ:** mostre a linha e seu ID na tabela da página. Execute `inspect` no outro terminal para comprovar no PostgreSQL.
3. **UPDATE:** clique em **Editar**, altere a marca para `Equipe atualizada` e salve. Execute `inspect` novamente.
4. **DELETE:** clique em **Excluir** e execute `inspect`. A linha desaparece da página e do banco.

Mostre `DatabaseFirst/src/main/java/br/com/estoque/databasefirst/ModelService.java`: `db.newRecord(MODELS)`, `model.store()`, `fetchInto(Models.class)` e `model.delete()` usam código gerado. `DatabaseFirstTest` também consulta pelo `ModelsDao` gerado e confere os resultados por JDBC independente.

Explique: **jOOQ gerou classes e código de acesso aos dados; o Controller, o Service e a página Thymeleaf foram escritos manualmente.** jOOQ não gera telas. Adicionar outro framework só para gerar essa página aumentaria a complexidade sem ajudar a comprovar Database First.

A loja da demonstração é fixa e não pode ser escolhida pelo navegador. Todas as consultas e alterações filtram essa loja. Modelos vinculados a produtos não podem ser excluídos; nome obrigatório, limite de 200 caracteres, unicidade e auditoria são preservados. A auditoria da demonstração usa ator nulo, pois o login local em memória é separado dos usuários do sistema principal.

## 5. Alteração no banco

Pare a aplicação com **Ctrl+C** antes de alterar o esquema. Mostre que `demoNote` não existe ainda:

```powershell
Select-String -Path DatabaseFirst/target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/records/ModelsRecord.java -Pattern 'DemoNote'
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 add-column
```

O SQL real em `DatabaseFirst/sql/add-column.sql` verifica o nome do banco e executa:

```sql
ALTER TABLE public.models ADD COLUMN IF NOT EXISTS demo_note TEXT;
```

Execute `inspect` para mostrar a coluna no PostgreSQL. A classe Java ainda permanece como antes: alterar o banco sozinho não reescreve Java.

## 6. Regeneração

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 generate
Select-String -Path DatabaseFirst/target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/records/ModelsRecord.java -Pattern 'DemoNote'
Select-String -Path DatabaseFirst/target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/Models.java -Pattern 'DEMO_NOTE'
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 test
```

Mostre `getDemoNote()`, `setDemoNote(...)` e `DEMO_NOTE`. Nenhuma classe foi editada manualmente. O campo também aparece no POJO e no DAO; o formulário permanece igual, pois não foi gerado.

## 7. Finalização

Com a aplicação parada:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 restore
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 inspect
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 stop
```

`restore` remove somente a coluna de exemplo, os modelos e a auditoria da loja exclusiva, regenera e testa. Não reinicia sequências: IDs podem continuar aumentando. `stop` remove os containers desse projeto Compose, conservando seu volume. Não execute `down -v` no Compose original. A próxima apresentação começa novamente por `prepare` e `generate`.

## Verificação do sistema principal

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 main-test
node --test src/test/js/*.test.mjs
```

`main-test` compila e testa o Maven original usando o PostgreSQL exclusivo e um broker MQTT descartável, sem porta publicada no host. Os testes existentes criam seus próprios esquemas. A demonstração não participa do build raiz e não substitui seus repositórios JDBC. Veja `VALIDACAO.md` para resultados efetivamente obtidos, limitações e evidências.

Para repetir automaticamente a prova de regeneração (aplicação parada e esquema inicial):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/scripts/test_regeneration.ps1
```

O script comprova o campo novo, testa com a coluna, restaura, testa novamente e compara SHA-256 do Record antes/depois. Salva as evidências em `DatabaseFirst/evidence`.

Opcional, com a aplicação iniciada e Python 3 instalado, teste HTTP completo com conferência por `psql`:

```powershell
python DatabaseFirst/scripts/http_smoke.py
```

## Referências da ferramenta

- [Configuração oficial do gerador jOOQ](https://www.jooq.org/doc/3.19/manual/code-generation/codegen-configuration/)
- [DAOs gerados](https://www.jooq.org/doc/3.19/manual/code-generation/codegen-object-types/codegen-daos/)
- [Integração jOOQ e Spring Boot](https://docs.spring.io/spring-boot/reference/data/sql.html)

A versão gratuita `org.jooq` 3.19.26 é a mesma gerenciada pelo Spring Boot 3.5.6; executamos a demonstração em Java 21 e PostgreSQL 17.

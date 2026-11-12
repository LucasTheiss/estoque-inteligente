# Gerar entidades, CRUD e telas pelo esquema

Este fluxo importa no Bootify o esquema PostgreSQL usado pelo jOOQ. Assim a demonstracao cobre desde a leitura do banco ate a geracao das camadas web.

## Produzir o SQL de importacao

Rode na raiz do repositório:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/iniciar-banco.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/gerar-codigo.ps1
```

O segundo script gera as classes jOOQ em `DatabaseFirst/target/generated-sources/jooq/` e exporta as tabelas `tenants` e `models` para `DatabaseFirst/target/bootify-schema.sql`. A exportacao vem do PostgreSQL ativo e nao inclui linhas de dados.

## Gerar o projeto web

1. Abra [Bootify](https://bootify.io/) e crie um projeto Java/Spring Boot com PostgreSQL e Thymeleaf.
2. Importe o SQL de `DatabaseFirst/target/bootify-schema.sql` e examine o preview do esquema.
3. Habilite CRUD e frontend para `models`. Mantenha `tenants` como tabela de apoio sem tela CRUD.
4. Escolha **None (validate)** para mapear um esquema que ja existe, sem recria-lo.
5. Gere e baixe o projeto. Abra uma entidade JPA, o serviço/controlador gerado e os templates Thymeleaf.

O Bootify documenta a [importacao de um esquema SQL existente](https://bootify.io/spring-data/generate-entities-from-database-sql-script.html) e a [geracao de CRUD e frontend por entidade](https://bootify.io/frontend/spring-boot-crud-generator.html). Recursos SQL que a ferramenta nao reconhece podem exigir ajuste no preview.

## Limites

Os scripts deste repositorio automatizam a preparacao do banco, a geracao jOOQ e a exportacao SQL. A importacao e a configuracao das opcoes do Bootify ainda sao feitas no site; o projeto baixado e um scaffold separado.

A tabela `models` tem isolamento por `tenant_id`, relacao com a loja e unicidade composta. O codigo generico gerado deve ser revisado antes de ser conectado a dados reais. A atividade usa o projeto Bootify para demonstrar geracao de telas e CRUD; nao copia esse codigo para o sistema principal.

Para os comandos completos e a fala de apresentacao, consulte [PASSO_A_PASSO_PROFESSOR.md](PASSO_A_PASSO_PROFESSOR.md).

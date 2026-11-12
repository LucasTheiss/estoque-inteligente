# Validacao da geracao Database First

## Fluxo executado

Em 12/11/2026, com Docker Desktop ativo, foi executado o fluxo de geracao:

| Etapa | Resultado |
| --- | --- |
| PostgreSQL isolado | PostgreSQL 17.11 iniciou e passou no healthcheck. |
| Flyway | As tres migrations originais foram aplicadas com sucesso. |
| jOOQ Code Generator | Leu o esquema PostgreSQL ativo e gerou 45 fontes Java: tabelas, Records, POJOs, DAOs e chaves. |
| Exportacao Bootify | Exportou `tenants` e `models` para `target/bootify-schema.sql`, sem dados de negocio. |
| Verificacao do esquema | A loja `databasefirst` foi criada no banco isolado. |

O codigo gerado e o SQL ficam sob `DatabaseFirst/target/` e sao recriados pelos scripts; esses artefatos nao sao versionados. A importacao e a geracao do projeto Bootify acontecem no site e nao fazem parte de uma verificacao automatizada local.

## Regeneracao apos uma alteracao

O script opcional `scripts/test_regeneration.ps1`, chamado por:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/gerar-codigo.ps1 -DemonstrarRegeneracao
```

adiciona `demo_note` ao banco isolado, confirma que o Java nao muda antes do jOOQ, executa a geracao, procura o novo getter/setter/campo tipado e restaura o esquema. As evidencias temporarias ficam em `DatabaseFirst/target/evidence/` e incluem os Records antes/depois, o esquema alterado e os hashes usados para confirmar a restauracao.

## Limite

Esta branch mantem o pipeline de geracao, nao uma aplicacao MVC escrita manualmente. O jOOQ gera codigo de acesso ao banco. O Bootify gera entidades, CRUD e telas a partir do SQL depois da selecao das opcoes no site; o codigo resultante precisa ser revisado para respeitar as regras de isolamento e autorizacao do sistema.

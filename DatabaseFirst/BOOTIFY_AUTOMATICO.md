# Gerar entidades, CRUD e telas a partir do esquema

Esta etapa complementa a demonstração jOOQ com um gerador de aplicação que cria também as camadas web. O jOOQ continua sendo a demonstração executável e validada dentro deste repositório; o Bootify é a opção de scaffolding para gerar entidades JPA, serviços/DTOs, endpoints CRUD e frontend a partir de SQL.

## Exportar o esquema real

Na raiz do repositório, com Docker iniciado:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 prepare
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 export-bootify
```

O comando consulta o PostgreSQL exclusivo criado pela demonstração e grava `DatabaseFirst/target/bootify-schema.sql`. O arquivo contém as tabelas reais `tenants` e `models`; `models` depende de `tenants`. Ele não contém dados nem a tabela de controle do Flyway.

## Gerar o projeto e a interface

1. Abra [Bootify](https://bootify.io/) e crie um projeto Spring Boot com Java, PostgreSQL e frontend Thymeleaf.
2. Na área de entidades, importe o conteúdo de `DatabaseFirst/target/bootify-schema.sql` como esquema SQL existente.
3. Confira o preview das tabelas e relações. Ative CRUD e frontend para `models`; mantenha `tenants` como entidade de apoio sem tela CRUD.
4. Configure a geração do banco como **None (validate)** para o projeto gerado validar o esquema existente em vez de recriá-lo.
5. Gere e baixe o projeto. A ferramenta cria as entidades e os arquivos de serviço, controlador e tela associados às opções selecionadas.

O Bootify documenta a importação de esquema SQL e a seleção de CRUD/frontend por entidade. Alguns recursos do PostgreSQL podem não ser representados pelo importador; confira o preview antes de gerar. [Importar SQL e gerar entidades](https://bootify.io/spring-data/generate-entities-from-database-sql-script.html), [opções de CRUD e frontend](https://bootify.io/frontend/spring-boot-crud-generator.html).

## Como apresentar

Mostre primeiro a origem: `prepare` cria o banco, e `export-bootify` lê o esquema ativo. No Bootify, mostre o preview após importar o arquivo, marque CRUD para `models` e gere a aplicação. Abra no projeto baixado uma entidade, o controlador/serviço e a página Thymeleaf gerados. Isso demonstra a geração automática das três partes a partir do mesmo esquema.

Para demonstrar execução real neste repositório, continue com o fluxo jOOQ em [ROTEIRO_DEMONSTRACAO.md](ROTEIRO_DEMONSTRACAO.md): as classes e DAOs são gerados a partir do PostgreSQL, e a aplicação demonstra CREATE, READ, UPDATE e DELETE com login, isolamento da loja e auditoria.

## Limite de integração

O código baixado do Bootify é um scaffold separado; não é copiado automaticamente para a aplicação principal nem para a demonstração jOOQ. A tabela `models` usa isolamento por `tenant_id`, chave/índices compostos e regras de exclusão. Um CRUD scaffold genérico não deve ser ligado diretamente a dados reais sem adaptar essas regras e rever autorização. Para a entrega, a geração do Bootify comprova o scaffolding de CRUD e telas; o fluxo jOOQ existente comprova as operações seguras em execução.

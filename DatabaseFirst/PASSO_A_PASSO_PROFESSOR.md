# Roteiro Database First para apresentacao

## O que a atividade demonstra

O ponto de partida e um esquema PostgreSQL existente. O jOOQ le esse esquema e gera classes e DAOs Java. Para gerar tambem entidades, CRUD e telas, o mesmo banco e exportado para SQL e importado no Bootify. Este branch contem somente a infraestrutura, os scripts e as instrucoes para esses geradores; nao contem um CRUD MVC escrito manualmente.

## Preparar e gerar

Pre-requisitos: Docker Desktop iniciado, internet na primeira execucao e terminais PowerShell abertos dentro da pasta estoque-inteligente\DatabaseFirst. Confira com Get-Location; os comandos abaixo usam essa pasta como ponto de partida.

### Terminal 1: criar o banco de demonstracao

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-banco.ps1
```

O script inicia o PostgreSQL isolado na porta local `5544`, aplica as migrations originais pelo Flyway e insere uma loja de demonstracao. O Flyway cria/atualiza o banco; ele nao gera classes Java.

### Terminal 2: gerar os artefatos

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\gerar-codigo.ps1
```

O script gera 45 arquivos Java jOOQ em `target/generated-sources/jooq/` e exporta `tenants` e `models` para `target/bootify-schema.sql`. Nao inicia uma aplicacao MVC manual.

Para demonstrar que uma mudanca no banco altera o codigo gerado, rode esta variante. Ela adiciona `demo_note`, regenera as classes e restaura o esquema ao final:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\gerar-codigo.ps1 -DemonstrarRegeneracao
```

O arquivo SQL exportado e a prova principal para a etapa seguinte. Para conferir a estrutura atual do banco:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\demo.ps1 inspect
```

## Gerar CRUD e telas no Bootify

1. Abra [Bootify](https://bootify.io/) e crie um projeto Java com PostgreSQL e Thymeleaf.
2. Importe o conteudo de `target\bootify-schema.sql`, relativo à pasta `DatabaseFirst` onde seu terminal está.
3. Confira o preview. Habilite CRUD e frontend para `models`; mantenha `tenants` como entidade de apoio.
4. Selecione **None (validate)** para o projeto validar o esquema existente em vez de tentar criá-lo.
5. Gere e baixe o código. Mostre a entidade, o serviço/controlador CRUD e a tela Thymeleaf gerados.

Para abrir o site e usar o CRUD no banco existente, siga [BOOTIFY_AUTOMATICO.md](BOOTIFY_AUTOMATICO.md). Esse roteiro mostra os ajustes de tabelas, sequências e Flyway, a leitura das credenciais e os comandos para compilar e abrir `http://localhost:8083/models`. Trocar somente o nome do projeto ou do banco não configura esses mapeamentos.

O Bootify oferece importacao de esquema SQL e opcoes de CRUD/frontend por entidade ([guia de importacao](https://bootify.io/spring-data/generate-entities-from-database-sql-script.html), [guia de CRUD/frontend](https://bootify.io/frontend/spring-boot-crud-generator.html)). A selecao das opcoes e o download acontecem no site; nao existe uma chamada automatica ao Bootify nestes scripts.

## O que cada ferramenta faz

| Ferramenta | Funcao | Explicacao para o professor |
| --- | --- | --- |
| PostgreSQL | Banco de origem isolado para a atividade. | “O esquema do banco e o ponto de partida.” |
| Flyway | Aplica migrations versionadas ao PostgreSQL. | “Flyway prepara o esquema; nao cria as classes Java.” |
| jOOQ Code Generator | Le metadados JDBC e gera tipos Java, Records, POJOs, DAOs e chaves. | “O jOOQ consulta as tabelas existentes e escreve codigo tipado automaticamente.” |
| Maven | Executa Flyway e o perfil `codegen` no container Java 21. | “O Maven coordena os plugins e as dependencias de geracao.” |
| Bootify | Importa SQL e gera um scaffold de aplicacao. | “Ao marcar CRUD e Thymeleaf para a entidade, gera entidades, operacoes e telas.” |
| PowerShell | Encadeia os passos repetiveis. | “Um script prepara o banco; outro regenera os artefatos.” |

Uma fala curta:

> “Primeiro subimos um PostgreSQL isolado e aplicamos as migrations. O jOOQ consulta o esquema real e gera classes e DAOs Java. Depois exportamos as tabelas necessarias e importamos esse SQL no Bootify, que gera as entidades, o CRUD e as telas selecionadas. Assim mostramos Database First desde o banco ate o codigo de acesso e a interface.”

## Encerrar

Depois de salvar ou mostrar os arquivos gerados, pare os containers sem apagar o volume:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\demo.ps1 stop
```

Nao use `down -v`: esse comando apagaria o banco e o cache Maven.

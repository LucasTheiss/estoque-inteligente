# Abrir o CRUD Bootify no banco existente

Todos os comandos abaixo partem de `estoque-inteligente\DatabaseFirst`.

## 1. Preparar o banco

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-banco.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\gerar-codigo.ps1
```

Importe `target\bootify-schema.sql` no Bootify e preencha as configuracoes abaixo. Depois habilite CRUD/frontend nas entidades e baixe o projeto.

### Project settings

| Campo | O que selecionar |
| --- | --- |
| Project group | `estoque-inteligente` |
| Project name | `ei-front` |
| Build type | **Maven** (o iniciador usa `mvnw.cmd`) |
| Language | **Java** |
| Enable Lombok | **Desmarcado** |
| Frontend | **Thymeleaf** |
| Build tool | **Vite** |
| CSS framework | **Bootstrap** |
| Include htmx | **Desmarcado** |
| Use TypeScript | **Desmarcado** |

### Database settings

| Campo | O que selecionar |
| --- | --- |
| Database | **PostgreSQL** |
| DB Generation | **None (validate only)**; pode aparecer abreviado como **None (validate)** |
| Pluralize table names | **Marcado**, para usar nomes plurais como `models` e `tenants` |
| Add dateCreated/lastUpdated to entities | **Desmarcado**; nao queremos acrescentar campos que nao existem nas tabelas importadas |

Se DB Generation estiver em **Flyway**, troque para **None (validate only)**. O Flyway dos nossos scripts ja prepara o banco. O aplicativo gerado deve apenas validar esse esquema, conforme o [guia oficial de importacao SQL do Bootify](https://bootify.io/spring-data/generate-entities-from-database-sql-script.html). O iniciador tambem desativa Flyway para acomodar downloads anteriores feitos com essa opcao.

### Developer preferences

| Campo | O que selecionar |
| --- | --- |
| Appl. format | **Yaml** |
| Packages | **Domain driven** |
| Include coding agent instructions | **Desmarcado**; e opcional e nao faz parte da demonstracao |
| Enable OpenAPI/Swagger UI | **Desmarcado**; vamos demonstrar pelas telas |
| Use Docker compose for development | **Desmarcado**; vamos usar o banco do `compose.yaml` de DatabaseFirst |
| Java version | **17**, para gerar codigo compativel com o JDK 19 encontrado anteriormente nesta maquina |
| Deployment | **None** |
| Further dependencies | **Deixe vazio**; use as dependencias adicionadas pelo Bootify |

Estas sao as escolhas para este roteiro. Java 17 aqui e a versao alvo do codigo gerado; o iniciador detecta o JDK instalado e passa a versao dele ao Maven. Nao selecione 21 ou 25 enquanto estiver usando somente o JDK 19. Para trocar, instale o JDK correspondente e configure JAVA_HOME.

### Entidades e download

1. Importe o SQL de `target\bootify-schema.sql`.
2. Confira as entidades **Model** e **Tenant** e o relacionamento do modelo com a loja. Preserve os campos e tipos importados; nao adicione campos de auditoria.
3. Habilite a geracao de **CRUD/frontend** para Model e Tenant, para termos as telas `/models` e `/tenants`. Escolher Thymeleaf no projeto nao substitui a selecao das telas por entidade.
4. Confira os nomes das tabelas: `models` e `tenants`. O preparador tambem aplica esses nomes e ajusta as sequences existentes automaticamente.
5. Baixe e extraia em `bootify\ei-front`: essa pasta deve conter `pom.xml`, `mvnw.cmd` e `src` diretamente, sem uma segunda pasta `ei-front` dentro dela.

O download continua fora do Git. Repita essas escolhas quando gerar outro projeto.

## 2. Configurar, compilar e abrir

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-bootify.ps1
```

O script ajusta automaticamente as entidades, le as credenciais de `.env`, desativa o Flyway do aplicativo, exige a validacao do esquema existente, compila com o Maven Wrapper e inicia o JAR. Ele usa o Java de `JAVA_HOME`, ou do PATH se JAVA_HOME nao estiver definido, e passa essa versao ao Maven sem editar o POM. Instale um JDK completo (com javac), compativel com o codigo baixado; o script exige pelo menos Java 17. Se o download usar recursos de Java mais recente, selecione esse JDK em JAVA_HOME.

A primeira compilacao precisa de internet para baixar dependencias e ferramentas do frontend. Aguarde `BUILD SUCCESS` e a mensagem `Started` do aplicativo. Abra:

- http://localhost:8083/models — cadastrar, consultar, editar e excluir modelos.
- http://localhost:8083/tenants — lojas.

Para um modelo, selecione a loja `Loja exclusiva Database First`. Para conferir a gravacao, em outro terminal dentro de DatabaseFirst:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\demo.ps1 inspect
```

Se o download tiver outro nome, informe o caminho relativo a DatabaseFirst (ou um caminho absoluto):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-bootify.ps1 -Projeto .\bootify\meu-projeto
```

Nao e preciso mudar o nome do projeto para o nome do banco. A conexao usa `estoque_databasefirst`, porta 5544. O banco e preparado por Docker/Flyway; jOOQ apenas le o esquema e gera codigo.

## O que foi automatizado e por que

| Ajuste | Motivo |
| --- | --- |
| Model usa a tabela models; Tenant usa tenants | Os nomes Java gerados nao correspondem automaticamente aos nomes plurais do banco. |
| IDs usam models_id_seq e tenants_id_seq, com allocationSize 1 | Essas sequences ja existem e fornecem os proximos IDs. A primary_sequence do download nao e usada. |
| Flyway do aplicativo desligado durante a execucao | O banco ja tem migrations e tabelas; a migration inicial do download conflita com esse historico. |
| Hibernate em validate | Confere os mapeamentos sem recriar as tabelas. |
| Credenciais lidas do .env | Reutiliza o banco da atividade sem gravar senha nos fontes Java. |
| Maven Wrapper compila e empacota | Executa o build do backend e do frontend configurado no POM baixado. |

`preparar-bootify.ps1` altera somente as duas entidades, preservando as demais propriedades e as restricoes unicas. Salva o primeiro original ao lado de cada Java com extensao `.databasefirst.bak`. Valida os dois arquivos antes de escrever e pode ser executado novamente. Para apenas aplicar esses ajustes, sem compilar:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\preparar-bootify.ps1
```

`iniciar-bootify.ps1` chama esse preparador e configura a conexao/Flyway no processo. Portanto, use esse iniciador toda vez; abrir o JAR diretamente nao reaplica essas variaveis. Nenhuma tabela ou sequence e criada por esses dois scripts.

## Roteiro para explicar ao professor

1. Mostre o banco preparado e as tabelas existentes.
2. Execute gerar-codigo.ps1: o jOOQ le o banco e gera classes; o script tambem exporta SQL para Bootify.
3. Mostre a importacao e o download do Bootify: ele gera entidades, CRUD e telas.
4. Execute iniciar-bootify.ps1: ele adapta os nomes gerados ao esquema existente, compila e inicia.
5. Cadastre, edite e exclua um modelo na tela. Mostre demo.ps1 inspect para conferir os dados.

Fala sugerida: “O banco e a origem. jOOQ gera o acesso tipado e Bootify gera o aplicativo com CRUD e telas. Nosso script automatiza a adaptacao dos mapeamentos e a execucao; nao escreve os controladores ou as telas.”

Se baixar o projeto novamente, basta extrair e repetir o iniciador. A importacao/download no site continua manual. O projeto baixado fica ignorado pelo Git.

## Limpar depois do ensaio e gerar novamente

Depois de testar, pare o site com Ctrl+C e, dentro de `DatabaseFirst`, execute:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\limpar-gerados.ps1
```

Isso apaga `target` e `bootify` inteiras, incluindo alteracoes locais no download. Preserva os scripts, `.env` e o banco. Para repetir, execute `iniciar-banco.ps1`, `gerar-codigo.ps1`, importe o SQL no Bootify, gere/baixe e extraia o projeto novamente e execute `iniciar-bootify.ps1`. O download no site continua necessario; o iniciador nao o substitui.

O [roteiro de apresentacao](PASSO_A_PASSO_PROFESSOR.md#ensaiar-limpar-e-repetir-na-frente-do-professor) detalha o ensaio, a limpeza e a demonstracao desde os arquivos gerados ausentes.

## Parar e limites de validacao

Pressione Ctrl+C no terminal do site. O script retorna a pasta original. Para parar o banco, dentro de DatabaseFirst:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\demo.ps1 stop
```

Os dados permanecem no volume. Use o banco isolado da atividade: o CRUD gerado permite acessar outras lojas tambem.

A automacao dos mapeamentos foi testada com exemplos do formato gerado, incluindo repeticao e rejeicao de arquivos inesperados. O download ei-front nao estava presente neste checkout durante essa validacao; a compilacao e o uso do site real ainda precisam ser testados com ele.

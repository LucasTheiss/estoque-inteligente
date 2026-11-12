# Como os scripts funcionam: explicacao para o professor

Todos os comandos para executar neste guia partem de **estoque-inteligente\DatabaseFirst**. Os trechos marcados como internos servem para explicar o funcionamento; nao precisam ser executados separadamente.

## Onde eu mando gerar o codigo?

Execute:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\gerar-codigo.ps1
```

Dentro dele, **`& $demo generate`** chama `demo.ps1` com a acao `generate`. Essa acao executa **`mvn -Pcodegen generate-sources`** dentro do container Maven. No [pom.xml](pom.xml), o perfil **codegen** configura o plugin **jooq-codegen-maven**, que consulta o banco e escreve os fontes Java.

Para gerar **o aplicativo com CRUD e telas**, o ponto de geracao e outro: voce importa o SQL no **site do Bootify**, habilita CRUD/frontend e gera/baixa o projeto. `iniciar-bootify.ps1` adapta, compila e executa esse download.

> “gerar-codigo.ps1 aciona o jOOQ por meio do Maven. A geracao do CRUD web e das telas acontece no Bootify, a partir do SQL exportado.”

## Fluxo completo

```text
Migrations SQL -> Flyway -> PostgreSQL existente
                              |
                              +-> jOOQ via JDBC -> classes, Records, POJOs e DAOs
                              |
                              +-> pg_dump -> bootify-schema.sql
                                               |
                                               v
                                       Importacao no Bootify
                                               |
                                               v
                                  Entidades, CRUD e telas gerados
                                               |
                                               v
                                  Download em bootify/ei-front
                                               |
                                               v
                                  Ajustar -> compilar -> executar
```

Sao duas geracoes a partir do mesmo esquema. O aplicativo Bootify usa JPA/Hibernate; ele nao usa as classes jOOQ da outra pasta. As migrations preparam o banco; a etapa Database First e gerar codigo a partir desse banco existente.

## Mapa dos scripts

| Script | Funcao | Resultado |
| --- | --- | --- |
| iniciar-banco.ps1 | Chama demo.ps1 prepare | PostgreSQL pronto e loja de demonstracao |
| gerar-codigo.ps1 | Limpa target, aciona jOOQ e exporta SQL | Java jOOQ e bootify-schema.sql |
| preparar-bootify.ps1 | Ajusta as anotacoes das entidades baixadas | Mapeamentos para tabelas e sequences existentes |
| iniciar-bootify.ps1 | Configura, compila e executa o download | Site em localhost:8083 |
| limpar-gerados.ps1 | Remove target e bootify | Arquivos gerados ausentes para repetir a demonstracao |
| demo.ps1 | Centraliza Docker, Maven e SQL | Executa a acao solicitada |
| scripts/test_regeneration.ps1 | Altera uma coluna, regenera e compara | Evidencias da geracao a partir do banco |

## 1. iniciar-banco.ps1: preparar a origem

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-banco.ps1
```

Ele chama `& $demo prepare`. Em demo.ps1, essa acao executa:

1. `Docker @('up','-d','--wait','db')`: inicia PostgreSQL em segundo plano e aguarda o healthcheck.
2. `Maven @('flyway:migrate')`: aplica as migrations pendentes de `../src/main/resources/db/migration`. O Flyway controla as versoes em `flyway_schema_history`.
3. `Sql 'seed'`: executa `sql/seed.sql`, que insere a loja `Loja exclusiva Database First` se o slug `databasefirst` ainda nao existir.

Se `.env` nao existir, a preparacao cria o arquivo com usuario e senha aleatoria. Docker Compose usa essas variaveis para configurar o banco `estoque_databasefirst`.

O Windows acessa o banco por **localhost:5544**. Entre containers, o Maven acessa **db:5432**, pois db e o nome do servico na rede Docker. O volume `databasefirst-data` preserva os dados entre execucoes. Este script nao gera classes Java.

## 2. gerar-codigo.ps1: o comando de geracao

As linhas internas centrais sao:

```powershell
& $demo clean
& $demo generate
& $demo export-bootify
```

**clean** executa `mvn clean` para remover o target do gerador. Nao e `flyway:clean` e nao apaga o banco. **generate** aciona jOOQ. **export-bootify** exporta a estrutura SQL.

O comando equivalente para executar apenas a geracao jOOQ, dentro de DatabaseFirst, e:

```powershell
docker compose --env-file .\.env -f .\compose.yaml run --rm maven mvn -B -ntp -Pcodegen generate-sources
```

| Parte | Significado |
| --- | --- |
| --env-file / -f | Usa as credenciais locais e o Compose da atividade |
| run --rm maven | Executa o servico Maven em container temporario e o remove ao terminar |
| mvn | Inicia Maven dentro desse container, com Java 21 |
| -B / -ntp | Modo sem perguntas interativas e sem progresso de transferencia no log |
| -Pcodegen | Ativa o perfil codegen no pom.xml |
| generate-sources | Executa a fase associada ao plugin gerador |

### Como o pom.xml configura a geracao

No perfil codegen, procure `jooq-codegen-maven`. Sua secao execution contem:

```xml
<phase>generate-sources</phase>
<goals><goal>generate</goal></goals>
```

Isso liga a fase Maven ao objetivo generate do plugin. A configuracao JDBC fornece URL, usuario e senha por variaveis de ambiente. `PostgresDatabase` le os metadados do PostgreSQL: tabelas, colunas, tipos e chaves.

| Configuracao no POM | Efeito |
| --- | --- |
| inputSchema = public | Seleciona o esquema public |
| includes = .* | Inclui os objetos desse esquema |
| excludes = flyway_schema_history | Exclui a tabela de controle das migrations |
| records / pojos / daos = true | Solicita essas categorias de arquivos Java |
| packageName | Define br.com.estoque.databasefirst.generated |
| directory | Define target/generated-sources/jooq como saida |

O Compose monta o repositorio no container em `/workspace`. Por isso, os arquivos escritos pelo Maven aparecem no Windows.

### O que o jOOQ gera

| Artefato | Funcao |
| --- | --- |
| tables/Models.java | Representa tabela e colunas com tipos Java |
| ModelsRecord.java | Representa um registro associado a tabela, com getters/setters |
| POJO | Objeto Java simples para transportar valores |
| DAO | Metodos de acesso a dados, como inserir, buscar, atualizar e excluir |
| Chaves e referencias | Informacoes do esquema usadas em consultas tipadas |

Um DAO nao e uma tela nem um endpoint HTTP. Gerar esses arquivos nao inicia um servidor; para usar os DAOs numa aplicacao, e preciso configurar o acesso ao banco.

### Como exportamos para Bootify

`export-bootify` executa `pg_dump` no container do banco. Os argumentos centrais sao:

```text
--schema-only --table=public.tenants --table=public.models
```

`--schema-only` exporta estrutura, sem registros. `--no-owner` e `--no-privileges` omitem comandos de proprietario e permissoes. O script grava a saida em `target/bootify-schema.sql`, com UTF-8 sem BOM.

O jOOQ consulta o esquema public mais amplo; o SQL para Bootify seleciona as tabelas da demonstracao de lojas e modelos.

## 3. Bootify: onde nascem o CRUD e as telas

Siga [as configuracoes do Bootify](BOOTIFY_AUTOMATICO.md), importe o SQL, mantenha **Model** e **Tenant** no singular e habilite CRUD/frontend. Gere/baixe e extraia em `bootify\ei-front`.

Mostre estes arquivos do download ao professor:

| Arquivo/pasta | Papel |
| --- | --- |
| Model.java / Tenant.java | Entidades JPA que representam tabelas |
| ModelRepository.java | Interface de acesso implementada pelo Spring Data JPA em execucao |
| ModelService.java | Coordenacao das operacoes e conversao de dados |
| ModelController.java | Recebe requisicoes das telas e chama o servico |
| ModelDTO.java | Dados usados em formularios e operacoes |
| resources/templates/models | Templates Thymeleaf de listagem, cadastro e edicao |

Ao cadastrar: **formulario -> controller -> service -> repository/JPA -> PostgreSQL**. Os arquivos exatos dependem das opcoes selecionadas no Bootify.

Nao extraia um projeto por cima de outro com entidades diferentes: os fontes antigos podem permanecer e entrar na compilacao. Limpe antes de substituir o download.

## 4. preparar-bootify.ps1: adaptar nomes ao banco

O iniciador chama este script automaticamente. Para executar apenas a adaptacao:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\preparar-bootify.ps1
```

Ele procura exatamente um Model.java e um Tenant.java em src/main/java. Le os arquivos e usa expressoes regulares para localizar e ajustar anotacoes JPA:

- `@Table`: define models e tenants, preservando as restricoes unicas.
- `@SequenceGenerator`: define models_id_seq e tenants_id_seq, com allocationSize 1, removendo a configuracao inicial da sequence do download.
- `@GeneratedValue`: aponta o ID para o gerador correspondente.

Uma sequence e um objeto do PostgreSQL que fornece numeros para novos IDs. O script usa as existentes; nao cria outras nem reinicia a numeracao.

Ele valida os dois arquivos antes de escrever e salva o primeiro original ao lado do Java com extensao `.databasefirst.bak`. Repetir o script com os mesmos mapeamentos nao muda novamente o conteudo. Nao renomeia classes, controllers ou telas e nao altera o banco.

> “Bootify gera o aplicativo; o preparador adapta as anotacoes aos nomes do nosso esquema existente.”

## 5. iniciar-bootify.ps1: compilar e executar

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-bootify.ps1
```

O script le `.env`, localiza `mvnw.cmd`, detecta o Java em JAVA_HOME ou no PATH e chama o preparador. Configura no ambiente do processo a URL e as credenciais do banco, sem escreve-las nos fontes.

Define `SPRING_FLYWAY_ENABLED=false` porque o banco ja foi preparado e `SPRING_JPA_HIBERNATE_DDL_AUTO=validate` para Hibernate conferir a correspondencia entre entidades e esquema existente.

O comando interno de compilacao e:

```powershell
& $wrapper "-Djava.version=$javaVersion" clean package
```

`$wrapper` aponta para mvnw.cmd, que usa a versao Maven definida pelo download. `-Djava.version` informa a versao Java detectada. `clean` limpa o target **do aplicativo Bootify** e `package` executa o build e produz o JAR. Com as opcoes do roteiro, o plugin frontend do POM tambem prepara Node/npm e compila a interface com Vite. Os downloads explicam a demora maior na primeira execucao.

Se a compilacao falhar, o script nao inicia o site. Se passar, executa:

```powershell
& $java -jar $jars[0].FullName --spring.profiles.active=production "--server.port=$Porta" --server.address=127.0.0.1
```

`-jar` inicia o aplicativo empacotado. O perfil production usa a configuracao para os recursos compilados; a porta padrao e 8083, acessivel nesta maquina. Aguarde `Started` e abra http://localhost:8083/models.

O processo fica no terminal ate ser encerrado com Ctrl+C. O bloco finally restaura a pasta e as variaveis de ambiente anteriores. O iniciador compila codigo ja baixado; nao chama Bootify para gerar telas novamente.

## 6. limpar-gerados.ps1: preparar outra demonstracao

Primeiro pressione **Ctrl+C no terminal do site** e aguarde o prompt voltar. Dentro de DatabaseFirst:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\limpar-gerados.ps1
```

O script calcula caminhos a partir de `$PSScriptRoot`, verifica que sao target e bootify diretamente dentro de DatabaseFirst e rejeita links/junctions. Usa `Remove-Item -LiteralPath ... -Recurse -Force` para apagar essas pastas, incluindo alteracoes locais no download.

Preserva scripts, roteiros, `.env` e banco. Para apenas visualizar os destinos, acrescente `-WhatIf`. Pode repetir quando uma pasta ja nao existe.

Se o JAR estiver aberto, o Windows impede a remocao e a limpeza pode ficar parcial. Encerre o site e repita; Force nao desbloqueia arquivos em uso. Para outra apresentacao, repita a geracao jOOQ/SQL e o download Bootify conforme o roteiro.

## 7. demo.ps1: coordenar as ferramentas

| Acao | Comando/arquivo interno | Efeito |
| --- | --- | --- |
| prepare | compose up, flyway:migrate, seed.sql | Prepara banco e loja |
| clean | mvn clean | Remove target do gerador |
| generate | mvn -Pcodegen generate-sources | Gera Java jOOQ |
| export-bootify | pg_dump --schema-only | Exporta estrutura SQL |
| inspect | psql com inspect.sql | Consulta migrations, colunas e dados da demonstracao |
| add-column | psql com add-column.sql | Adiciona demo_note em models |
| restore | psql com restore.sql e geracao jOOQ | Remove demo_note, apaga modelos/auditoria da loja databasefirst e regenera Java |
| stop | docker compose down | Remove containers/rede, preservando volumes |

Para inspecionar ou parar:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\demo.ps1 inspect
powershell -NoProfile -ExecutionPolicy Bypass -File .\demo.ps1 stop
```

A funcao Docker inclui os caminhos do .env e Compose. Maven usa Docker para executar o gerador no container temporario. Sql envia o arquivo SQL pela entrada padrao ao psql, com parada em erro e transacao unica.

**restore altera dados do banco**, nao e a limpeza dos arquivos. **stop nao encerra o site Java**, que roda no Windows; use Ctrl+C nele antes.

## 8. scripts/test_regeneration.ps1: provar a regeneracao

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\gerar-codigo.ps1 -DemonstrarRegeneracao
```

Esse teste usa restore no inicio e no final: **apaga modelos e auditoria da loja databasefirst**. Seu funcionamento:

1. Restaura o esquema de referencia e gera o Java inicial.
2. Salva copia e hash SHA-256 de ModelsRecord.java.
3. Adiciona demo_note TEXT no banco.
4. Confirma que o Java ainda nao mudou: alterar o banco sozinho nao reexecuta o gerador.
5. Executa generate novamente.
6. Confere getDemoNote, setDemoNote e DEMO_NOTE nos fontes.
7. Salva evidencias em target/evidence.
8. No finally, restaura o esquema e regenera; depois compara o hash com o inicial e verifica que a coluna foi removida.

> “Alterei o banco e mandei gerar novamente. O novo campo apareceu no Java, mostrando que a estrutura do banco e a origem do codigo.”

## Como ler os comandos PowerShell

| Trecho | Significado |
| --- | --- |
| powershell -NoProfile | Abre sem personalizacoes do perfil |
| -ExecutionPolicy Bypass | Permite scripts nessa execucao, sem alterar permanentemente a politica da maquina |
| -File .\arquivo.ps1 | Executa o arquivo relativo a pasta atual |
| $PSScriptRoot | Pasta do proprio script, usada para localizar arquivos internos |
| & $demo generate | Executa o caminho em $demo passando generate como argumento |
| $LASTEXITCODE | Codigo do ultimo programa externo; zero indica sucesso |
| $ErrorActionPreference = 'Stop' | Faz erros PowerShell interromperem o fluxo |
| try / finally | Executa uma operacao e uma etapa de encerramento/restauracao ao sair |

## Perguntas provaveis do professor

**Qual linha gera as classes?** `& $demo generate`, em gerar-codigo.ps1. Ela chega a mvn -Pcodegen generate-sources e ao objetivo generate do plugin jOOQ.

**Quem cria o banco e quem gera Java?** PostgreSQL executa as migrations aplicadas por Flyway. jOOQ consulta o banco para gerar Java.

**Quem gera CRUD e telas?** Bootify, apos importar o SQL e selecionar as opcoes. jOOQ gera DAOs para acesso a dados, mas nao as telas web.

**O que voces escreveram?** Os scripts de coordenacao, adaptacao e demonstracao. Os geradores produzem as classes jOOQ e o aplicativo Bootify; os scripts nao escrevem manualmente cada controller ou template.

**Onde esta a configuracao?** No perfil codegen de DatabaseFirst/pom.xml para jOOQ; nas opcoes do site para Bootify. O pom.xml dentro de bootify/ei-front configura o build desse outro projeto.

**Por que desligar Flyway no aplicativo?** O banco ja foi preparado pelo Flyway dos scripts. O aplicativo valida o esquema existente.

**Se apagar, pode gerar de novo?** Sim: preservamos o banco e repetimos a geracao. Reextrair um ZIP antigo recupera uma geracao anterior; nao demonstra uma nova geracao no site.

**Mudancas no banco aparecem sozinhas no Java?** Nao. Precisa executar o gerador novamente; o teste de demo_note demonstra isso.

**O que e tenant?** Uma loja/empresa. tenant_id associa cada modelo a ela. Esse campo sozinho nao implementa isolamento de acesso entre lojas.

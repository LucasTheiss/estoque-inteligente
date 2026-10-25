# Roteiro de gravação: empacotamento de ponta a ponta

Duração sugerida: 8 a 10 minutos, descontando esperas de build. Público: professor
de Reuso de Software. O foco é demonstrar a transformação do código em artefatos
distribuíveis e o consumo real de um componente. Este roteiro não é um vídeo gravado.

## Antes de gravar

Abra Docker Desktop e aguarde os serviços ficarem disponíveis. Tenha um JDK 21+
instalado. Deixe abertas as abas: `pom.xml`, `Dockerfile`, `compose.yaml`,
`scripts/empacotar-reuso.ps1`, `examples/reuso/ConsumidorEstoque.java` e este roteiro.
Não exponha arquivos de credenciais, hashes de usuários ou configurações pessoais.
Use as contas públicas de demonstração se precisar autenticar na interface.

No PowerShell, prepare a sessão:

```powershell
cd "C:\Users\Lucas Theiss\Documents\Projetos\faculdade"
$raizProjeto = (Get-Location).Path
# Caminho do JDK utilizado e verificado neste computador; ajuste em outra máquina.
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-23'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$versao = ([xml](Get-Content pom.xml -Raw)).project.version
java -version
javac -version
docker compose version
git remote -v
```

Java 23 foi usado para a gravação preparada; `--release 21` produz classes para
Java 21+. A aplicação Docker é construída e executada com Java 21.

## Cena 1 — intenção e repositório (40 segundos)

**Mostre:** repositório no GitHub e árvore do projeto.

**Fala:**

> Empacotamento transforma código-fonte em um artefato que outra pessoa consegue
> executar ou utilizar. Nosso projeto tem duas entregas: um JAR executável com o
> sistema web completo e um JAR de biblioteca com regras de estoque reutilizáveis.
> O objetivo não é apenas diminuir o tamanho dos arquivos: é distribuir uma
> implementação com versão, contrato e documentação.

Apresente rapidamente `src/main/java` (código), `src/main/resources` (templates,
configuração, assets e migrações), `src/test` (testes) e `pom.xml` (build).
Não percorra todos os controllers: eles não são o assunto deste vídeo.

## Cena 2 — Maven constrói a aplicação (1 minuto)

**Mostre:** `pom.xml`.

**Fala:**

> O POM identifica o projeto por groupId, artifactId e version. Declara Java 21
> e dependências como Spring MVC, Thymeleaf, JDBC, PostgreSQL, Flyway e PDFBox.
> Maven coordena a compilação, testes e empacotamento. O plugin do Spring Boot
> prepara o JAR executável com o código, os recursos e as dependências.

**Mostre:** `Dockerfile`, destacando:

```dockerfile
RUN mvn -B dependency:go-offline
RUN mvn -B package
COPY --from=build /build/target/estoque-inteligente-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Fala:**

> A primeira etapa usa uma imagem com Maven e JDK para construir. A segunda usa
> uma imagem com runtime Java e recebe o JAR. Maven não precisa ficar executando
> no ambiente final. O comando que inicia o sistema é java -jar app.jar.

Não afirme que `package` certifica todas as integrações: os testes que precisam
de banco dependem das variáveis de teste. O serviço `verify` fornece essas variáveis.

## Cena 3 — construir e executar de ponta a ponta (1 a 2 minutos)

Execute na raiz:

```powershell
docker compose --env-file .env.demo up -d --build --wait
docker compose --env-file .env.demo ps
```

Mostre o fim do build e os containers saudáveis. Pode acelerar a espera na edição.
Se aparecer cache, explique que Docker reutilizou etapas cujos arquivos não mudaram;
isso não significa que a construção deixou de existir.

Abra `http://localhost:8080/login` no navegador.

**Fala:**

> Agora estamos acessando a aplicação empacotada. NGINX encaminha a requisição
> ao Spring Boot. PostgreSQL e MQTT são serviços separados; não estão embutidos
> no JAR. O volume preserva o banco. Na inicialização, Flyway aplica as migrações
> pendentes e o provisionamento prepara os dados demonstrativos quando habilitado.

Opcionalmente, entre no Mercado Raiz como `admin`, senha inicial
`Loja-demo-local-2026!`, e abra o Histórico. Não é necessário alterar estoque.

Para mostrar fisicamente o JAR que está em execução:

```powershell
New-Item -ItemType Directory -Force target/video | Out-Null
docker compose --env-file .env.demo cp app:/app/app.jar target/video/aplicacao.jar
jar tf target/video/aplicacao.jar | Select-String 'BOOT-INF/classes/br/com/estoque/EstoqueInteligenteApplication.class|BOOT-INF/classes/templates/login.html|BOOT-INF/lib/spring-webmvc'
```

**Fala:**

> Essas entradas mostram a classe da aplicação, o template e uma dependência
> dentro do pacote executável. O JAR está dentro da imagem Docker; são artefatos diferentes.

## Cena 4 — selecionar o componente reutilizável (1 minuto)

**Mostre:** `scripts/empacotar-reuso.ps1`, especialmente `$sources`.

**Fala:**

> Para demonstrar reuso por outro programa, selecionamos nove tipos já usados
> pelo sistema: o contrato Quantidade e suas três políticas; o contrato
> CalculoEstoque; a base OperacaoEstoque; e entrada, saída e ajuste. Não copiamos
> a implementação para uma segunda versão: compilamos as próprias fontes existentes.

Mostre `Quantidade.validar` e `CalculoEstoque.executar`: são os contratos de uso.
Explique que valores são BigDecimal e entradas inválidas geram IllegalArgumentException.

**Fala:**

> Esse componente só calcula e valida. Ele não autentica usuários nem grava no
> banco. A aplicação continua responsável por transações, permissões e auditoria.
> Essa separação permite utilizar as regras em outro programa sem iniciar Spring.

## Cena 5 — gerar o JAR de reuso (1 minuto)

Execute:

```powershell
powershell -NoProfile -File scripts/empacotar-reuso.ps1
$jarReuso = Join-Path $raizProjeto "target/reuso/estoque-regras-$versao.jar"
Get-Item $jarReuso | Select-Object Name, Length, LastWriteTime
jar tf $jarReuso
```

Mostre as nove classes, `META-INF/MANIFEST.MF` e `README.md`.

**Fala:**

> Este segundo pacote é gerado diretamente pelas ferramentas do JDK. O script
> usa javac para compilar e jar para empacotar. Ele lê a versão do POM, mas não
> chama Maven. Também inclui o contrato documentado e verifica o consumidor.
> O componente não possui dependências externas, somente Java 21 ou superior.

## Cena 6 — provar reuso fora do projeto (1 a 2 minutos)

**Mostre:** `examples/reuso/ConsumidorEstoque.java`.

Explique as importações e a sequência 0 -> entrada 10 -> saída 3 -> ajuste 8.
O código do consumidor não implementa essas fórmulas; importa os tipos da biblioteca.
As asserções também verificam precisão, saldo zero e rejeição de entradas inválidas.

Execute no mesmo PowerShell:

```powershell
$destino = Join-Path $env:TEMP ('consumidor-estoque-' + [guid]::NewGuid().ToString())
New-Item -ItemType Directory $destino | Out-Null
Copy-Item -LiteralPath $jarReuso -Destination $destino
Copy-Item -LiteralPath "$raizProjeto/examples/reuso/ConsumidorEstoque.java" -Destination $destino
Set-Location $destino
Get-ChildItem
javac --release 21 -cp "estoque-regras-$versao.jar" ConsumidorEstoque.java
java -ea -cp "estoque-regras-$versao.jar;." ConsumidorEstoque
```

Resultado esperado:

```text
Reuso verificado: 0 -> 10 -> 7 -> 8; precisao e rejeicoes preservadas.
```

**Fala:**

> Esta pasta está fora do repositório e recebeu apenas o JAR e o código do
> consumidor. O parâmetro -cp coloca a biblioteca no classpath, onde Java procura
> as classes. O ponto inclui o consumidor na pasta atual. -ea habilita as
> asserções. O sucesso mostra que usamos a implementação distribuída, sem copiar
> os fontes das regras e sem depender do servidor web ou do banco.

Se quiser abrir o manifesto e a documentação dentro do pacote:

```powershell
jar xf "estoque-regras-$versao.jar" META-INF/MANIFEST.MF README.md
Get-Content META-INF/MANIFEST.MF
Get-Content README.md -Encoding UTF8 -TotalCount 12
Set-Location $raizProjeto
```

## Cena 7 — atualização e testes (40 segundos)

**Mostre:** a versão no POM e os comandos no roteiro; não precisa alterar código
de negócio apenas para criar uma encenação de atualização.

**Fala:**

> O pacote é um retrato do código no momento da construção. Se alterarmos uma
> regra incluída, executamos o script novamente. Para uma nova entrega identificável,
> atualizamos a versão no POM. Quem consome precisa receber o novo JAR e testar a
> compatibilidade. Adicionar um novo tipo ao componente exige incluí-lo em sources.
> Para atualizar a aplicação Docker, reconstruímos com up --build. A biblioteca
> e o sistema completo têm comandos de construção separados.

Para a suíte integrada, na raiz:

```powershell
docker compose --env-file .env.demo --profile check run --rm verify
```

Na validação desta alteração: 75 testes Java passaram com PostgreSQL e MQTT,
zero falhas, erros ou ignorados. A contagem pode mudar com novas alterações.
Mostre o resultado da sua execução se afirmar no vídeo que acabou de testar.

## Encerramento (20 segundos)

> Demonstramos o ciclo completo: código versionado, construção, artefato,
> execução do sistema e consumo independente de uma biblioteca. Maven e Spring
> Boot empacotam a aplicação; as ferramentas do JDK empacotam o componente de
> regras. O reuso aparece quando outro programa utiliza esse componente sem
> reimplementar a lógica.

## Conferência antes de enviar

- O vídeo mostra código, comando de geração, arquivo gerado e consumidor funcionando.
- Diferencia JAR executável, biblioteca JAR, imagem Docker e container em execução.
- Não atribui ao Maven a execução do script que usa javac/jar diretamente.
- Não afirma que o JAR da biblioteca atualiza banco ou outros consumidores sozinho.
- Não usa `docker compose down -v`: esse comando remove o volume do banco.
- Publicação não listada e envio do link ficam a cargo da equipe; não são executados por este roteiro.

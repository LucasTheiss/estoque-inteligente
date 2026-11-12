# Abrir o CRUD Bootify no banco existente

Comece com o PowerShell dentro de `estoque-inteligente\DatabaseFirst`. Confira com `Get-Location`. Não repita `DatabaseFirst/` no argumento `-File`.

O banco é `estoque_databasefirst`, preparado pelo Docker/Flyway. O jOOQ lê esse banco e gera Java. O nome do projeto pode continuar `ei-front`: ele é independente do nome do banco.

## 1. Preparar e gerar

Dentro de `DatabaseFirst`:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-banco.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\gerar-codigo.ps1
```

Para gerar pelo site, importe `target\bootify-schema.sql`, escolha PostgreSQL e Thymeleaf, habilite CRUD/frontend e selecione **None (validate)** em DB Generation. Extraia o download em `bootify\ei-front`. Confira os ajustes abaixo a cada nova geração.

## 2. Corrigir os nomes das tabelas

Os caminhos desta seção são relativos a `DatabaseFirst\bootify\ei-front`.

Em `src/main/java/estoque_inteligente/ei_front/model/Model.java`, acrescente o nome à anotação existente:

```java
@Entity
@Table(
        name = "models",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "name", "brand", "category"})
)
public class Model {
```

Em `src/main/java/estoque_inteligente/ei_front/tenant/Tenant.java`, adicione o import:

```java
import jakarta.persistence.Table;
```

E coloque a anotação acima da classe:

```java
@Entity
@Table(name = "tenants")
public class Tenant {
```

## 3. Corrigir as sequências dos IDs

O banco já tem `models_id_seq` e `tenants_id_seq`. Não use a `primary_sequence` do download.

Em `Model.java`, substitua os blocos `@SequenceGenerator` e `@GeneratedValue` por estes. Preserve `@Id`, `@Column` e `private Long id`:

```java
@SequenceGenerator(
        name = "models_id_generator",
        sequenceName = "models_id_seq",
        allocationSize = 1
)
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "models_id_generator")
```

Em `Tenant.java`, substitua os mesmos blocos por:

```java
@SequenceGenerator(
        name = "tenants_id_generator",
        sequenceName = "tenants_id_seq",
        allocationSize = 1
)
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tenants_id_generator")
```

## 4. Usar o esquema já preparado

Em `src/main/resources/application.yml`, acrescente `flyway` ao bloco `spring` já existente:

```yaml
spring:
  flyway:
    enabled: false
```

Não substitua o arquivo inteiro nem duplique o bloco `spring`. Preserve o restante e mantenha `spring.jpa.hibernate.ddl-auto: validate`. A migration Bootify `V001__CREATE_INITIAL.sql` não deve executar nesse banco: as tabelas e o histórico Flyway já foram criados pelos scripts originais.

## 5. Conferir a versão Java

O download pede Java 25 no `pom.xml`; o Java encontrado nesta máquina foi 19. Para compilar com o JDK 19, altere `<java.version>25</java.version>` para `<java.version>19</java.version>`. Se instalar e selecionar JDK 25, mantenha 25. Confira o JDK usado com `java -version` e, dentro de `ei-front`, `.\mvnw.cmd -v`.

Estes são ajustes de configuração do projeto gerado. A compilação e execução do Bootify ainda precisam ser validadas; o teste do gerador jOOQ não comprova que o site Bootify iniciou.

## 6. Conectar, compilar e iniciar

Comece este bloco dentro de `DatabaseFirst`. Ele lê as credenciais locais sem exibir a senha, configura o banco existente e entra no projeto:

```powershell
if (-not (Test-Path .\.env)) { throw 'Execute iniciar-banco.ps1 dentro de DatabaseFirst primeiro.' }
$config = @{}
Get-Content .\.env | ForEach-Object {
    if ($_ -match '^([^#=]+)=(.*)$') { $config[$matches[1].Trim()] = $matches[2] }
}
$env:JDBC_DATABASE_URL = 'jdbc:postgresql://localhost:5544/estoque_databasefirst'
$env:JDBC_DATABASE_USERNAME = $config['DF_DB_USER']
$env:JDBC_DATABASE_PASSWORD = $config['DF_DB_PASSWORD']
$env:SPRING_FLYWAY_ENABLED = 'false'
Set-Location .\bootify\ei-front
```

Agora você está em `ei-front`. No mesmo terminal, compile:

```powershell
.\mvnw.cmd clean package
if ($LASTEXITCODE -ne 0) { throw 'A compilacao falhou. Corrija o erro antes de iniciar o site.' }
```

O POM inclui o plugin que baixa Node e compila a interface durante o build. Para executar o JAR pronto, não precisa iniciar Vite separadamente. A primeira compilação exige internet.

Depois de aparecer `BUILD SUCCESS`, execute:

```powershell
java -jar .\target\ei-front-0.0.1-SNAPSHOT.jar --spring.profiles.active=production --server.port=8083
```

Abra `http://localhost:8083/models`. Cadastre um modelo selecionando a loja `Loja exclusiva Database First`, criada pelo script. Depois edite e exclua o modelo cadastrado. A tela de lojas fica em `http://localhost:8083/tenants`.

Para comprovar a gravação, abra outro terminal dentro de `DatabaseFirst`:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\demo.ps1 inspect
```

Esse comando mostra os modelos da loja `databasefirst`. O CRUD passa a usar as tabelas `models` e `tenants` do mesmo banco consultado pelo jOOQ. O CRUD gerado permite acessar outras lojas também; use o banco isolado da atividade.

## 7. Parar

No terminal do site, pressione `Ctrl+C`. Estando em `ei-front`, volte à pasta `DatabaseFirst`:

```powershell
Set-Location ..\..
powershell -NoProfile -ExecutionPolicy Bypass -File .\demo.ps1 stop
```

O volume é preservado. Se fechar o terminal, repita o bloco de conexão antes de abrir o JAR. Não crie o banco `ei_front` neste fluxo: a URL usa `estoque_databasefirst`.

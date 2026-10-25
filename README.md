# Estoque Inteligente

Sistema de gestão de estoque multi-loja com Java 21, Spring Boot MVC,
Thymeleaf, PostgreSQL e integração MQTT. Oferece cadastros de produtos,
modelos, posições, vínculos, usuários e lojas, além de movimentações,
histórico e exportação de relatórios.

Os recursos IoT, relatórios avançados e quantidades fracionadas são
configuráveis por loja. O acesso às operações depende das permissões do perfil.

## Executar com Docker

Pré-requisitos: Docker Desktop iniciado, com containers Linux, e internet
para baixar imagens e dependências na primeira construção.

Na raiz do projeto:

```powershell
docker compose --env-file .env.demo up -d --build --wait
```

As entregas individuais de APF e suas fórmulas estão em [docs/entregas-individuais](docs/entregas-individuais/README.md). A aplicação inclui consulta de ofertas externas e comparação de preços; o Compose inicia os provedores demonstrativos de fornecedores e ofertas.

Abra http://localhost:8080/login. O comando compila o JAR, inicia PostgreSQL,
MQTT e NGINX e aplica as migrações do banco automaticamente. Java e Maven
locais não são necessários para essa execução.

### Contas de demonstração local

| Loja | Usuários | Senha |
| --- | --- | --- |
| essencial, calcados ou granel | admin, gerente, estoquista ou vendedor | Loja-demo-local-2026! |
| Administração global | superadmin | Global-demo-local-2026! |

A configuração `.env.demo` cria os dados iniciais e habilita a simulação IoT.
Reiniciar preserva os dados existentes; a simulação não exige dispositivos físicos.

Consultar estado e logs:

```powershell
docker compose --env-file .env.demo ps
docker compose --env-file .env.demo logs --tail=120 app db mqtt
```

Parar os serviços, preservando os dados:

```powershell
docker compose --env-file .env.demo stop
```

Para uma instalação própria, copie `.env.example` para `.env`, configure
as credenciais e execute `docker compose up -d --build --wait`. Em HTTP local,
use `COOKIE_SECURE=false`; em HTTPS, use `true`. O superadmin cria as lojas
pelo painel global. As credenciais acima são exclusivas da demonstração local.

## Desenvolvimento local

Pré-requisitos: JDK 21 ou superior, Maven 3.9 ou superior e Docker Desktop.

```powershell
docker compose --env-file .env.demo up -d --wait db mqtt
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Abra http://localhost:8081/login. O perfil `local` lê `.env.demo` e usa o banco
em `127.0.0.1:5433` e o MQTT em `127.0.0.1:1883`.

Os scripts `scripts/demo.ps1`, `scripts/demo.sh` e `scripts/dev.ps1`
facilitam a execução.

## Testes

Com JDK e Maven locais:

```powershell
mvn test
```

Para executar os testes Java com PostgreSQL e MQTT do Compose:

```powershell
docker compose --env-file .env.demo --profile check run --rm verify
```

Os testes integrados dependem das variáveis de conexão fornecidas por esse
serviço. Sem a configuração correspondente, são ignorados na execução local.
O script `scripts/test.ps1` executa a verificação pelo Docker.

Para os testes de JavaScript, com Node.js 20 ou superior:

```powershell
node --test src/test/js/*.test.mjs
```

## Empacotar a aplicação

Com JDK 21 ou superior e Maven:

```powershell
mvn package
```

O resultado é `target/estoque-inteligente-0.0.1-SNAPSHOT.jar`.
O Dockerfile executa esse empacotamento durante a construção da imagem.

Para iniciar o JAR localmente, com banco e MQTT iniciados pelo comando da
seção de desenvolvimento e executando a partir da raiz do repositório:

```powershell
java -jar target/estoque-inteligente-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

Acesse http://localhost:8081/login. O JAR inclui a aplicação e suas dependências
Java; PostgreSQL e MQTT continuam sendo serviços externos.

## Biblioteca de regras reutilizáveis

Com JDK 21 ou superior no PATH ou em JAVA_HOME:

```powershell
powershell -NoProfile -File scripts/empacotar-reuso.ps1
```

O script gera `target/reuso/estoque-regras-0.0.1-SNAPSHOT.jar` e verifica seu
consumo por `examples/reuso/ConsumidorEstoque.java`.
Veja o [contrato e as instruções da biblioteca](docs/reuso/README.md).

## Estrutura

A [entrega de Análise de Pontos de Função](docs/apf/README.md) contém o inventário
de ALIs/AIE, EEs, SEs e CEs, memória de cálculo, NI e estimativas para três integrantes.
A entrega de APF reutiliza as funcionalidades existentes e acrescenta apenas a consulta `/fornecedores` (AIE) e dois relatórios em `/relatorios`: valor do estoque e movimentações no período.
O Compose também inicia um [provedor externo demonstrativo de fornecedores](external/fornecedores/README.md).

- `src/main/java/br/com/estoque/`: modelos, controllers, serviços, repositórios,
  estratégias e integração MQTT.
- `src/main/resources/templates/`: páginas Thymeleaf.
- `src/main/resources/static/assets/`: CSS e JavaScript.
- `src/main/resources/db/migration/`: migrações Flyway.
- `src/test/`: testes Java e JavaScript.
- `deploy/`: configuração de NGINX e Mosquitto.
- `examples/reuso/`: consumidor independente da biblioteca de regras.

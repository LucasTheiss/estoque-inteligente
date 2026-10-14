# Execução e operação

Para a apresentação acadêmica, consulte [demonstração MVP](DEMONSTRACAO_MVP.md): credenciais públicas, dados iniciais e roteiro sem cadastro preparatório.

## Docker

1. Abra o Docker Desktop.
2. Para demonstração, execute na raiz do projeto:

   ```powershell
   docker compose --env-file .env.demo up -d --build --wait
   docker compose --env-file .env.demo ps
   ```

3. Abra `http://localhost:8080/login`. Selecione `calcados`, `granel` ou
   `essencial`. Cada loja possui `vendedor`, `estoquista`, `gerente` e
   `admin`, com a senha `Loja-demo-local-2026!`.
4. Para administração global, selecione `Administração global` e entre com
   `superadmin` e a senha `Global-demo-local-2026!`.
5. Para consultar falhas de inicialização:

   ```powershell
   docker compose --env-file .env.demo logs --tail=120 app db mqtt
   ```

O script `scripts/demo.ps1` executa a inicialização acima. Flyway aplica as
migrações automaticamente. A demonstração provisiona três lojas, produtos,
posições, vínculos, saldos e perfis; reiniciar não recria lojas existentes.
O simulador Java publica telemetria identificada como simulada. Não comprova
acionamento físico de LEDs ou calibração de balanças.

Para instalação própria, copie `.env.example` para `.env`, configure senhas
próprias e execute `docker compose up -d --build --wait`. Em HTTP local,
configure `COOKIE_SECURE=false`; em HTTPS, mantenha `true`.
Entre na administração global com a senha configurada em `SUPERADMIN_PASSWORD`
e crie as lojas. As contas de demonstração são exclusivas do ambiente local.

NGINX publica HTTP na porta 8080; PostgreSQL publica a porta 5433 somente em
127.0.0.1. MQTT exige senha; para dispositivos LAN, configure MQTT_BIND com o IP
da interface local e restrinja a porta 1883 no firewall. Não exponha MQTT sem TLS
na internet. Credenciais do aplicativo ficam no ambiente, fora da interface.

## Desenvolvimento local

Java 21+, Maven 3.9+ e Docker Desktop. Na raiz do projeto, execute:

```powershell
docker compose --env-file .env.demo up -d --wait db mqtt
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

Abra `http://localhost:8081/login`. As contas são as mesmas da demonstração.
O script `scripts/dev.ps1` executa esses comandos. O perfil `local` lê
`.env.demo`, conecta ao PostgreSQL em 127.0.0.1:5433 e ao MQTT local em 1883.
A tradução do nome MQTT do Compose ocorre na conexão; não altera os registros
das lojas. A porta 8081 evita conflito com o NGINX de uma execução Docker.

Templates ficam em `src/main/resources/templates`; CSS e JavaScript ficam em
`src/main/resources/static/assets`. O controlador retorna o nome da view sem
extensão. `return "login"` resolve `templates/login.html` no classpath.

Volumes existentes preservam a senha com que o banco foi criado. Alterar
POSTGRES_PASSWORD em um arquivo não muda a senha de um banco já inicializado.
Use a credencial correspondente ao volume; não exclua volumes para resolver
falhas de autenticação. O diretório `target/pgdata`, quando existente em uma
sessão de desenvolvimento, contém um banco local: não execute limpeza geral
de `target` enquanto precisar preservar esse banco.

## Testes

`mvn test` executa os testes unitários e de renderização Thymeleaf. Para
integração, configure TEST_DATABASE_URL com uma URL JDBC PostgreSQL de testes,
DATABASE_USER e DATABASE_PASSWORD. Cada execução cria um schema temporário
exclusivo e o remove ao terminar; não use credenciais de produção. Sem essa
variável os testes integrados são ignorados, não aprovados. TEST_MQTT_URI habilita
o teste MQTT e exige as credenciais MQTT correspondentes.

Com a infraestrutura Docker:

```powershell
docker compose --env-file .env.demo --profile check run --rm verify
```

Os testes de telas isolam os serviços e verificam templates reais, formulários,
CSRF e respostas HTML/JSON. Os testes integrados exercitam demonstração,
autenticação, segregação por loja, RBAC, estoque inteiro/fracionado,
idempotência, estorno, concorrência e MQTT. O fluxo HTTP real usa servidor em
porta aleatória, cookies e CSRF para login, cadastro, vínculo, movimentação,
exportação e logout; sua execução também exige TEST_DATABASE_URL.
Os resultados da refatoração estão
registrados em `docs/VALIDACAO.md`; a existência de um teste não prova sua execução.

A busca assistida permite ouvir cada localização e interromper a reprodução.
Sem síntese de voz, o navegador mantém os resultados em texto. Para os testes
isolados dessa interação, use Node 20+ e execute
`node --test src/test/js/speech.test.mjs`. Node não participa do runtime Java.

## Produção

Use HTTPS e COOKIE_SECURE=true. O perfil opcional `tunnel` executa Cloudflare
Tunnel com TUNNEL_TOKEN fornecido pelo operador; configure hostname para
http://nginx:80. Não publique sem configurar contas e domínio próprios.

Faça backup PostgreSQL com pg_dump e teste restauração regularmente.
Monitore /health, logs e disponibilidade no calendário comercial acordado.
Reinício automático não substitui medição de disponibilidade.
`docker stats --no-stream` mede consumo; execute carga concorrente antes de
homologar teto de memória e prazo de sinalização física.

## Saldo inicial de uma grade

Em Cadastros → Produtos, abra Cadastrar uma grade de tamanhos e selecione
Uma variação por tamanho. Informe os tamanhos separados por vírgula. Para
iniciar com estoque, selecione uma posição da mesma unidade e informe uma linha
por tamanho, por exemplo `36=5`, `37=0` e `38=3`. Todos os tamanhos devem
aparecer uma única vez. Quantidades em kg aceitam ponto ou vírgula decimal.

O cadastro cria as variantes e os vínculos; quantidades positivas geram entradas
auditadas. Quantidades zero criam somente o vínculo. A operação exige as
permissões de cadastro e, para saldo positivo, entrada. Falhas devem reverter
todo o cadastro na transação; o teste PostgreSQL desse contrato ainda precisa
ser executado. Deixe posição e quantidades vazias para cadastrar sem estoque.

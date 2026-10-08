# Execução e operação

## Docker

1. Copie `.env.example` para `.env` (não versionado). Defina senhas aleatórias e
   SECRET_KEY com pelo menos 32 caracteres aleatórios. Para evitar ambiguidades
   na URL PostgreSQL, use senhas hexadecimais geradas com `secrets.token_hex(32)`.
2. Execute `docker compose up -d --build`.
3. Execute `docker compose exec app flask --app estoque init-db`.
4. Crie a loja: `docker compose exec app flask --app estoque create-tenant --slug loja-1 --name "Loja Centro" --iot --enterprise --fractional`.
   O comando solicita a senha do administrador sem exibi-la. Remova as flags dos
   recursos não contratados. Cada loja tem usuários, permissões e dados próprios.
5. Abra `http://localhost:8080`, selecione a loja e entre com o usuário `admin`.

Não há senha padrão, usuários fictícios nem telemetria simulada na aplicação.
Cadastre posições, produtos e vínculos; registre uma entrada para disponibilizar
saldo. A grade cria variantes ligadas a um modelo e permite estoque inicial por tamanho.

NGINX é o único ponto HTTP publicado; PostgreSQL não expõe porta no host.
MQTT exige senha; para dispositivos LAN, configure MQTT_BIND com o IP da interface
local e restrinja a porta 1883 no firewall. Não exponha MQTT sem TLS na internet.
Credenciais do aplicativo ficam no ambiente, fora da interface.

## Desenvolvimento local

Python 3.12+, PostgreSQL 17+ e broker MQTT para IoT. Crie ambiente virtual e
instale requirements-dev.txt. Defina DATABASE_URL, SECRET_KEY e
COOKIE_SECURE=false apenas para HTTP local. Execute:

```text
flask --app estoque init-db
flask --app estoque create-tenant --slug loja-1 --name "Loja Centro" --iot --enterprise --fractional
waitress-serve --host=127.0.0.1 --port=8000 --threads=2 --call estoque:create_app
```

## Testes

`python -m pytest tests -q`. Para integração, configure TEST_DATABASE_URL para
PostgreSQL de testes. Cada teste cria um schema temporário exclusivo e o remove
ao terminar; não use credenciais de produção. Sem essa variável os testes
integrados são ignorados, não aprovados.

Testes cobrem autenticação, CSRF, segregação por loja, RBAC, variabilidade,
cadastros, estoque inteiro/fracionado, idempotência, estorno, concorrência,
rollback provocado e renderização das telas/exportações.

## Produção

Use HTTPS e COOKIE_SECURE=true. O perfil opcional `tunnel` executa Cloudflare
Tunnel com TUNNEL_TOKEN fornecido pelo operador; configure hostname para
http://nginx:80. Não publique sem configurar contas e domínio próprios.

Faça backup PostgreSQL com pg_dump e teste restauração regularmente.
Monitore /health, logs e disponibilidade no calendário comercial acordado.
Reinício automático não substitui medição de disponibilidade.
`docker stats --no-stream` mede consumo; execute carga concorrente antes de
homologar teto de memória e prazo de sinalização física.

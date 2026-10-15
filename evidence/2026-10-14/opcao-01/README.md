# Validação da Opção 01 — 14/10/2026

- `java-tests.log`: 68 testes Java aprovados, zero falhas, zero erros e zero ignorados.
  Execução Docker com PostgreSQL e MQTT reais; inclui os novos CRUDs, permissões,
  revogação, isolamento, transações, padrões e renderização Thymeleaf.
- `docker-build.log`: imagem compilada e serviços iniciados com sucesso pelo comando
  `docker compose --env-file .env.demo up -d --build --wait`. A fase de build ignora
  integrações sem variáveis de banco; a execução completa está em `java-tests.log`.
- `browser.json`: revisão pelo Edge headless através de `http://localhost:8080`,
  usando NGINX. CRUD completo de modelo descartável e criação/consulta/remoção de
  vínculo vazio. Registros descartáveis removidos ao final; auditoria preservada.
- Seis PNGs: Modelos, edição de Vínculos e edição de Lojas em 1440×1000 e 390×844.
  Inspeção visual realizada. Tabelas mantêm rolagem interna no celular; não houve
  transbordamento horizontal da página.

Também passaram os nove testes JavaScript existentes: navegação, tema e voz.

Conferência SQL após a inicialização: três lojas ativas (`essencial`, `calcados`,
`granel`), 13 usuários, 12 modelos, 12 produtos, 24 posições e 12 vínculos.
Flyway aplicou V1 e V2 com sucesso. PostgreSQL e aplicação estavam saudáveis;
MQTT e NGINX estavam em execução.

As credenciais usadas no navegador são as contas públicas de demonstração do README.
Hardware IoT físico não foi utilizado; a integração MQTT usa os dispositivos simulados.

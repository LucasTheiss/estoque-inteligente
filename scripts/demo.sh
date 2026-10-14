#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
docker compose --env-file .env.demo up -d --build --wait
printf '%s\n' 'Abra http://localhost:8080. Lojas: calcados, granel e essencial.' 'Usuários: vendedor, estoquista, gerente e admin. Senha: Loja-demo-local-2026!' 'Acesso global: superadmin / Global-demo-local-2026!'

$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    docker compose --env-file .env.demo --profile check run --rm verify
    if ($LASTEXITCODE -ne 0) { throw 'Os testes Java falharam.' }
} finally { Pop-Location }

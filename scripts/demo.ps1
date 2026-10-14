$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    if (-not (Test-Path -LiteralPath '.env.demo')) {
        throw 'Arquivo .env.demo ausente na raiz do projeto.'
    }
    docker compose --env-file .env.demo up -d --build --wait
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao iniciar os serviços de demonstração.' }
    Write-Host 'Abra http://localhost:8080. Lojas: calcados, granel e essencial.'
    Write-Host 'Usuários: vendedor, estoquista, gerente e admin. Senha: Loja-demo-local-2026!'
    Write-Host 'Acesso global: superadmin / Global-demo-local-2026!'
} finally { Pop-Location }

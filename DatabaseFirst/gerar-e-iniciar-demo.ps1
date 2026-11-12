param([switch]$SemTestes)
$ErrorActionPreference = 'Stop'
$demo = Join-Path $PSScriptRoot 'demo.ps1'

function Invoke-DemoAction([string]$Action, [string]$Description) {
    Write-Host "`n$Description"
    & $demo $Action
    if ($LASTEXITCODE -ne 0) {
        throw "A etapa '$Action' falhou (codigo $LASTEXITCODE)."
    }
}

Invoke-DemoAction 'generate' 'Etapa 1/4: gerando tabelas, Records, POJOs e DAOs jOOQ a partir do PostgreSQL.'
Invoke-DemoAction 'export-bootify' 'Etapa 2/4: exportando tenants/models para importar e gerar CRUD/tela no Bootify.'

if (-not $SemTestes) {
    Invoke-DemoAction 'test' 'Etapa 3/4: compilando e validando a demonstracao contra o PostgreSQL real.'
} else {
    Write-Host 'Etapa 3/4: testes ignorados por opcao.'
}

Write-Host "`nEtapa 4/4: empacotando e iniciando a aplicacao web. Pressione Ctrl+C para encerrar."
Invoke-DemoAction 'run' 'Aplicacao em http://127.0.0.1:8082/.'

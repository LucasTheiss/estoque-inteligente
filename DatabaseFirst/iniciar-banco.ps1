$ErrorActionPreference = 'Stop'
$demo = Join-Path $PSScriptRoot 'demo.ps1'

Write-Host 'Etapa 1/1: iniciando PostgreSQL, aplicando migrations e criando os dados da demonstracao.'
& $demo prepare
if ($LASTEXITCODE -ne 0) {
    throw "A preparacao do banco falhou (codigo $LASTEXITCODE)."
}

Write-Host 'Banco pronto. O endpoint local e 127.0.0.1:5544; a aplicacao usa a rede interna do Compose.'

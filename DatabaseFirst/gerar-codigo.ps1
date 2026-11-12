param([switch]$DemonstrarRegeneracao)
$ErrorActionPreference = 'Stop'
$demo = Join-Path $PSScriptRoot 'demo.ps1'

Write-Host 'Etapa 1/2: gerando classes e DAOs jOOQ a partir do PostgreSQL.'
& $demo clean
if (-not $?) { throw 'Nao foi possivel limpar os artefatos da demonstracao anterior.' }
& $demo generate
if (-not $?) { throw 'A geracao jOOQ falhou.' }

Write-Host "`nEtapa 2/2: exportando o esquema SQL para gerar CRUD e telas no Bootify."
& $demo export-bootify
if (-not $?) { throw 'A exportacao do esquema falhou.' }

if ($DemonstrarRegeneracao) {
    Write-Host "`nDemonstracao opcional: adicionar uma coluna, regenerar o Java e restaurar o banco."
    & (Join-Path $PSScriptRoot 'scripts/test_regeneration.ps1')
    if (-not $?) { throw 'A demonstracao de regeneracao falhou.' }
}

$generated = Join-Path $PSScriptRoot 'target/generated-sources/jooq'
$javaCount = (Get-ChildItem -Path $generated -Filter '*.java' -Recurse | Measure-Object).Count
Write-Host "`nConcluido: $javaCount classes jOOQ geradas. O SQL de importacao esta em target/bootify-schema.sql."

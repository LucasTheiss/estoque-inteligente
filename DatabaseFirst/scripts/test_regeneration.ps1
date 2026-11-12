$ErrorActionPreference = 'Stop'
$demoRoot = Split-Path $PSScriptRoot -Parent
$demo = Join-Path $demoRoot 'demo.ps1'
$record = Join-Path $demoRoot 'target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/records/ModelsRecord.java'
$table = Join-Path $demoRoot 'target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/Models.java'
$evidence = Join-Path $demoRoot 'target/evidence'
New-Item -ItemType Directory -Force $evidence | Out-Null
if (!(Test-Path $record)) { throw 'Execute iniciar-banco.ps1 e gerar-codigo.ps1 primeiro.' }

& $demo restore
if (-not $?) { throw 'Nao foi possivel restaurar o banco ao esquema inicial.' }
if (Select-String -Path $record -Pattern 'getDemoNote' -Quiet) { throw 'A coluna de demonstracao permaneceu apos restaurar o esquema.' }

$before = (Get-FileHash $record).Hash
Copy-Item $record (Join-Path $evidence 'ModelsRecord.before.java.txt') -Force
try {
    & $demo add-column
    if (-not $?) { throw 'Nao foi possivel adicionar demo_note ao banco isolado.' }
    if ((Get-FileHash $record).Hash -ne $before) { throw 'O arquivo Java mudou sem executar o gerador.' }

    & $demo generate
    if (-not $?) { throw 'A regeneracao jOOQ falhou.' }
    if (!(Select-String -Path $record -Pattern 'getDemoNote' -Quiet)) { throw 'Getter nao foi gerado.' }
    if (!(Select-String -Path $record -Pattern 'setDemoNote' -Quiet)) { throw 'Setter nao foi gerado.' }
    if (!(Select-String -Path $table -Pattern 'DEMO_NOTE' -Quiet)) { throw 'Coluna tipada nao foi gerada.' }

    Copy-Item $record (Join-Path $evidence 'ModelsRecord.after.java.txt') -Force
    $inspection = & $demo inspect
    if (-not $?) { throw 'Nao foi possivel capturar o esquema alterado.' }
    $inspection | Set-Content -Encoding UTF8 (Join-Path $evidence 'schema-after.txt')
} finally {
    & $demo restore
    if (-not $?) { throw 'A restauracao do esquema ou das classes falhou.' }
}

if (Select-String -Path $record -Pattern 'getDemoNote' -Quiet) { throw 'O campo permaneceu apos restaurar o banco.' }
$restored = (Get-FileHash $record).Hash
if ($restored -ne $before) { throw 'A classe restaurada difere do estado inicial.' }
$restoredSchema = & $demo inspect
if (-not $?) { throw 'Nao foi possivel verificar o esquema restaurado.' }
if ($restoredSchema -match 'demo_note') { throw 'A coluna demo_note permaneceu no banco.' }

@{ before_sha256=$before; restored_sha256=$restored; generated_getter=$true; generated_setter=$true; generated_column=$true; restored=$true } |
    ConvertTo-Json | Set-Content -Encoding UTF8 (Join-Path $evidence 'regeneration.json')
Write-Output 'Regeneracao validada: coluna refletida no Java e banco restaurado ao estado inicial.'

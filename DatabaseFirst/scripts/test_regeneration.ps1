$ErrorActionPreference = 'Stop'
$demoRoot = Split-Path $PSScriptRoot -Parent
$demo = Join-Path $demoRoot 'demo.ps1'
$record = Join-Path $demoRoot 'target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/records/ModelsRecord.java'
$table = Join-Path $demoRoot 'target/generated-sources/jooq/br/com/estoque/databasefirst/generated/tables/Models.java'
$evidence = Join-Path $demoRoot 'evidence'
New-Item -ItemType Directory -Force $evidence | Out-Null
if (!(Test-Path $record)) { throw 'Execute prepare e generate primeiro.' }
if (Select-String -Path $record -Pattern 'getDemoNote' -Quiet) { throw 'Execute restore antes deste teste.' }
$before = (Get-FileHash $record).Hash
Copy-Item $record (Join-Path $evidence 'ModelsRecord.before.java.txt')
try {
    & $demo add-column
    if ((Get-FileHash $record).Hash -ne $before) { throw 'A classe mudou antes de executar o gerador.' }
    & $demo generate
    if (!(Select-String -Path $record -Pattern 'getDemoNote' -Quiet)) { throw 'Getter nao foi gerado.' }
    if (!(Select-String -Path $record -Pattern 'setDemoNote' -Quiet)) { throw 'Setter nao foi gerado.' }
    if (!(Select-String -Path $table -Pattern 'DEMO_NOTE' -Quiet)) { throw 'Coluna tipada nao foi gerada.' }
    Copy-Item $record (Join-Path $evidence 'ModelsRecord.after.java.txt')
    & $demo inspect | Set-Content -Encoding UTF8 (Join-Path $evidence 'schema-after.txt')
    & $demo test
    Copy-Item (Join-Path $demoRoot 'target/surefire-reports/br.com.estoque.databasefirst.DatabaseFirstTest.txt') (Join-Path $evidence 'tests-added-column.txt')
} finally {
    & $demo restore
}
if (Select-String -Path $record -Pattern 'getDemoNote' -Quiet) { throw 'Campo permaneceu apos restauracao.' }
if ((Get-FileHash $record).Hash -ne $before) { throw 'Classe restaurada difere do estado inicial.' }
Copy-Item (Join-Path $demoRoot 'target/surefire-reports/br.com.estoque.databasefirst.DatabaseFirstTest.txt') (Join-Path $evidence 'tests-restored.txt')
@{ before_sha256=$before; restored_sha256=(Get-FileHash $record).Hash; generated_getter=$true; generated_setter=$true; generated_column=$true; restored=$true } | ConvertTo-Json | Set-Content -Encoding UTF8 (Join-Path $evidence 'regeneration.json')
Write-Output 'Regeneracao validada: campo gerado, CRUD aprovado com a coluna e estado inicial restaurado.'

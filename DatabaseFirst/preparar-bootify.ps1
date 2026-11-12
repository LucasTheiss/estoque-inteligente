param([string]$Projeto = 'bootify\ei-front')
$ErrorActionPreference = 'Stop'
if (-not [IO.Path]::IsPathRooted($Projeto)) { $Projeto = Join-Path $PSScriptRoot $Projeto }
$Projeto = [IO.Path]::GetFullPath($Projeto)
$javaRoot = Join-Path $Projeto 'src\main\java'
if (-not (Test-Path $javaRoot)) { throw "Extraia o projeto Bootify em $Projeto (a pasta deve conter pom.xml e src)." }
$changes = @()
foreach ($entity in @('Model', 'Tenant')) {
    $files = @(Get-ChildItem $javaRoot -Recurse -Filter "$entity.java" -File)
    if ($files.Count -ne 1) { throw "Esperado exatamente um $entity.java em $javaRoot; encontrados $($files.Count)." }
    $path = $files[0].FullName
    $original = [IO.File]::ReadAllText($path)
    $content = $original
    $table = if ($entity -eq 'Model') { 'models' } else { 'tenants' }
    if ($content -notmatch '@Entity\b') { throw "Entidade JPA ausente: $path" }
    if ($content -notmatch 'import jakarta.persistence.(Table|\*);') {
        if ($content -notmatch 'package [^;]+;') { throw "Package ausente: $path" }
        $content = [regex]::Replace($content, '(package [^;]+;)', ('$1' + "`n`nimport jakarta.persistence.Table;"), 1)
    }
    if ($content -match '@Table\s*\(') {
        # Bootify emits name first when present; preserve unique constraints.
        if ($content -match '@Table\s*\(\s*name\s*=\s*"[^"]*"') {
            $content = [regex]::Replace($content, '(@Table\s*\(\s*name\s*=\s*)"[^"]*"', ('$1"' + $table + '"'))
        } elseif ($content -match '@Table\s*\(\s*\)') {
            $content = [regex]::Replace($content, '@Table\s*\(\s*\)', ('@Table(name = "' + $table + '")'))
        } elseif ($content -match '@Table\s*\(\s*uniqueConstraints\s*=') {
            $content = [regex]::Replace($content, '@Table\s*\(', ('@Table(name = "' + $table + '", '))
        } else { throw "Formato @Table nao reconhecido em $path; nenhum arquivo foi alterado." }
    } else {
        $content = [regex]::Replace($content, '@Entity\b', ('@Entity' + "`n" + '@Table(name = "' + $table + '")'))
    }
    foreach ($annotation in @('SequenceGenerator', 'GeneratedValue')) {
        if ([regex]::Matches($content, "@$annotation\s*\([^)]*\)").Count -ne 1) {
            throw "Esperado um @$annotation em $path; nenhum arquivo foi alterado."
        }
    }
    $content = [regex]::Replace($content, '@SequenceGenerator\s*\([^)]*\)', ('@SequenceGenerator(name = "' + $table + '_id_generator", sequenceName = "' + $table + '_id_seq", allocationSize = 1)'))
    $content = [regex]::Replace($content, '@GeneratedValue\s*\([^)]*\)', ('@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "' + $table + '_id_generator")'))
    $changes += @{ Path = $path; Original = $original; Content = $content }
}
# Validate both entities before writing. Preserve the first original for recovery.
$utf8 = New-Object Text.UTF8Encoding($false)
foreach ($change in $changes) {
    if ($change.Content -ne $change.Original) {
        if (-not (Test-Path ($change.Path + '.databasefirst.bak'))) {
            [IO.File]::WriteAllText(($change.Path + '.databasefirst.bak'), $change.Original, $utf8)
        }
        [IO.File]::WriteAllText($change.Path, $change.Content, $utf8)
    }
}
Write-Host 'Mapeamentos prontos: models/models_id_seq e tenants/tenants_id_seq.'
Write-Host 'Use iniciar-bootify.ps1 para configurar a conexao, desativar Flyway e iniciar.'

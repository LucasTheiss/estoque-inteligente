param(
    [string]$Projeto = 'bootify\ei-front',
    [ValidateRange(1024, 65535)][int]$Porta = 8083
)
$ErrorActionPreference = 'Stop'
if (-not [IO.Path]::IsPathRooted($Projeto)) { $Projeto = Join-Path $PSScriptRoot $Projeto }
$Projeto = [IO.Path]::GetFullPath($Projeto)
$envFile = Join-Path $PSScriptRoot '.env'
if (-not (Test-Path $envFile)) { throw 'Execute iniciar-banco.ps1 primeiro.' }
$config = @{}
Get-Content $envFile | ForEach-Object {
    if ($_ -match '^([^#=]+)=(.*)$') { $config[$matches[1].Trim()] = $matches[2] }
}
if (-not $config['DF_DB_USER'] -or -not $config['DF_DB_PASSWORD']) { throw 'Credenciais DF_DB_USER/DF_DB_PASSWORD ausentes no .env.' }
$wrapper = Join-Path $Projeto 'mvnw.cmd'
if (-not (Test-Path $wrapper)) { throw "Extraia o download Bootify em $Projeto; mvnw.cmd nao encontrado." }
$java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin\java.exe' } else { (Get-Command java -ErrorAction Stop).Source }
$oldPreference = $ErrorActionPreference
$ErrorActionPreference = 'Continue'
$versionOutput = (& $java -version 2>&1 | Out-String)
$javaExit = $LASTEXITCODE
$ErrorActionPreference = $oldPreference
if ($javaExit -ne 0 -or $versionOutput -notmatch 'version "(\d+)') { throw 'Nao foi possivel detectar o JDK. Confira JAVA_HOME e java -version.' }
$javaVersion = [int]$matches[1]
if ($javaVersion -lt 17) { throw 'Use um JDK 17 ou superior, incluindo javac.' }
& (Join-Path $PSScriptRoot 'preparar-bootify.ps1') -Projeto $Projeto
$settings = @{
    JDBC_DATABASE_URL = 'jdbc:postgresql://localhost:5544/estoque_databasefirst'
    JDBC_DATABASE_USERNAME = $config['DF_DB_USER']
    JDBC_DATABASE_PASSWORD = $config['DF_DB_PASSWORD']
    SPRING_FLYWAY_ENABLED = 'false'
    SPRING_JPA_HIBERNATE_DDL_AUTO = 'validate'
}
$previous = @{}
foreach ($key in $settings.Keys) {
    $previous[$key] = [Environment]::GetEnvironmentVariable($key, 'Process')
    [Environment]::SetEnvironmentVariable($key, $settings[$key], 'Process')
}
Push-Location $Projeto
try {
    Write-Host "Compilando com Java $javaVersion. A primeira compilacao precisa de internet."
    & $wrapper "-Djava.version=$javaVersion" clean package
    if ($LASTEXITCODE -ne 0) { throw 'A compilacao falhou; o site nao foi iniciado. Confira o erro Maven acima.' }
    $jars = @(Get-ChildItem '.\target\*.jar' -File | Where-Object { $_.Name -notmatch '-(sources|javadoc|tests|plain)\.jar$' })
    if ($jars.Count -ne 1) { throw 'Esperado um JAR executavel em target.' }
    Write-Host "Aguarde a mensagem Started e abra http://localhost:$Porta/models . Ctrl+C encerra."
    & $java -jar $jars[0].FullName --spring.profiles.active=production "--server.port=$Porta" --server.address=127.0.0.1
    if ($LASTEXITCODE -ne 0) { throw 'O aplicativo encerrou com erro; confira o log acima.' }
} finally {
    Pop-Location
    foreach ($key in $previous.Keys) { [Environment]::SetEnvironmentVariable($key, $previous[$key], 'Process') }
}

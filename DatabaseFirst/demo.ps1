param([ValidateSet('prepare','generate','run','test','main-test','inspect','add-column','restore','stop','credentials')][string]$Action = 'prepare')
$ErrorActionPreference = 'Stop'
$demoRoot = $PSScriptRoot
$envFile = Join-Path $demoRoot '.env'
if (!(Test-Path $envFile)) {
    if ($Action -ne 'prepare') { throw 'Execute primeiro: .\DatabaseFirst\demo.ps1 prepare' }
    $dbSecret = [guid]::NewGuid().ToString('N') + [guid]::NewGuid().ToString('N')
    $webSecret = [guid]::NewGuid().ToString('N')
    @("DF_DB_USER=df_demo", "DF_DB_PASSWORD=$dbSecret", "DF_WEB_USER=professor", "DF_WEB_PASSWORD=$webSecret") | Set-Content -Encoding ascii $envFile
}
$composeArgs = @('compose','--env-file',$envFile,'-f',(Join-Path $demoRoot 'compose.yaml'))
function Docker([string[]]$Arguments) {
    & docker.exe @composeArgs @Arguments
    if ($LASTEXITCODE -ne 0) { throw "Docker falhou (codigo $LASTEXITCODE)." }
}
function Maven([string[]]$Goals) { Docker (@('run','--rm','maven','mvn','-B','-ntp') + $Goals) }
function Sql([string]$Name) {
    Get-Content -Raw -Encoding UTF8 (Join-Path $demoRoot "sql/$Name.sql") | & docker.exe @composeArgs exec -T db sh -c 'psql -v ON_ERROR_STOP=1 -1 -U "$POSTGRES_USER" -d estoque_databasefirst'
    if ($LASTEXITCODE -ne 0) { throw "SQL $Name falhou." }
}
switch ($Action) {
    prepare { Docker @('up','-d','--wait','db'); Maven @('flyway:migrate'); Sql 'seed' }
    generate { Maven @('-Pcodegen','generate-sources') }
    run { Maven @('package','-DskipTests'); Docker @('run','--rm','--service-ports','maven','java','-jar','target/databasefirst-1.0.0.jar') }
    test { Maven @('verify') }
    main-test {
        Docker @('--profile','verify','up','-d','--wait','db','mqtt')
        Docker @('run','--rm','-e','TEST_MQTT_URI=tcp://mqtt:1883','maven','mvn','-B','-ntp','-f','../pom.xml','test')
    }
    inspect { Sql 'inspect' }
    add-column { Sql 'add-column' }
    restore { Sql 'restore'; Maven @('-Pcodegen','generate-sources'); Maven @('verify') }
    stop { Docker @('--profile','verify','down') }
    credentials { Get-Content $envFile | Where-Object { $_ -like 'DF_WEB_*' } }
}

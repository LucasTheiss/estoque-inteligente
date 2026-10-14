$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    foreach ($tool in @('java', 'mvn', 'docker')) {
        if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) {
            throw "Comando $tool indisponível. Instale Java 21+, Maven 3.9+ e Docker Desktop, ou use scripts/demo.ps1."
        }
    }
    docker compose --env-file .env.demo up -d --wait db mqtt
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao iniciar PostgreSQL e MQTT. Confira o Docker Desktop e os logs de db e mqtt.' }
    Write-Host 'Spring local: http://localhost:8081/login. Encerre com Ctrl+C.'
    mvn spring-boot:run '-Dspring-boot.run.profiles=local'
    if ($LASTEXITCODE -ne 0) { throw 'Spring terminou com erro. Consulte a primeira causa no log acima.' }
} finally { Pop-Location }

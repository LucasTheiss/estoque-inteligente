$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    $jdkBin = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin' } else { Split-Path (Get-Command javac -ErrorAction Stop).Source }
    $javac = Join-Path $jdkBin 'javac.exe'
    $jar = Join-Path $jdkBin 'jar.exe'
    $java = Join-Path $jdkBin 'java.exe'
    $version = ([xml](Get-Content pom.xml -Raw)).project.version
    $build = Join-Path 'target/reuso' ([guid]::NewGuid().ToString())
    $classes = Join-Path $build 'classes'
    $consumer = Join-Path $build 'consumer'
    New-Item -ItemType Directory -Force $classes, $consumer | Out-Null
    $sources = @(
        'strategy/Quantidade', 'strategy/QuantidadeInteira', 'strategy/QuantidadeFracionada',
        'strategy/QuantidadeMonetaria', 'strategy/CalculoEstoque',
        'service/OperacaoEstoque', 'service/EntradaEstoque', 'service/SaidaEstoque', 'service/AjusteEstoque'
    ) | ForEach-Object { "src/main/java/br/com/estoque/$_.java" }
    & $javac --release 21 -encoding UTF-8 -d $classes @sources
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar as regras reutilizaveis.' }
    $manifest = Join-Path $build 'MANIFEST.MF'
    @("Manifest-Version: 1.0", "Implementation-Title: estoque-regras", "Implementation-Version: $version", '') |
        Set-Content -Encoding Ascii $manifest
    $artifact = "target/reuso/estoque-regras-$version.jar"
    & $jar --create --file $artifact --manifest $manifest -C $classes . -C docs/reuso README.md
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao criar o JAR.' }
    & $javac --release 21 -encoding UTF-8 -cp $artifact -d $consumer examples/reuso/ConsumidorEstoque.java
    if ($LASTEXITCODE -ne 0) { throw 'O consumidor nao compilou usando o JAR.' }
    & $java -ea -cp "$artifact;$consumer" ConsumidorEstoque
    if ($LASTEXITCODE -ne 0) { throw 'O teste de consumo do JAR falhou.' }
    Write-Output "JAR verificado: $artifact"
} finally { Pop-Location }

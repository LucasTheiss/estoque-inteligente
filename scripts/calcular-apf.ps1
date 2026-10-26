param([string]$Catalogo = "$PSScriptRoot/../docs/apf/contagem.json")
$ErrorActionPreference = 'Stop'
$dados = Get-Content -LiteralPath $Catalogo -Raw -Encoding UTF8 | ConvertFrom-Json
function Indice-Entrada([int]$ar, [int]$td) {
    $r = if ($ar -le 1) {0} elseif ($ar -eq 2) {1} else {2}
    $c = if ($td -le 4) {0} elseif ($td -le 15) {1} else {2}
    $matriz = @(@(0,0,1),@(0,1,2),@(1,2,2))
    return $matriz[$r][$c]
}
function Indice-Saida([int]$ar, [int]$td) {
    $r = if ($ar -le 1) {0} elseif ($ar -le 3) {1} else {2}
    $c = if ($td -le 5) {0} elseif ($td -le 19) {1} else {2}
    $matriz = @(@(0,0,1),@(0,1,2),@(1,2,2))
    return $matriz[$r][$c]
}
function Indice-Dados([int]$rl, [int]$td) {
    $r = if ($rl -eq 1) {0} elseif ($rl -le 5) {1} else {2}
    $c = if ($td -le 19) {0} elseif ($td -le 50) {1} else {2}
    $matriz = @(@(0,0,1),@(0,1,2),@(1,2,2))
    return $matriz[$r][$c]
}
$linhas = [System.Collections.Generic.List[object]]::new()
$nomes = @('Simples','Moderada','Complexa')
$ids = @{}
foreach ($d in $dados.dados) {
    if ($ids.ContainsKey($d.id)) {throw 'ID repetido'}
    $ids[$d.id]=$true
    $td=@($d.campos | Select-Object -Unique).Count
    $rl=@($d.registrosLogicos).Count
    $i=Indice-Dados $rl $td
    $pesos=if($d.tipo -eq 'ALI'){@(7,10,15)}else{@(5,7,10)}
    $linhas.Add([pscustomobject]@{id=$d.id;tipo=$d.tipo;nome=$d.nome;ar_rl=$rl;td=$td;tdEntrada=0;tdSaida=0;complexidade=$nomes[$i];pf=$pesos[$i]})
}
foreach ($f in $dados.funcoes) {
    foreach($ref in $f.arquivos){if(-not $ids.ContainsKey($ref)){throw "Arquivo desconhecido: $ref"}}
    $ar=@($f.arquivos | Select-Object -Unique).Count
    $entrada=@(@($f.entrada)+@($f.controle) | Select-Object -Unique).Count
    $saida=@($f.saida | Where-Object {$_ -notin $f.entrada} | Select-Object -Unique).Count
    $td=@(@($f.entrada)+@($f.saida)+@($f.controle) | Select-Object -Unique).Count
    switch($f.tipo){
        'EE' {$i=Indice-Entrada $ar $td; $pesos=@(3,4,6)}
        'SE' {$i=Indice-Saida $ar $td; $pesos=@(4,5,7)}
        'CE' {
            $arEntrada=if($null -ne $f.arEntrada){[int]$f.arEntrada}else{$ar}
            $arSaida=if($null -ne $f.arSaida){[int]$f.arSaida}else{$ar}
            $i=[Math]::Max((Indice-Entrada $arEntrada $entrada),(Indice-Saida $arSaida $saida)); $pesos=@(3,4,6)
        }
        default {throw 'Tipo desconhecido'}
    }
    $linhas.Add([pscustomobject]@{id=$f.id;tipo=$f.tipo;nome=$f.nome;ar_rl=$ar;td=$td;tdEntrada=$entrada;tdSaida=$saida;complexidade=$nomes[$i];pf=$pesos[$i]})
}
foreach($c in $dados.caracteristicas){if($c.nota -lt 0 -or $c.nota -gt 5){throw 'NI fora de 0 a 5'}}
if(@($dados.caracteristicas).Count -ne 14){throw 'Esperadas 14 caracteristicas'}
$ni=($dados.caracteristicas | Measure-Object nota -Sum).Sum
$pfb=($linhas | Measure-Object pf -Sum).Sum
$fa=[decimal]0.65+[decimal]0.01*$ni
$pfa=$pfb*$fa
$esforco=$pfa*$dados.premissas.horasPorPF
$prazo=$esforco/($dados.premissas.integrantes*$dados.premissas.horasPorDia)
$custo=$esforco*$dados.premissas.valorHora
$resumo=[ordered]@{NI=$ni;PFB=$pfb;FA=$fa;PFA=$pfa;esforcoHoras=$esforco;prazoDias=[Math]::Round($prazo,2);prazoDiasUteisInteiros=[Math]::Ceiling($prazo);custoReais=$custo}
$pasta=Split-Path -Parent (Resolve-Path -LiteralPath $Catalogo)
$saidaJson=[ordered]@{resumo=$resumo;funcoes=$linhas}
$utf8=[System.Text.UTF8Encoding]::new($false)
[System.IO.File]::WriteAllText((Join-Path $pasta 'resultados.json'),($saidaJson | ConvertTo-Json -Depth 8),$utf8)
$md=[System.Collections.Generic.List[string]]::new()
$md.Add('# Memoria de calculo gerada')
$md.Add('')
$md.Add('Fonte: contagem.json. Recalcular com scripts/calcular-apf.ps1. AR/RL = arquivos referenciados ou registros logicos. Em CE, a complexidade e o maior resultado entre entrada e saida, usando os AR especificos de cada lado declarados no catalogo.')
$md.Add('')
$md.Add('| ID | Tipo | Funcao | AR/RL | TD | TD entrada CE | TD saida CE | Complexidade | PF |')
$md.Add('|---|---|---|---:|---:|---:|---:|---|---:|')
foreach($r in $linhas){$md.Add("| $($r.id) | $($r.tipo) | $($r.nome) | $($r.ar_rl) | $($r.td) | $($r.tdEntrada) | $($r.tdSaida) | $($r.complexidade) | $($r.pf) |")}
$md.Add('');$md.Add('## Totais por tipo');$md.Add('')
foreach($g in ($linhas | Group-Object tipo)){$md.Add("- $($g.Name): $($g.Count) funcoes; $(($g.Group | Measure-Object pf -Sum).Sum) PF.")}
$md.Add('');$md.Add('## Resultado');$md.Add('')
foreach($chave in $resumo.Keys){$md.Add("- ${chave}: $($resumo[$chave])")}
[System.IO.File]::WriteAllLines((Join-Path $pasta 'CALCULOS.md'),$md,$utf8)
$resumo | Format-Table -AutoSize

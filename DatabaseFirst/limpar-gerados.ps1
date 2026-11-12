[CmdletBinding(SupportsShouldProcess = $true)]
param()
$ErrorActionPreference = 'Stop'
$root = [IO.Path]::GetFullPath($PSScriptRoot).TrimEnd('\')
$paths = @()
foreach ($name in @('target', 'bootify')) {
    $path = [IO.Path]::GetFullPath((Join-Path $root $name))
    if ([IO.Path]::GetDirectoryName($path) -ne $root) { throw "Destino fora de DatabaseFirst: $path" }
    if (Test-Path -LiteralPath $path) {
        # Refuse links/junctions before recursive deletion, including nested ones.
        $pending = New-Object 'System.Collections.Generic.Stack[string]'
        $pending.Push($path)
        while ($pending.Count -gt 0) {
            $item = Get-Item -LiteralPath $pending.Pop() -Force
            if ($item.Attributes -band [IO.FileAttributes]::ReparsePoint) {
                throw "Link/junction encontrado; limpeza cancelada sem apagar arquivos: $($item.FullName)"
            }
            if ($item.PSIsContainer) {
                foreach ($child in Get-ChildItem -LiteralPath $item.FullName -Force) { $pending.Push($child.FullName) }
            }
        }
        $paths += $path
    }
}
foreach ($path in $paths) {
    if ($PSCmdlet.ShouldProcess($path, 'Apagar arquivos gerados e download Bootify')) {
        Remove-Item -LiteralPath $path -Recurse -Force
        Write-Host "Removido: $path"
    }
}
Write-Host 'Scripts, roteiros, .env e banco preservados. Para regenerar, siga PASSO_A_PASSO_PROFESSOR.md.'

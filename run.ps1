$ErrorActionPreference = "Stop"

& (Join-Path $PSScriptRoot "build.ps1")

$outDir = Join-Path $PSScriptRoot "out"
& java -cp $outDir thejourney.Main

if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

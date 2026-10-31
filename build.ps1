$ErrorActionPreference = "Stop"

$root = $PSScriptRoot
$outDir = Join-Path $root "out"

if (Test-Path $outDir) {
    Remove-Item -Recurse -Force $outDir
}

New-Item -ItemType Directory -Path $outDir | Out-Null

$sources = Get-ChildItem -Path (Join-Path $root "src") -Recurse -Filter *.java |
    ForEach-Object { $_.FullName }

& javac --release 17 -encoding UTF-8 -d $outDir $sources

if ($LASTEXITCODE -ne 0) {
    throw "javac failed with exit code $LASTEXITCODE"
}

$assetsDir = Join-Path $root "assets"
if (Test-Path $assetsDir) {
    Copy-Item -Path $assetsDir -Destination (Join-Path $outDir "assets") -Recurse -Force
}

Write-Host "Build OK -> $outDir"

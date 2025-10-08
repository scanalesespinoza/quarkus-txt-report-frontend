param(
    [ValidateSet("podman", "docker")]
    [string]$Builder = "podman",
    [switch]$SkipPush
)

$ErrorActionPreference = "Stop"

$projectRoot = Resolve-Path "$PSScriptRoot\.."
$mvnw = Join-Path $projectRoot "mvnw"

Write-Host "=== Running Quarkus native build from $projectRoot ===`n"

$args = @(
    "clean", "package",
    "-Dquarkus.native.enabled=true",
    "-Dquarkus.native.container-build=true",
    "-Dquarkus.native.container-runtime=$Builder",
    "-Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21",
    "-Dquarkus.container-image.builder=$Builder",
    "-Dquarkus.container-image.build=true",
    "-Dquarkus.container-image.registry=quay.io",
    "-Dquarkus.container-image.group=sergio_canales_e/quarkus",
    "-Dquarkus.container-image.name=txt-report-frontend"
)

if ($SkipPush) {
    Write-Host ">> Container push disabled (`$SkipPush supplied)" -ForegroundColor Yellow
    $args += "-Dquarkus.container-image.push=false"
} else {
    $args += "-Dquarkus.container-image.push=true"
}

$args += "quarkus:image-build"

try {
    & $mvnw @args
    Write-Host "`n=== Build finished ===" -ForegroundColor Green
}
catch {
    Write-Host "`nBuild failed:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Yellow
    exit 1
}

# build-quarkus.ps1
$ErrorActionPreference = "Stop"

try {
    $projectRoot = Resolve-Path "$PSScriptRoot\.."
    $mvnw = Join-Path $projectRoot "mvnw"

    Write-Host "=== Iniciando build nativo Quarkus desde: $projectRoot ===`n"

    $args = @(
        "clean", "package",
        "-Dquarkus.package.type=native",
        "-Dquarkus.native.container-build=true",
        "-Dquarkus.native.container-runtime=podman",
        "-Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21",
        "-Dquarkus.container-image.builder=podman",
        "-Dquarkus.container-image.build=true",
        "-Dquarkus.container-image.push=true",
        "-Dquarkus.container-image.registry=quay.io",
        "-Dquarkus.container-image.group=sergio_canales_e/quarkus",
        "-Dquarkus.container-image.name=txt-report-frontend",
        "quarkus:image-build"
    )

    # Ejecutar y mostrar salida en consola
    & $mvnw @args
}
catch {
    Write-Host "`n❌ Error durante la compilación:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Yellow
    Write-Host "`nPresiona una tecla para cerrar..." -ForegroundColor Gray
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
}

# env_manager.ps1 — Gestiona variables de entorno en PowerShell
# Uso:
#   .\env_manager.ps1 -Mode setup   # Para ingresar valores y guardarlos en 'env'
#   .\env_manager.ps1 -Mode set     # Para cargar variables desde 'env' al entorno actual
# Ejecución:
#   . .\env_manager.ps1 -Mode set    # Carga las variables en la sesión actual

param (
    [Parameter(Mandatory=$true)]
    [ValidateSet("setup","set")]
    [string]$Mode
)

$EnvFile = ".\env"

function Show-Usage {
    Write-Host "Uso:"
    Write-Host "  .\env_manager.ps1 -Mode setup   # Ingresar valores y guardarlos en 'env'"
    Write-Host "  .\env_manager.ps1 -Mode set     # Cargar variables desde 'env' al entorno actual"
}

function Prompt-ForValue {
    param (
        [string]$VarName,
        [string]$Desc,
        [string]$CurrentValue
    )
    if ($CurrentValue) {
        # Uso de subexpresión para evitar conflicto con ':'
        Write-Host -NoNewline "$($Desc) [$CurrentValue]: "
        $input = Read-Host
        if ([string]::IsNullOrEmpty($input)) {
            return $CurrentValue
        } else {
            return $input
        }
    } else {
        Write-Host -NoNewline "$($Desc): "
        return Read-Host
    }
}

function Setup {
    $defaults = @{}
    if (Test-Path $EnvFile) {
        Get-Content $EnvFile | ForEach-Object {
            if ($_ -match "^\s*#") { return }
            $parts = $_ -split '=', 2
            $defaults[$parts[0]] = $parts[1]
        }
    }

    "# Archivo env generado por env_manager.ps1" | Out-File $EnvFile -Encoding UTF8

    $vars = @(
        @{ Name="QUARKUS_HTTP_PORT"; Desc="Puerto HTTP"; Default=$defaults["QUARKUS_HTTP_PORT"] }
        @{ Name="QUARKUS_NATIVE_CONTAINER_BUILD"; Desc="Native container build (true/false)"; Default=$defaults["QUARKUS_NATIVE_CONTAINER_BUILD"] }
        @{ Name="QUARKUS_CONTAINER_IMAGE_BUILD"; Desc="Construcción de imagen (true/false)"; Default=$defaults["QUARKUS_CONTAINER_IMAGE_BUILD"] }
        @{ Name="QUARKUS_CONTAINER_IMAGE_PUSH"; Desc="Push de imagen (true/false)"; Default=$defaults["QUARKUS_CONTAINER_IMAGE_PUSH"] }
        @{ Name="QUARKUS_CONTAINER_IMAGE_REGISTRY"; Desc="Registry de contenedor"; Default=$defaults["QUARKUS_CONTAINER_IMAGE_REGISTRY"] }
        @{ Name="QUARKUS_CONTAINER_IMAGE_GROUP"; Desc="Grupo en registry"; Default=$defaults["QUARKUS_CONTAINER_IMAGE_GROUP"] }
        @{ Name="QUARKUS_CONTAINER_IMAGE_NAME"; Desc="Nombre de la imagen"; Default=$defaults["QUARKUS_CONTAINER_IMAGE_NAME"] }
        @{ Name="QUARKUS_CONTAINER_IMAGE_TAG"; Desc="Tag de la imagen"; Default=$defaults["QUARKUS_CONTAINER_IMAGE_TAG"] }
        @{ Name="QUARKUS_CONTAINER_IMAGE_BUILDER"; Desc="Builder de contenedor"; Default=$defaults["QUARKUS_CONTAINER_IMAGE_BUILDER"] }
        @{ Name="PLATFORM_QUARKUS_NATIVE_BUILDER_IMAGE"; Desc="Builder image Mandrel"; Default=$defaults["PLATFORM_QUARKUS_NATIVE_BUILDER_IMAGE"] }
        @{ Name="APP_MAX_REPORTS"; Desc="Máximo de reportes en cache"; Default=$defaults["APP_MAX_REPORTS"] }
        @{ Name="QUARKUS_HTTP_ENCODING_ENABLED"; Desc="Forzar HTTP UTF-8 (true/false)"; Default=$defaults["QUARKUS_HTTP_ENCODING_ENABLED"] }
        @{ Name="QUARKUS_HTTP_ENCODING_CHARSET"; Desc="Charset HTTP"; Default=$defaults["QUARKUS_HTTP_ENCODING_CHARSET"] }
    )

    foreach ($var in $vars) {
        $value = Prompt-ForValue -VarName $var.Name -Desc $var.Desc -CurrentValue $var.Default
        "$($var.Name)=$value" | Out-File $EnvFile -Append -Encoding UTF8
    }

    Write-Host "Archivo '$EnvFile' generado con las variables:"
    Get-Content $EnvFile | ForEach-Object { Write-Host $_ }
}

function Set-Vars {
    if (-not (Test-Path $EnvFile)) {
        Write-Error "No existe el archivo '$EnvFile'. Ejecuta '.\env_manager.ps1 -Mode setup' primero."
        exit 1
    }

    Get-Content $EnvFile | ForEach-Object {
        if ($_ -match "^\s*#") { return }
        $parts = $_ -split '=', 2
        $name = $parts[0]
        $val = $parts[1]
        Set-Item -Path "Env:$name" -Value $val
        Write-Host "CARGADO: $name=$val"
    }
}

switch ($Mode) {
    "setup" { Setup }
    "set"   { Set-Vars }
    default { Show-Usage; exit 1 }
}

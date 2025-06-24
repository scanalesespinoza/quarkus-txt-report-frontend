param(
    [Parameter(Mandatory = $true)]
    [string]$Tag
)

# Validar formato vX.Y.Z
if ($Tag -notmatch '^v\d+\.\d+\.\d+$') {
    Write-Error "❌ Formato inválido: use vX.Y.Z (ej: v1.2.3)"
    exit 1
}

$Version = $Tag.TrimStart("v")
$RootDir = Resolve-Path "$PSScriptRoot\.."
$PomPath = Join-Path $RootDir "pom.xml"
$AppPropsPath = Join-Path $RootDir "src\main\resources\application.properties"

Write-Host "🔧 Actualizando a versión $Version"
Write-Host "📁 Proyecto en: $RootDir"

# 1. Actualizar <version> en pom.xml (solo primera coincidencia)
$PomContent = Get-Content $PomPath
$PomUpdated = $false
for ($i = 0; $i -lt $PomContent.Count; $i++) {
    if ($PomContent[$i] -match '<version>.*</version>') {
        $PomContent[$i] = $PomContent[$i] -replace '<version>.*</version>', "<version>$Version</version>"
        $PomUpdated = $true
        break
    }
}
if ($PomUpdated) {
    $PomContent | Set-Content $PomPath -Encoding UTF8
    Write-Host "✅ pom.xml actualizado."
}
else {
    Write-Warning "⚠️ No se encontró la etiqueta <version> en pom.xml"
}

# 2. Actualizar quarkus.container-image.tag
$PropsContent = Get-Content $AppPropsPath
$UpdatedProps = $false
for ($i = 0; $i -lt $PropsContent.Count; $i++) {
    if ($PropsContent[$i] -match '^quarkus\.container-image\.tag=.*QUARKUS_CONTAINER_IMAGE_TAG:') {
        $pattern = '(\$\{QUARKUS_CONTAINER_IMAGE_TAG:)[^}]+(\})'
        $PropsContent[$i] = [regex]::Replace($PropsContent[$i], $pattern, '${1}' + $Version + '${2}')
        $UpdatedProps = $true
        break
    }
}
if (-not $UpdatedProps) {
    $PropsContent += "quarkus.container-image.tag=`$\{QUARKUS_CONTAINER_IMAGE_TAG:$Version`}"
}
$PropsContent | Set-Content $AppPropsPath -Encoding UTF8
Write-Host "✅ application.properties actualizado."

# 3. Mostrar resumen de cambios
Write-Host "📄 Cambios locales:"
git -C $RootDir status --short $PomPath $AppPropsPath

# 4. Commit opcional
$commit = Read-Host "¿Deseas hacer commit de los cambios? (s/N)"
if ($commit -match '^[sS]$') {
    git -C $RootDir add --all
    git -C $RootDir commit -m "chore: update version to $Tag"
    Write-Host "✅ Commit realizado."
}

# 5. Crear y pushear tag opcional
$tagPush = Read-Host "¿Deseas crear y pushear el tag $Tag? (s/N)"
if ($tagPush -match '^[sS]$') {
    git -C $RootDir tag $Tag
    git -C $RootDir push origin $Tag
    Write-Host "🏷️ Tag $Tag creado y enviado a remoto."
}

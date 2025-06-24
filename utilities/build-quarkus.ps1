# build-quarkus.ps1 - Build Quarkus app as native or traditional Java and push container image
param(
  [ValidateSet("native", "java")][string]$Mode = "native"
)

$baseImageName = "txt-report-frontend"
$imageName = if ($Mode -eq "native") { "$baseImageName-native" } else { "$baseImageName-java" }

$buildArgs = @(
  "clean", "package",
  "-Dquarkus.container-image.builder=podman",
  "-Dquarkus.container-image.registry=quay.io",
  "-Dquarkus.container-image.group=sergio_canales_e/quarkus",
  "-Dquarkus.container-image.name=$imageName"
)

if ($Mode -eq "native") {
  $buildArgs += @(
    "-Dquarkus.package.type=native",
    "-Dquarkus.native.container-build=true",
    "-Dquarkus.native.container-runtime=podman",
    "-Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21"
  )
}

# Always build and push container image
$buildArgs += @(
  "-Dquarkus.container-image.build=true",
  "-Dquarkus.container-image.push=true"
)

Write-Host "▶️ Building Quarkus app in '$Mode' mode with image name '$imageName'..."
& .\mvnw @buildArgs

if ($LASTEXITCODE -eq 0) {
  Write-Host "✅ Build and container push successful!"
} else {
  Write-Error "❌ Build failed with exit code $LASTEXITCODE"
}

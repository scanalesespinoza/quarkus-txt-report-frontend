# Health Behavior Simulator Utilities

This directory contains utilities to create and delete multiple `health-behavior-simulator` deployments
in your OpenShift/Kubernetes cluster for testing purposes.

## Files

- `simulator-deployment.yaml.tpl` - Template file; contains placeholders `{{ index }}` and `{{ behavior }}`.
- `generate-simulators.sh` - Bash script to create/delete simulators.
- `generate-simulators.ps1` - PowerShell script equivalent.
- `README.md` - This documentation.

## Usage

### Bash

```bash
# Create 20 simulators
./generate-simulators.sh create 20
# Delete all simulators
./generate-simulators.sh delete
```

### PowerShell

```powershell
# Create 20 simulators
.\generate-simulators.ps1 -Action create -Count 20
# Delete all simulators
.\generate-simulators.ps1 -Action delete
```

Ensure you have `oc` or `kubectl` in your PATH and are logged into the appropriate cluster with access to the target namespace (`kpulse` by default).


```ps1
PS C:\Users\sergio\git\quarkus-txt-report-frontend\utilities> .\mvnw clean package `
   '-Dquarkus.package.type=native' `
   '-Dquarkus.native.container-build=true' `
   '-Dquarkus.native.container-runtime=podman' `
   '-Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21' `
   '-Dquarkus.container-image.builder=podman' `
   '-Dquarkus.container-image.build=true' `
   '-Dquarkus.container-image.push=true' `
   '-Dquarkus.container-image.registry=quay.io' `
   '-Dquarkus.container-image.group=sergio_canales_e/quarkus' `
   '-Dquarkus.container-image.name=txt-report-frontend' `
   '-Dquarkus.container-image.tag=1.0.4'
   quarkus:image-build
```

Delete all health-behavior-simulator-sim

```ps1
$deployments = oc get deploy -o name | Where-Object { $_ -like "*health-behavior-simulator-sim-*" }
foreach ($d in $deployments) {
    oc delete $d
}
```

```bash
oc get deploy -n <tu-namespace> -o name | grep 'health-behavior-simulator-sim-' | xargs oc delete -n <tu-namespace>
```
# Health Behavior Simulator Utilities

This directory bundles helper scripts that make it easy to simulate unhealthy workloads and exercise the monitoring pipeline before rolling it into a production cluster.

## Files
- `simulator-deployment.yaml.tpl` – YAML template used by the generator scripts.
- `generate-simulators.sh` / `generate-simulators.ps1` – create or delete multiple simulator deployments.
- `env_manager.sh` / `env_manager.ps1` – optional helpers to render an `.env` file with build parameters.
- `build-quarkus.ps1` – wrapper that performs a native build and container image push via Quarkus.
- `update-version.sh` / `update-version.ps1` – bump the project version across sources.

## Usage
### Create or delete simulators
```bash
# create 20 simulators with randomly assigned behaviors
./generate-simulators.sh create 20

# delete every simulator created with the template
./generate-simulators.sh delete
```

```powershell
.\generate-simulators.ps1 -Action create -Count 20
.\generate-simulators.ps1 -Action delete
```

`oc` (or `kubectl`) must be on your `PATH` and logged into the target namespace. Update the default namespace inside the scripts if required.

### Build the Quarkus native image with Podman
```powershell
.\build-quarkus.ps1
```

The script runs `mvn clean package` with the native profile, builds and pushes the container image defined in `application.properties`, and emits the artifact inside `target/`.

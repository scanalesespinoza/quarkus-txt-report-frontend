# txt-report-frontend

Small Quarkus web UI that stores CSV snapshots of workload availability and renders summary/detail dashboards for platform stability reviews.

The repo now contains everything required to cover the application life cycle:

- Build and test the Quarkus app.
- Generate and push a native container image.
- Deploy runtime pieces (frontend, cronjob, helpers) to Kubernetes or OpenShift.

## Prerequisites
- JDK 21+
- Maven 3.8+
- Podman or Docker (used to build the container image)
- Access to a container registry (Quay.io is used in the defaults)
- `oc` or `kubectl` for cluster operations

## Local development
```bash
./mvnw quarkus:dev
```

## Build and publish the container image
The defaults live in `src/main/resources/application.properties`. Adjust registry, image name, and tag as needed.

```powershell
.\utilities\build-quarkus.ps1
```

Or run the underlying Maven goal manually:

```bash
./mvnw clean package \
  -Dquarkus.package.type=native \
  -Dquarkus.native.container-build=true \
  -Dquarkus.native.container-runtime=podman \
  -Dquarkus.container-image.builder=podman \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.push=true \
  -Dquarkus.container-image.registry=quay.io \
  -Dquarkus.container-image.group=sergio_canales_e/quarkus \
  -Dquarkus.container-image.name=txt-report-frontend \
  -Dquarkus.container-image.tag=1.0.9
```

To run the image locally:

```bash
podman run --rm -it \
  -p 8080:8080 \
  -e QUARKUS_HTTP_HOST=0.0.0.0 \
  -e TEAMS_WEBHOOK_URL="https://example.com/teams-webhook" \
  quay.io/sergio_canales_e/quarkus/txt-report-frontend:1.0.9
```

## Kubernetes / OpenShift deployment
All manifests reside under `manifests/`.

1. Update the image reference in `manifests/frontend-deployment.yaml` if you changed registry or tag.
2. Create or update the Secret with your Microsoft Teams webhook URL:
   ```bash
   oc apply -f manifests/secret.yaml
   ```
   Replace the placeholder value or use:
   ```bash
   oc create secret generic teams-webhook-secret \
     --from-literal=url="https://your-webhook-url" \
     --dry-run=client -o yaml | oc apply -f -
   ```
3. Apply the ConfigMap, Deployment, Service, and CronJob:
   ```bash
   oc apply -k manifests/
   ```
   If you prefer discrete commands:
   ```bash
   oc apply -f manifests/configmap.yaml
   oc apply -f manifests/frontend-deployment.yaml
   oc apply -f manifests/frontend-service.yaml
   oc apply -f manifests/cronjob.yaml
   ```
4. (Optional) Use the mock simulator to produce unhealthy workloads:
   ```bash
   oc apply -f manifests/mock-manifests/behavior-deployment.yaml
   ```

> **Note:** The CronJob runs `oc` inside the cluster. Ensure the default service account in the target namespace has permissions to list deployments and deploymentconfigs, or bind a dedicated service account accordingly.

## Utilities
- `utilities/build-quarkus.ps1`: native build + image push wrapper.
- `utilities/update-version.*`: bump the project version across sources.
- `utilities/generate-simulators.*`: quick way to create multiple failing workloads.
- `utilities/env_manager.*`: render `.env` files with common parameters.

## CI/CD
- `.github/workflows/pr-validation.yml` builds and tests every pull request targeting `main` and enables auto-merge when the checks succeed.
- `.github/workflows/maven-publish.yml` validates tagged releases and publishes the Maven artifact.
- `.github/workflows/container-build.yml` compiles the native binary, builds the container image, and (optionally) pushes it to Quay. Configure the `QUAY_USERNAME` and `QUAY_PASSWORD` secrets to enable the push step.

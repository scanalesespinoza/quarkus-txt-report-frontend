# Deployment Assets

This folder contains the Kubernetes/OpenShift YAML needed to run the monitoring workflow end to end.

## Files
- `configmap.yaml` – ships the `run.sh` script consumed by the CronJob.
- `cronjob.yaml` – executes the script on a schedule (uses the `oc` CLI inside the cluster).
- `frontend-deployment.yaml` – deploys the Quarkus frontend.
- `frontend-service.yaml` – exposes the frontend inside the cluster.
- `kustomization.yaml` – bundle of the resources above for `oc apply -k`.
- `secret.yaml` – helper template to store the Microsoft Teams webhook URL (update before applying).
- `mock-manifests/behavior-deployment.yaml` – optional synthetic workload that can flip between healthy and unhealthy states.

## Usage
```bash
# create/update runtime config
oc apply -f secret.yaml           # update the URL first or use oc create secret ...
oc apply -k .
```

Use role bindings to give the namespace service account access to list deployments and deploymentconfigs across the namespaces you care about; otherwise the CronJob will not see all workloads.

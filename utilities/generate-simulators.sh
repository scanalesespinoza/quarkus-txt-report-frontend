#!/usr/bin/env bash
# generate-simulators.sh - create or delete health-behavior-simulator deployments
# Usage: 
#   ./generate-simulators.sh create <count>  # create <count> simulators
#   ./generate-simulators.sh delete          # delete all simulators

ACTION="$1"
COUNT="${2:-20}"
NAMESPACE="kpulse"
TEMPLATE_FILE="simulator-deployment.yaml.tpl"

if [[ "$ACTION" = "create" ]]; then
  for i in $(seq 1 "$COUNT"); do
    # Random behavior: normal, notready, or restart
    case $((RANDOM % 3)) in
      0) BEHAVIOR="ok" ;;
      1) BEHAVIOR="notready" ;;
      2) BEHAVIOR="restart" ;;
    esac
    DEPLOYMENT_YAML="sim-${i}.yaml"
    sed -e "s/{{ index }}/sim-${i}/g" \
        -e "s/{{ behavior }}/$BEHAVIOR/g" "$TEMPLATE_FILE" > "$DEPLOYMENT_YAML"
    oc apply -f "$DEPLOYMENT_YAML" -n "$NAMESPACE"
  done
elif [[ "$ACTION" = "delete" ]]; then
  for file in sim-*.yaml; do
    oc delete -f "$file" -n "$NAMESPACE" || true
  done
  rm -f sim-*.yaml
else
  echo "Usage: $0 create <count> | delete"
  exit 1
fi

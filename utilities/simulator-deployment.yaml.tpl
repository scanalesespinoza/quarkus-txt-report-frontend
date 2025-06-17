apiVersion: apps/v1
kind: Deployment
metadata:
  name: health-behavior-simulator-{{ index }}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: health-behavior-simulator-{{ index }}
  template:
    metadata:
      labels:
        app: health-behavior-simulator-{{ index }}
    spec:
      containers:
        - name: health-sim
          image: registry.access.redhat.com/ubi10/ubi-minimal
          env:
            - name: BEHAVIOR
              value: "{{ behavior }}"
          command:
            - sh
            - -c
            - |
              echo "[$(date -u)] Starting with BEHAVIOR=$BEHAVIOR"
              if [ "$BEHAVIOR" = "restart" ]; then
                sleep 60
                echo "[$(date -u)] Simulating crash to trigger restart"
                exit 1
              elif [ "$BEHAVIOR" = "notready" ]; then
                # stay running but readiness probe fails
                while true; do
                  sleep 30
                done
              else
                while true; do
                  echo "[$(date -u)] Running normally (BEHAVIOR=$BEHAVIOR)"
                  sleep 30
                done
              fi
          livenessProbe:
            exec:
              command: ["sh","-c","exit 0"]
            initialDelaySeconds: 15
            periodSeconds: 20
          readinessProbe:
            exec:
              command: ["sh","-c", "if [ \"$BEHAVIOR\" = \"notready\" ]; then exit 1; else exit 0; fi"]
            initialDelaySeconds: 5
            periodSeconds: 10

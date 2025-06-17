# generate-simulators.ps1 - create or delete health-behavior-simulator deployments
param(
  [Parameter(Mandatory)][ValidateSet("create","delete")][string]$Action,
  [int]$Count = 20
)
$namespace = "kpulse"
$templateFile = "simulator-deployment.yaml.tpl"

if ($Action -eq "create") {
  for ($i=1; $i -le $Count; $i++) {
    $rand = Get-Random -Minimum 0 -Maximum 3
    switch ($rand) {
      0 { $behavior = "ok" }
      1 { $behavior = "notready" }
      2 { $behavior = "restart" }
    }
    $yaml = "sim-$i.yaml"
    (Get-Content $templateFile) -replace "{{ index }}", "sim-$i" `
                                 -replace "{{ behavior }}", $behavior `
      | Set-Content $yaml
    oc apply -f $yaml -n $namespace
  }
} elseif ($Action -eq "delete") {
  Get-ChildItem -Filter "sim-*.yaml" | ForEach-Object {
    oc delete -f $_.FullName -n $namespace -ErrorAction SilentlyContinue
    Remove-Item $_.FullName -Force
  }
} else {
  Write-Host "Usage: .\generate-simulators.ps1 -Action create <count> | delete"
  exit 1
}

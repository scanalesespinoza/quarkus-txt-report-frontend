# generate-simulators.ps1 - create or delete health-behavior-simulator deployments
param(
  [Parameter(Mandatory)][ValidateSet("create", "delete")][string]$Action,
  [int]$Count = 20
)

$namespace = "kpulse"
$templateFile = "simulator-deployment.yaml.tpl"

if ($Action -eq "create") {
  for ($i = 1; $i -le $Count; $i++) {
    $rand = Get-Random -Minimum 0 -Maximum 3
    switch ($rand) {
      0 { $behavior = "ok" }
      1 { $behavior = "notready" }
      2 { $behavior = "restart" }
    }
    $yaml = "sim-$i.yaml"
    (Get-Content $templateFile -Raw) `
      -replace "{{ index }}", "sim-$i" `
      -replace "{{ behavior }}", $behavior `
      | Set-Content $yaml

    & oc apply -f $yaml -n $namespace
  }
}
elseif ($Action -eq "delete") {
  Get-ChildItem -Filter "sim-*.yaml" | ForEach-Object {
    try {
      & oc delete -f $_.FullName -n $namespace 2>$null
    } catch {
      Write-Warning "No se pudo eliminar $_"
    }
    Remove-Item $_.FullName -Force -ErrorAction SilentlyContinue
  }
}
else {
  Write-Host "Uso: .\generate-simulators.ps1 -Action create -Count <n> | -Action delete"
  exit 1
}

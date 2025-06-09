#!/usr/bin/env bash
#
# env_manager.sh — Gestiona la configuración de variables de entorno
# Uso:
#   ./env_manager.sh setup   # Para ingresar valores y guardarlos en "env"
#   ./env_manager.sh set     # Para imprimir export commands basados en "env"
# Ejecución:
#   eval "$(./env_manager.sh set)"   # Carga las variables en el shell actual

ENV_FILE="./env"

print_usage() {
  echo "Uso:"
  echo "  $0 setup   # Ingresar valores y guardarlos en ‘env’"
  echo "  $0 set     # Imprimir comandos export basados en ‘env’"
}

# Pregunta por un valor, ofrece un default si existe
prompt() {
  local var_name="$1"
  local current="$2"
  local prompt_text="$3"
  local input

  if [ -n "$current" ]; then
    read -p "$prompt_text [$current]: " input
    if [ -z "$input" ]; then
      input="$current"
    fi
  else
    read -p "$prompt_text: " input
  fi
  echo "$input"
}

# Modo setup: lee valores desde terminal y guarda en ENV_FILE
setup() {
  declare -A defaults
  if [ -f "$ENV_FILE" ]; then
    while IFS='=' read -r key val; do
      # Ignorar líneas que empiecen con #
      [[ "$key" =~ ^# ]] && continue
      defaults["$key"]="$val"
    done < "$ENV_FILE"
  fi

  echo "# Archivo env generado por env_manager.sh" > "$ENV_FILE"

  vars=(
    "HTTP_HOST|Bind HTTP Host (p. ej. 0.0.0.0)|${defaults[HTTP_HOST]:-0.0.0.0}"
    "HTTP_PORT|Puerto HTTP|${defaults[HTTP_PORT]:-8080}"
    "MAX_REPORTS|Máximo de reportes en cache|${defaults[MAX_REPORTS]:-16000}"
    "QUARKUS_LOG_LEVEL|Nivel de log (INFO/DEBUG/ERROR)|${defaults[QUARKUS_LOG_LEVEL]:-INFO}"
    "QUARKUS_CONTAINER_IMAGE_BUILDER|Builder de contenedor (jib/docker/podman)|${defaults[QUARKUS_CONTAINER_IMAGE_BUILDER]:-podman}"
    "QUARKUS_CONTAINER_IMAGE_BUILD|Habilitar construcción de imagen (true/false)|${defaults[QUARKUS_CONTAINER_IMAGE_BUILD]:-true}"
    "QUARKUS_CONTAINER_IMAGE_PUSH|Habilitar push de imagen (true/false)|${defaults[QUARKUS_CONTAINER_IMAGE_PUSH]:-true}"
    "QUARKUS_CONTAINER_IMAGE_REGISTRY|Registry de contenedor|${defaults[QUARKUS_CONTAINER_IMAGE_REGISTRY]:-quay.io}"
    "QUARKUS_CONTAINER_IMAGE_GROUP|Grupo en registry|${defaults[QUARKUS_CONTAINER_IMAGE_GROUP]:-sergio_canales_e}"
    "QUARKUS_CONTAINER_IMAGE_NAME|Nombre de la imagen|${defaults[QUARKUS_CONTAINER_IMAGE_NAME]:-txt-report-frontend}"
    "QUARKUS_CONTAINER_IMAGE_TAG|Tag de la imagen|${defaults[QUARKUS_CONTAINER_IMAGE_TAG]:-1.0.0}"
    "QUARKUS_JIB_JVM_ADDITIONAL_ARGUMENTS|Args JVM adicionales para Jib (p. ej. debug)|${defaults[QUARKUS_JIB_JVM_ADDITIONAL_ARGUMENTS]:-}"
    "PLATFORM_QUARKUS_NATIVE_BUILDER_IMAGE|Builder image Mandrel|${defaults[PLATFORM_QUARKUS_NATIVE_BUILDER_IMAGE]:-quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21}"
    "QUARKUS_NATIVE_CONTAINER_BUILD|Habilitar native container build (true/false)|${defaults[QUARKUS_NATIVE_CONTAINER_BUILD]:-true}"
    "QUARKUS_HTTP_ENCODING_ENABLED|Forzar HTTP UTF-8 (true/false)|${defaults[QUARKUS_HTTP_ENCODING_ENABLED]:-true}"
    "QUARKUS_HTTP_ENCODING_CHARSET|Charset HTTP|${defaults[QUARKUS_HTTP_ENCODING_CHARSET]:-UTF-8}"
  )

  for entry in "${vars[@]}"; do
    IFS='|' read -r var_name desc default_val <<< "$entry"
    new_val=$(prompt "$var_name" "$default_val" "$desc")
    echo "$var_name=$new_val" >> "$ENV_FILE"
  done

  echo "Archivo ‘$ENV_FILE’ generado con las variables:"
  nl -w2 -ba "$ENV_FILE"
}

# Modo set: imprime comandos export basados en ENV_FILE
set_vars() {
  if [ ! -f "$ENV_FILE" ]; then
    echo "Error: No existe el archivo '$ENV_FILE'. Ejecuta '$0 setup' primero." >&2
    exit 1
  fi

  while IFS='=' read -r key val; do
    [[ "$key" =~ ^# ]] && continue
    [[ -z "$key" ]] && continue
    echo "export $key=\"${val}\""
  done < "$ENV_FILE"
}

if [ $# -ne 1 ]; then
  print_usage
  exit 1
fi

case "$1" in
  setup)
    setup
    ;;
  set)
    set_vars
    ;;
  *)
    print_usage
    exit 1
    ;;
esac

#!/bin/bash

set -e

if [ -z "$1" ]; then
  echo "❌ Uso: $0 <tag>"
  echo "Ejemplo: $0 v1.2.3"
  exit 1
fi

TAG="$1"
VERSION="${TAG#v}"  # elimina la 'v' si existe
ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

# Validar formato semántico: vX.Y.Z
if [[ ! "$TAG" =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "❌ Formato de tag inválido: $TAG"
  echo "✔️ Debe ser del tipo: v1.2.3"
  exit 1
fi

POM_FILE="$ROOT_DIR/pom.xml"
APP_PROPS="$ROOT_DIR/src/main/resources/application.properties"

echo "🔧 Actualizando a versión $VERSION..."
echo "📁 Directorio raíz: $ROOT_DIR"

# 1. Actualizar <version> en pom.xml (solo la primera aparición)
echo "🔄 Actualizando pom.xml..."
sed -i "0,/<version>.*<\/version>/s|<version>.*</version>|<version>$VERSION</version>|" "$POM_FILE"

# 2. Actualizar valor por defecto en quarkus.container-image.tag
# Usar sed para reemplazar el valor por defecto de la propiedad
# Ruta del archivo a modificar
sed -i -E "s|^([[:space:]]*quarkus\.container-image\.tag[[:space:]]*=[[:space:]]*\$\{QUARKUS_CONTAINER_IMAGE_TAG:)([^}]+)(\}.*)$|\1${VERSION}\3|" "$APP_PROPS"

echo "Actualizado quarkus.container-image.tag a la versión $VERSION en $APP_PROPS"

# 3. Mostrar cambios
echo "📄 Cambios realizados:"
git -C "$ROOT_DIR" status --short "$POM_FILE" "$APP_PROPS" || true

# 4. Commit opcional
read -p "¿Deseas hacer commit de los cambios? (s/N): " confirm
if [[ "$confirm" =~ ^[Ss]$ ]]; then
  git -C "$ROOT_DIR" add "$POM_FILE" "$APP_PROPS"
  git -C "$ROOT_DIR" commit -m "chore: update version to $TAG"
  echo "✅ Commit realizado."
fi

# 5. Tag opcional
read -p "¿Deseas crear y pushear el tag $TAG en Git? (s/N): " tag_confirm
if [[ "$tag_confirm" =~ ^[Ss]$ ]]; then
  git -C "$ROOT_DIR" tag "$TAG"
  git -C "$ROOT_DIR" push origin "$TAG"
  echo "🏷️ Tag $TAG creado y enviado a remoto."
fi

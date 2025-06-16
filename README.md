## Useful commands
Run local

´´´bash
quarkus dev
´´´
Native build and image build and publish

´´´bash
PS C:\Users\sergi\git\quarkus-txt-report-frontend> .\mvnw clean package `
>>   '-Dquarkus.package.type=native' `
>>   '-Dquarkus.native.container-build=true' `
>>   '-Dquarkus.native.container-runtime=podman' `
>>   '-Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-21' `
>>   '-Dquarkus.container-image.builder=podman' `
>>   '-Dquarkus.container-image.build=true' `
>>   '-Dquarkus.container-image.push=true' `
>>   '-Dquarkus.container-image.registry=quay.io' `
>>   '-Dquarkus.container-image.group=sergio_canales_e/quarkus' `
>>   '-Dquarkus.container-image.name=txt-report-frontend' `
>>   '-Dquarkus.container-image.tag=1.0.1' `
´´´

Remember to start your podman

´´´bash
podman machine start
´´´

Run image locally with podman

´´´bash
podman run --rm -it \
  -p 8080:8080 \
  -e QUARKUS_HTTP_HOST=0.0.0.0 \
  -e TEAMS_WEBHOOK_URL="https://<tu-webhook>" \
  quay.io/sergio_canales_e/txt-report-frontend:1.0.1
´´´
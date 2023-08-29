# Spring Cloud Gateway

- Initialize the project at [start.spring.io](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.1.3&packaging=jar&jvmVersion=17&groupId=dashaun&artifactId=io.springkubed.service.gateway&name=&description=&packageName=io.springkubed.service.gateway&dependencies=cloud-gateway,actuator,native,prometheus,zipkin,testcontainers,cloud-starter-vault-config,webflux)

Optionally you can use the [initializr-plusplus](https://github.com/dashaun/initializr-plusplus/) CLI to update the project:

- initializr-plusplus extension
- initializr-plusplus project-version
- initializr-plusplus project-description
- initializr-plusplus project-name
- initializr-plusplus multi-arch-builder

By having consistent conventions across projects, you have enable some interesting automation possibilities.

## What do you normally do first?

```bash
./mvnw clean package
./mvnw spring-boot:run
./mvnw spring-boot:test-run
```
> `spring-boot:test-run` is new since Spring Boot 3.1.0

## Testcontainers

The `spring-initializr` already setup `zipkin` for us to use with Testcontainers.

Because we added `spring-cloud-vault` the application will be looking for a vault server as well.  It will not run without one.

```java
@Bean
public GenericContainer vaultContainer(DynamicPropertyRegistry registry) {
    GenericContainer container = new GenericContainer("hashicorp/vault:latest")
    .withExposedPorts(8200);
    registry.add("spring.cloud.vault.host", container::getHost);
    registry.add("spring.cloud.vault.port", container::getFirstMappedPort);
    registry.add("spring.cloud.vault.scheme", () -> "http");
    registry.add("spring.cloud.vault.token", () -> "tc-token");
    return container;
}
```
> Add this to the `TestApplication` class

## spring-boot:test-run

You only need Docker installed to run this locally.  Spring Boot is not downloading and configuring the Testcontainers for you.

```bash
./mvnw spring-boot:test-run
```
> This runs, but its not doing much at the moment

You can validate that the application is running by hitting the `/actuator/health` endpoint.

```bash
http :8080/actuator/health
```

## Build an Image

```bash
./mvnw spring-boot:build-image
```

## Push to registry

```bash
docker push dashaun/io.springkubed.service.gateway:v0.0.0-x86_64
docker tag dashaun/io.springkubed.service.gateway:v0.0.0-x86_64 dashaun/io.springkubed.service.gateway:latest
docker push dashaun/io.springkubed.service.gateway:latest
```

## Deploy to Kubernetes

```bash
kn service create gateway-service --image dashaun/io.springkubed.service.gateway:latest --port 8080 -n springkubed-io
```
package dashaun.io.springkubed.service.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestApplication {

	@Bean
	@ServiceConnection(name = "openzipkin/zipkin")
	GenericContainer<?> zipkinContainer() {
		return new GenericContainer<>(DockerImageName.parse("openzipkin/zipkin:latest")).withExposedPorts(9411);
	}

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
	
	
	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestApplication.class).run(args);
	}

}

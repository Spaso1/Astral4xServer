package org.ast.astral4xclient;

import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.ast.astral4xclient.service.FrpService.killPlainProcess;

@SpringBootApplication
@EnableScheduling
public class Astral4xClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(Astral4xClientApplication.class, args);
	}
	@PreDestroy
	public void destroy() throws Exception {
		killPlainProcess();
	}
}

package org.ast.astral4xclient;

import jakarta.annotation.PreDestroy;
import org.ast.astral4xclient.service.FrpService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

import static org.ast.astral4xclient.service.FrpService.killPlainProcess;
import static org.ast.astral4xclient.service.FrpService.process;

@SpringBootApplication
@EnableScheduling
public class Astral4xClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(Astral4xClientApplication.class, args);
	}
	@PreDestroy
	public void destroy() throws Exception {
		killPlainProcess();
		FrpService.flag = false;
		System.out.println("destroy");
	}
}

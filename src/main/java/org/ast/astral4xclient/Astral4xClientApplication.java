package org.ast.astral4xclient;

import jakarta.annotation.PreDestroy;
import org.ast.astral4xclient.service.FrpService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.*;

import static org.ast.astral4xclient.service.FrpService.killPlainProcess;
import static org.ast.astral4xclient.service.FrpService.process;

@SpringBootApplication
@EnableScheduling
public class Astral4xClientApplication {
	public static String host_web;
	public static int port_web;
	public static void main(String[] args) {
		Map<String,String> list = new HashMap<>();
		for(int i=0;i<args.length;i++){
			list.put(args[i].split("=")[0],args[i].split("=")[1]);
		}
		host_web=list.get("host_web");
		port_web=Integer.parseInt(list.get("port_web"));
		SpringApplication.run(Astral4xClientApplication.class, args);
	}
	@PreDestroy
	public void destroy() throws Exception {
		killPlainProcess();
		FrpService.flag = false;
		System.out.println("destroy");
	}
}

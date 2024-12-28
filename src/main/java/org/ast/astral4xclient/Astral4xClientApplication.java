package org.ast.astral4xclient;

import jakarta.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
	private static final Logger logger = LogManager.getLogger(Astral4xClientApplication.class);
	public static void main(String[] args) {
		Map<String,String> list = new HashMap<>();
		for(int i=0;i<args.length;i++){
			list.put(args[i].split("=")[0],args[i].split("=")[1]);
		}
		if(list.containsKey("host_web")) {
			String h = list.get("host_web");
			if(h.contains("http://")) {
				host_web=h;
			}
			else if(h.contains("https://")) {
				host_web=h;
			}else {
				host_web="http://"+h;
			}
		} else {
			host_web="http://127.0.0.1";
		}
		if(list.containsKey("port_web")) {
			port_web=Integer.parseInt(list.get("port_web"));
		} else {
			port_web = 8070;
		}
		logger.info("USE host_web:"+host_web+",port_web:"+port_web);
		SpringApplication.run(Astral4xClientApplication.class, args);
	}
	@PreDestroy
	public void destroy() throws Exception {
		killPlainProcess();
		FrpService.flag = false;
		System.out.println("destroy");
	}
}

package org.ast.astral4xclient.frp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class ComponentFRP implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        //executeExternalExe();
    }

    private void executeExternalExe() {
        ProcessBuilder processBuilder = new ProcessBuilder("path/to/your/executable.exe");

        try {
            Process process = processBuilder.start();

            // 读取进程的输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待进程结束
            int exitCode = process.waitFor();
            System.out.println("Process exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

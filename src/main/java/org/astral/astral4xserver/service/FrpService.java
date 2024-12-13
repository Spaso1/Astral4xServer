package org.astral.astral4xserver.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class FrpService {
    public void startFrps() {
        executeCommand(".//a4xs//start-frps.sh");
    }
    public void startFrpsWin() {
        executeCommand(".//a4xs//start.bat");
    }
    public void stopFrps() {
        executeCommand(".//a4xs//stop-frps.sh");
    }
    private void executeCommand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);

        try {
            Process process = processBuilder.start();
            // 读取命令输出
            readStream(process.getInputStream());
            readStream(process.getErrorStream());
            // 等待进程结束
            int exitCode = process.waitFor();
            System.out.println("Command exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void readStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}

package org.astral.astral4xserver.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class FrpService {

    private List<Process> processes = new ArrayList<>();

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
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        try {
            Process process = processBuilder.start();
            processes.add(process);
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

    public void killAllProcesses() {
        String sys = System.getProperty("os.name");
        if (sys.contains("Windows")) {
            terminateProcessByPortWin(7000);
        } else {
            terminateProcessByPortLinux(7000);
        }
    }
    public void terminateProcessByPortWin(int port) {
        try {
            // 获取监听指定端口的进程ID
            String netstatCmd = "netstat -ano | findstr :" + port;
            Process netstatProcess = Runtime.getRuntime().exec(netstatCmd);
            InputStream inputStream = netstatProcess.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // 提取 PID
                String[] parts = line.split("\\s+");
                if (parts.length > 0) {
                    String pid = parts[parts.length - 1];
                    // 使用 taskkill 终止进程
                    String taskkillCmd = "taskkill /PID " + pid + " /F";
                    Process taskkillProcess = Runtime.getRuntime().exec(taskkillCmd);
                    taskkillProcess.waitFor();
                    System.out.println("Terminated process with PID: " + pid);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void terminateProcessByPortLinux(int port) {
        try {
            // 获取监听指定端口的进程ID
            String netstatCmd = "netstat -apn | grep " + port;
            Process netstatProcess = Runtime.getRuntime().exec(netstatCmd);
            InputStream inputStream = netstatProcess.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // 提取 PID
                String[] parts = line.split("\\s+");
                if (parts.length > 0) {
                    String pid = parts[parts.length - 1].split("//")[0];
                    // 使用 taskkill 终止进程
                    String taskkillCmd = "kill" + pid;
                    Process taskkillProcess = Runtime.getRuntime().exec(taskkillCmd);
                    taskkillProcess.waitFor();
                    System.out.println("Terminated process with PID: " + pid);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

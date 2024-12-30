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
        String sys = System.getProperty("os.name");
        if (sys.contains("Windows")) {
            startFrpsWin();
        } else {
            executeCommand(".//a4xs//start-frps.sh");
        }
    }

    public void startFrpsWin() {
        executeCommand(".//a4xs//start.bat");
    }

    public void stopFrps() {
        killAllProcesses();
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
        System.out.println("Kill all processes");
        if (sys.contains("Windows")) {
            terminateProcessByPortWin(7000);
        } else {
            terminateProcessByPortLinux(7000);
        }
    }
    public void terminateProcessByPortWin(int port) {
        System.out.println("Killing process on port " + port);
        try {
            ProcessBuilder netstatBuilder = new ProcessBuilder("cmd.exe", "/c", "netstat -ano | findstr :" + port);
            Process netstatProcess = netstatBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(netstatProcess.getInputStream()));
            String line;
            System.out.println("Executing command: " + netstatBuilder.command());
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                // 提取 PID
                String[] parts = line.trim().split("\\s+");
                if (parts.length > 4) {
                    String pid = parts[4];
                    // 使用 taskkill 终止进程
                    ProcessBuilder taskkillBuilder = new ProcessBuilder("taskkill", "/PID", pid, "/F");
                    Process taskkillProcess = taskkillBuilder.start();
                    // 读取 taskkill 输出
                    BufferedReader taskkillReader = new BufferedReader(new InputStreamReader(taskkillProcess.getInputStream()));
                    String taskkillLine;
                    while ((taskkillLine = taskkillReader.readLine()) != null) {
                        System.out.println(taskkillLine);
                    }
                    // 等待 taskkill 执行完成
                    int exitCode = taskkillProcess.waitFor();
                    if (exitCode == 0) {
                        System.out.println("Terminated process with PID: " + pid);
                    } else {
                        System.err.println("Failed to terminate process with PID: " + pid);
                    }
                }
            }

            // 等待 netstat 命令执行完成
            int netstatExitCode = netstatProcess.waitFor();
            if (netstatExitCode != 0) {
                System.err.println("netstat command failed with exit code: " + netstatExitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void terminateProcessByPortLinux(int port) {
        System.out.println("Killing process on port " + port);
        try {
            // 使用 ProcessBuilder 执行 netstat 命令
            ProcessBuilder netstatBuilder = new ProcessBuilder("sh", "-c", "netstat -apn | grep " + port);
            Process netstatProcess = netstatBuilder.start();

            // 读取 netstat 输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(netstatProcess.getInputStream()));
            String line;
            System.out.println("Executing command: " + netstatBuilder.command());

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                // 提取 PID
                String[] parts = line.trim().split("\\s+");
                if (parts.length > 6) {
                    String pid = parts[6].split("/")[0];

                    // 使用 kill 终止进程
                    ProcessBuilder killBuilder = new ProcessBuilder("kill", "-9", pid);
                    Process killProcess = killBuilder.start();

                    // 读取 kill 输出
                    BufferedReader killReader = new BufferedReader(new InputStreamReader(killProcess.getInputStream()));
                    String killLine;
                    while ((killLine = killReader.readLine()) != null) {
                        System.out.println(killLine);
                    }

                    // 等待 kill 执行完成
                    int exitCode = killProcess.waitFor();
                    if (exitCode == 0) {
                        System.out.println("Terminated process with PID: " + pid);
                    } else {
                        System.err.println("Failed to terminate process with PID: " + pid);
                    }
                }
            }

            // 等待 netstat 命令执行完成
            int netstatExitCode = netstatProcess.waitFor();
            if (netstatExitCode != 0) {
                System.err.println("netstat command failed with exit code: " + netstatExitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

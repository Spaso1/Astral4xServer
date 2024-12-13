package org.astral.astral4xserver.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class FireWall {
    public static void openPort(int port) {
        executeIptablesCommand("-A", port);
    }
    public static void closePort(int port) {
        executeIptablesCommand("-D", port);
    }
    public static void openPorts(List<Integer> ports) {
        for (int port : ports) {
            openPort(port);
        }
    }
    public static void closePorts(List<Integer> ports) {
        for (int port : ports) {
            closePort(port);
        }
    }
    private static void executeIptablesCommand(String action, int port) {
        try {
            // 构建 iptables 命令
            String[] command = {"sudo", "iptables", action, "INPUT", "-p", "tcp", "--dport", String.valueOf(port), "-j", "ACCEPT"};

            // 执行命令
            Process process = new ProcessBuilder(command).start();

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 等待命令执行完成
            int exitCode = process.waitFor();
            System.out.println("Command exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        List<Integer> portsToOpen = List.of(8080, 8081, 8082);
        List<Integer> portsToClose = List.of(8080, 8081, 8082);

        openPorts(portsToOpen);  // 例如，批量开放 8080, 8081, 8082 端口
        closePorts(portsToClose); // 例如，批量关闭 8080, 8081, 8082 端口
    }
}

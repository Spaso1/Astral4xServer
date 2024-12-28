package org.astral.astral4xserver.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class FireWall {
    private static final Logger logger = LogManager.getLogger(FireWall.class);

    public static void openPort(int port) {
        if (isWindows()) {
            executeNetshCommand("add", port);
        } else if (isUsingFirewalld()) {
            executeFirewallCmdCommand("--add-port", port, "--permanent");
        } else {
            executeIptablesCommand("-A", port);
        }
    }

    public static void closePort(int port) {
        if (isWindows()) {
            executeNetshCommand("delete", port);
        } else if (isUsingFirewalld()) {
            executeFirewallCmdCommand("--remove-port", port, "--permanent");
        } else {
            executeIptablesCommand("-D", port);
        }
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
            String[] command = {"sudo", "iptables", action, "INPUT", "-p", "tcp", "--dport", String.valueOf(port), "-j", "ACCEPT"};
            Process process = new ProcessBuilder(command).start();
            logProcessOutput(process);
            int exitCode = process.waitFor();
            logger.info("iptables command exited with code {}", exitCode);
        } catch (IOException | InterruptedException e) {
            logger.error("Error executing iptables command", e);
        }
    }

    private static void executeFirewallCmdCommand(String action, int port, String permanent) {
        try {
            String[] command = {"sudo", "firewall-cmd", action, port + "/tcp", permanent};
            Process process = new ProcessBuilder(command).start();
            logProcessOutput(process);
            int exitCode = process.waitFor();
            logger.info("firewall-cmd command exited with code {}", exitCode);

            // 重新加载 firewalld 配置
            String[] reloadCommand = {"sudo", "firewall-cmd", "--reload"};
            process = new ProcessBuilder(reloadCommand).start();
            logProcessOutput(process);
            exitCode = process.waitFor();
            logger.info("firewall-cmd reload command exited with code {}", exitCode);
        } catch (IOException | InterruptedException e) {
            logger.error("Error executing firewall-cmd command", e);
        }
    }

    private static void executeNetshCommand(String action, int port) {
        try {
            if (action.equals("delete")) {
                String[] command = {"netsh", "advfirewall", "firewall", action, "rule", "name=Port" + port};
                new ProcessBuilder(command).start();
                return;
            }
            String[] command = {"netsh", "advfirewall", "firewall", action, "rule", "name=Port" + port, "dir=in", "action=allow", "protocol=TCP", "localport=" + port};
            Process process = new ProcessBuilder(command).start();
            logProcessOutput(process);
            int exitCode = process.waitFor();
            logger.info("netsh command exited with code {}", exitCode);
        } catch (IOException | InterruptedException e) {
            logger.error("Error executing netsh command", e);
        }
    }

    private static void logProcessOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            logger.info("Command output: {}", line);
        }
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while ((line = errorReader.readLine()) != null) {
            logger.error("Command error: {}", line);
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private static boolean isUsingFirewalld() {
        try {
            String[] command = {"firewall-cmd", "--state"};
            Process process = new ProcessBuilder(command).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            return "running".equalsIgnoreCase(line);
        } catch (IOException e) {
            logger.error("Error checking firewall-cmd state", e);
            return false;
        }
    }

    public static void main(String[] args) {
        List<Integer> portsToOpen = List.of(8080, 8081, 8082);
        List<Integer> portsToClose = List.of(8080, 8081, 8082);

        openPorts(portsToOpen);  // 例如，批量开放 8080, 8081, 8082 端口
        closePorts(portsToClose); // 例如，批量关闭 8080, 8081, 8082 端口
    }
}

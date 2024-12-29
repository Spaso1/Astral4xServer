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
            setOutboundRateLimit(port, "5mbit");
            executeFirewallCmdCommand("--add-port", port, "--permanent");
        } else {
            setOutboundRateLimit(port, "5mbit");
            executeIptablesCommand("-A", port);
        }
    }

    public static void closePort(int port) {
        if (isWindows()) {
            executeNetshCommand("delete", port);
        } else if (isUsingFirewalld()) {
            executeFirewallCmdCommand("--remove-port", port, "--permanent");
            removeOutboundRateLimit(port);
        } else {
            executeIptablesCommand("-D", port);
            removeOutboundRateLimit(port);
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

    public static void setOutboundRateLimit(int port, String rate) {
        try {
            // 创建一个 tc qdisc (queuing discipline) 来限制流量
            String[] command1 = {"sudo", "tc", "qdisc", "add", "dev", "eth0", "root", "handle", "1:", "htb", "default", "10"};
            Process process1 = new ProcessBuilder(command1).start();
            logProcessOutput(process1);
            int exitCode1 = process1.waitFor();
            logger.info("tc qdisc command exited with code {}", exitCode1);

            // 创建一个 tc class 来设置具体的速率限制
            String[] command2 = {"sudo", "tc", "class", "add", "dev", "eth0", "parent", "1:", "classid", "1:10", "htb", "rate", rate};
            Process process2 = new ProcessBuilder(command2).start();
            logProcessOutput(process2);
            int exitCode2 = process2.waitFor();
            logger.info("tc class command exited with code {}", exitCode2);

            // 创建一个 tc filter 来将流量限制应用到特定的端口
            String[] command3 = {"sudo", "tc", "filter", "add", "dev", "eth0", "protocol", "ip", "parent", "1:0", "prio", "1", "u32", "match", "ip", "sport", String.valueOf(port), "0xffff", "flowid", "1:10"};
            Process process3 = new ProcessBuilder(command3).start();
            logProcessOutput(process3);
            int exitCode3 = process3.waitFor();
            logger.info("tc filter command exited with code {}", exitCode3);
        } catch (IOException | InterruptedException e) {
            logger.error("Error setting outbound rate limit", e);
        }
    }

    public static void removeOutboundRateLimit(int port) {
        try {
            // 删除 tc filter
            String[] command1 = {"sudo", "tc", "filter", "del", "dev", "eth0", "protocol", "ip", "parent", "1:0", "prio", "1", "u32", "match", "ip", "sport", String.valueOf(port), "0xffff", "flowid", "1:10"};
            Process process1 = new ProcessBuilder(command1).start();
            logProcessOutput(process1);
            int exitCode1 = process1.waitFor();
            logger.info("tc filter del command exited with code {}", exitCode1);

            // 删除 tc class
            String[] command2 = {"sudo", "tc", "class", "del", "dev", "eth0", "classid", "1:10"};
            Process process2 = new ProcessBuilder(command2).start();
            logProcessOutput(process2);
            int exitCode2 = process2.waitFor();
            logger.info("tc class del command exited with code {}", exitCode2);

            // 删除 tc qdisc
            String[] command3 = {"sudo", "tc", "qdisc", "del", "dev", "eth0", "root"};
            Process process3 = new ProcessBuilder(command3).start();
            logProcessOutput(process3);
            int exitCode3 = process3.waitFor();
            logger.info("tc qdisc del command exited with code {}", exitCode3);
        } catch (IOException | InterruptedException e) {
            logger.error("Error removing outbound rate limit", e);
        }
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

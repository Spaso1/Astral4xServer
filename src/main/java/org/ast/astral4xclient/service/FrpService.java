package org.ast.astral4xclient.service;

import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import org.ast.astral4xclient.controller.ApiClient;
import org.ast.astral4xclient.message.FrpMessage;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FrpService implements Runnable {
    private ArrayList<String> commandList = new ArrayList<>();
    public static Process process;
    public static int status = 100;
    public static boolean flag = true;

    public void run() {
        updateJSON();
        try {
            executeExternalExe();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeExternalExe() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(".//a4xs//start.bat");
        System.out.println("Process start");
        try {
            process = processBuilder.start();
            // 读取进程的输出流
            status = 200;
            System.out.println("Process start1");

            while (flag) {
                try {
                    File file = new File(".//a4xs//frpwinamd64//frpc.json");
                    if (file.exists()) {
                        file.delete();
                    }
                    file = new File(".//a4xs//frplinuxamd64//frpc.json");
                    if (file.exists()) {
                        file.delete();
                    }
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    System.err.println("Thread was interrupted, Failed to complete operation");
                    break;
                }
            }
            // 等待进程结束
            int exitCode = process.waitFor();
            System.out.println("Process exited with code " + exitCode);
        } catch (IOException e) {
            status = 400;
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted, Failed to complete operation");
        } finally {
            if (process != null) {
                killPlainProcess();
            }
            System.out.println("Process end");
        }
    }

    private void updateJSON() {
        File jsonFile = new File("./a4xs/frpwinamd64/frpc.json");
        try (PrintWriter printWriter = new PrintWriter(jsonFile)) {
            Gson gson = new Gson();
            printWriter.println(gson.toJson(ApiClient.frpJSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void killPlainProcess() throws Exception {
        try {
            String processName = "frpc.exe"; // 替换为你要终止的进程名称
            List<ProcessHandle> allProcesses = ProcessHandle.allProcesses().toList();

            for (ProcessHandle process : allProcesses) {
                if (process.info().command().map(cmd -> cmd.contains(processName)).orElse(false)) {
                    System.out.println("Found process: " + process.info().command());
                    process.destroy();
                    System.out.println("Terminated process: " + process.info().command());
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * mind here, the process used for call the kill should be clear too!!
     *
     * @param pidNum
     */
    private static void killChildProcess(long pidNum) throws Exception {
        // No need to implement this method separately for Windows
        // The taskkill command with /F flag will terminate the process and its children
    }
}

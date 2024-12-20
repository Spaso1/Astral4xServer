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
    private static Process process;
    public void run() {
        updateJSON();
        executeExternalExe();
    }

    private void executeExternalExe() {
        ProcessBuilder processBuilder = new ProcessBuilder(".//a4xs//start.bat");
        Process process = null;
        try {
            process = processBuilder.start();
            this.process = process;
            // 读取进程的输出流
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                commandList.add(line);
                System.out.println(line);
            }

            // 等待进程结束
            int exitCode = process.waitFor();
            System.out.println("Process exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
    private void updateJSON() {
        File jsonFile = new File("./a4xs/frpwinamd64/frpc.json");
        try {
            PrintWriter printWriter = new PrintWriter(jsonFile);
            Gson gson = new Gson();
            printWriter.println(gson.toJson(ApiClient.frpJSON));
            printWriter.close();
        }catch (Exception e ) {

        }
    }
    public static void killPlainProcess() throws Exception {

        Field f = process.getClass().getDeclaredField("pid");
        f.setAccessible(true);
        long pid = f.getLong(process) + 1;
        killChildProcess(pid);
        process.destroy();
    }

    /**
     * mind here, the process usd for call the kill should be clear too!!
     *
     * @param pidNum
     */
    private static void killChildProcess(long pidNum) throws Exception {
        String cmd = "kill -15 " + pidNum;
        try {
            Process killProcess = null;
            killProcess = Runtime.getRuntime().exec(cmd);
            killProcess.waitFor();
            TimeUnit.MILLISECONDS.sleep(100);
            killProcess.destroy();
        } catch (IOException | InterruptedException e) {
            throw e;
        }
    }

}

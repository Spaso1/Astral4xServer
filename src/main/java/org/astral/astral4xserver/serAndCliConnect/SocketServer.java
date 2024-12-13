package org.astral.astral4xserver.serAndCliConnect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class SocketServer implements Runnable {
    private static final Logger logger = LogManager.getLogger(SocketServer.class);

    private ServerSocket serverSocket;
    private Thread serverThread;

    @PostConstruct
    public void init() {
        try {
            serverSocket = new ServerSocket(54718); // 54718为端口号
            serverThread = new Thread(this);
            serverThread.start();
            logger.info("Socket服务器已启动，监听端口: 54718");
        } catch (IOException e) {
            logger.error("启动Socket服务器时发生错误", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                logger.info("Socket服务器已关闭");
            }
        } catch (IOException e) {
            logger.error("关闭Socket服务器时发生错误", e);
        }
    }

    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("新客户端连接: {}", clientSocket.getInetAddress());
                handleClient(clientSocket);
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    logger.error("接受客户端连接时发生错误", e);
                }
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    logger.info("收到消息: {}", inputLine);
                    out.println("Echo: " + inputLine);
                }
            } catch (IOException e) {
                logger.error("处理客户端消息时发生错误", e);
            } finally {
                try {
                    clientSocket.close();
                    logger.info("客户端断开连接: {}", clientSocket.getInetAddress());
                } catch (IOException e) {
                    logger.error("关闭客户端连接时发生错误", e);
                }
            }
        }).start();
    }
}

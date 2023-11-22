package com.maks.chess.service.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ServerSocketCallable implements Callable<Socket> {
    private ServerSocket serverSocket;
    private static final Integer ACCEPT_TIMEOUT = 30 * 1000;
    private final int port;

    public ServerSocketCallable(int port) {
        this.port = port;
    }

    @Override
    public Socket call() throws Exception {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(ACCEPT_TIMEOUT);
        return serverSocket.accept();
    }

    public void closeConnection() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}

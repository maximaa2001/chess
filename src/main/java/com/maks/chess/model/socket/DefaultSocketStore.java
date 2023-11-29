package com.maks.chess.model.socket;

import java.net.Socket;

public class DefaultSocketStore implements SocketStore {
    private static SocketStore socketStore;
    private Socket socket;

    public static SocketStore getSocketStore() {
        if (socketStore == null) {
            socketStore = new DefaultSocketStore();
        }
        return socketStore;
    }

    private DefaultSocketStore() {
    }

    @Override
    public void saveSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }
}

package com.maks.chess.model.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class DefaultSocketStore implements SocketStore {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSocketStore.class);
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
        logger.debug("saved socket");
        this.socket = socket;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }
}

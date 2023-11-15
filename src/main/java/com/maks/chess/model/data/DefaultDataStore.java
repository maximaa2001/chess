package com.maks.chess.model.data;

import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.socket.SocketStore;
import com.maks.chess.model.socket.SocketStoreFactory;

import java.net.Socket;

public class DefaultDataStore implements DataStore {
    private static DataStore dataStore;
    private final SocketStore socketStore;
    private GamerColor gamerColor;

    public static DataStore getDataStore() {
        if (dataStore == null) {
            dataStore = new DefaultDataStore();
        }
        return dataStore;
    }

    private DefaultDataStore() {
        socketStore = SocketStoreFactory.getSocketFactory();
    }

    @Override
    public void setSocket(Socket socket) {
        socketStore.saveSocket(socket);
    }

    @Override
    public Socket getSocket() {
        return socketStore.getSocket();
    }

    @Override
    public void setColor(GamerColor gamerColor) {
        this.gamerColor = gamerColor;
    }

    @Override
    public GamerColor getColor() {
        return gamerColor;
    }
}

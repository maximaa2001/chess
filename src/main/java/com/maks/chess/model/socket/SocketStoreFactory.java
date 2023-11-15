package com.maks.chess.model.socket;

public final class SocketStoreFactory {
    public static SocketStore getSocketFactory() {
        return DefaultSocketStore.getSocketStore();
    }
}

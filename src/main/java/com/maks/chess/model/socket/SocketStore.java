package com.maks.chess.model.socket;

import java.net.Socket;

public interface SocketStore {
    void saveSocket(Socket socket);
    Socket getSocket();
}

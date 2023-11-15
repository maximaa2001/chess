package com.maks.chess.model.data;

import com.maks.chess.constant.define.GamerColor;

import java.net.Socket;

public interface DataStore {
    void setSocket(Socket socket);
    Socket getSocket();
    void setColor(GamerColor gamerColor);
    GamerColor getColor();

}

package com.maks.chess.service;

import com.maks.chess.model.MoveDto;
import com.maks.chess.model.data.DataStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketWrapper {
    private static final Logger logger = LoggerFactory.getLogger(SocketWrapper.class);
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public SocketWrapper() {
        this.socket = DataStoreFactory.getDataStore().getSocket();
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            logger.error("Failed to create streams from socket");
            throw new RuntimeException();
        }
    }

    public MoveDto readMove() {
        if (!socket.isClosed() && socket.isConnected()) {
            try {
                return (MoveDto) in.readObject();
            } catch (Exception e) {
                logger.error("Failed deserialize MoveDto");
            }
        }
        return null;
    }

    public void writeMove(MoveDto moveDto) {
        try {
            out.writeObject(moveDto);
        } catch (IOException e) {
            logger.error("Failed serialize MoveDto");
        }
    }
}

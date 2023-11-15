package com.maks.chess.service.socket;

import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.data.DataStoreFactory;
import com.maks.chess.util.VoidSmth;

import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class ConnectionThread extends Thread {
    private final Future<Socket> futureSocket;

    private final Consumer<String> acceptTimeoutHandler;
    private final VoidSmth successfullyConnectionHandler;

    public ConnectionThread(Future<Socket> futureSocket,
                            Consumer<String> acceptTimeoutHandler,
                            VoidSmth successfullyConnectionHandler) {
        this.futureSocket = futureSocket;
        this.acceptTimeoutHandler = acceptTimeoutHandler;
        this.successfullyConnectionHandler = successfullyConnectionHandler;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!futureSocket.isDone()) ;
        try {
            Socket socket = futureSocket.get();
            DataStoreFactory.getDataStore().setColor(GamerColor.WHITE);
            DataStoreFactory.getDataStore().setSocket(socket);
            successfullyConnectionHandler.execute();
        } catch (InterruptedException e) {
            acceptTimeoutHandler.accept("Соединение прервано");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            Class<? extends Throwable> aClass = cause.getClass();
            if (aClass.equals(SocketTimeoutException.class)) {
                acceptTimeoutHandler.accept("Время ожидания подключения истекло");
            } else if (aClass.equals(SocketException.class)) {
                acceptTimeoutHandler.accept("Соединение прервано");
            } else {
                acceptTimeoutHandler.accept(e.getMessage());
            }
        }
    }
}

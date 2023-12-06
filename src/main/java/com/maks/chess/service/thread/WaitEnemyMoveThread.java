package com.maks.chess.service.thread;

import com.maks.chess.model.MoveDto;
import com.maks.chess.service.SocketWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class WaitEnemyMoveThread extends Thread {
    private final SocketWrapper socketWrapper;
    private final Consumer<MoveDto> moveDtoConsumer;

    public WaitEnemyMoveThread(SocketWrapper socketWrapper, Consumer<MoveDto> moveDtoHandler) {
        this.socketWrapper = socketWrapper;
        this.moveDtoConsumer = moveDtoHandler;
    }

    @Override
    public void run() {
        MoveDto moveDto = socketWrapper.readMove();
        if (moveDto != null) {
            moveDtoConsumer.accept(moveDto);
        }
    }
}

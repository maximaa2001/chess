package com.maks.chess.service.thread;

import com.maks.chess.constant.define.GameState;
import com.maks.chess.constant.define.LosingType;
import com.maks.chess.service.GameLogic;
import com.maks.chess.util.ThreadHelper;
import com.maks.chess.util.Timer;

import java.util.function.Consumer;

public class TimerThread extends Thread {
    private static final Integer SECONDS_ON_MOVE = 90;
    private int timeLeft = SECONDS_ON_MOVE;
    private boolean isStopped = false;
    private final Timer timer;
    private final GameLogic gameLogic;
    private final Consumer<LosingType> timeUpHandler;

    public TimerThread(Timer timer, GameLogic gameLogic, Consumer<LosingType> timeUpHandler) {
        this.timer = timer;
        this.gameLogic = gameLogic;
        this.timeUpHandler = timeUpHandler;
    }

    @Override
    public void run() {
        while (timeLeft > 0 && !isStopped) {
            updateTimer();
            ThreadHelper.sleep(1);
            decrementTime();
        }
        if (!isStopped && !gameLogic.getGameState().equals(GameState.WAIT_FOR_ENEMY)) {
            stopTimer();
            timeUpHandler.accept(LosingType.TIME_IS_UP);
        }
    }

    public synchronized void resetTimer() {
        timeLeft = SECONDS_ON_MOVE;
    }

    public void stopTimer() {
        isStopped = true;
    }

    private synchronized void decrementTime() {
        timeLeft--;
    }

    private void updateTimer() {
        timer.updateMessage(generateMessage());
        timer.updateTime(timeLeft);
    }

    private String generateMessage() {
        if (gameLogic.getGameState().equals(GameState.WAIT_FOR_ENEMY)) {
            return "Ходит противник";
        }
        return "Ваш ход";
    }
}

package com.maks.chess.service.game_state_logger.manager;

import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.LosingType;
import com.maks.chess.model.Coordinate;
import com.maks.chess.service.game_state_logger.GameStateLogger;
import com.maks.chess.service.game_state_logger.reverser.LogReverser;

import java.util.List;

public class DefaultGameLoggerManager implements GameLoggerManager {
    private static final String MOVE_LOG = "Вы: ходит с клетки (%s:%s) на клетку (%s:%s)";
    private static final String EAT_LOG = "Вы: съедает фигуру, походив с клетки (%s:%s) на клетку (%s:%s)";
    private static final String KING_IN_DANGER_LOG = "Вы: угрожает королю";
    private static final String PAWN_ELOVLED_LOG = "Вы: пешка дошла до конца доски и становится %s";
    private static final String CASTLING_LOG = "Вы: сделал рокировку";
    private static final String LOSING_TIME_IS_UP_LOG = "Вы: время хода истекло";
    private static final String LOSING_GIVE_UP_LOG = "Вы: сдался";
    private static final String LOSING_MAT_LOG = "Вы: поставил мат";
    private final List<GameStateLogger> gameStateLoggers;
    private final LogReverser logReverser;

    public DefaultGameLoggerManager(List<GameStateLogger> gameStateLoggers, LogReverser logReverser) {
        this.gameStateLoggers = gameStateLoggers;
        this.logReverser = logReverser;
    }

    @Override
    public String createMoveLog(Coordinate from, Coordinate to) {
        String moveLog = String.format(MOVE_LOG, from.getRow(), from.getColumn(), to.getRow(), to.getColumn());
        log(moveLog);
        return logReverser.reverseLog(moveLog);
    }

    @Override
    public String createEatLog(Coordinate from, Coordinate to) {
        String eatLog = String.format(EAT_LOG, from.getRow(), from.getColumn(), to.getRow(), to.getColumn());
        log(eatLog);
        return logReverser.reverseLog(eatLog);
    }

    @Override
    public String createKingInDangerLog() {
        log(KING_IN_DANGER_LOG);
        return logReverser.reverseLog(KING_IN_DANGER_LOG);
    }

    @Override
    public String createPawnEvolvedLog(FigureType type) {
        String pawnEvolvedLog = String.format(PAWN_ELOVLED_LOG, type);
        log(pawnEvolvedLog);
        return logReverser.reverseLog(pawnEvolvedLog);
    }

    @Override
    public String createCastlingLog() {
        log(CASTLING_LOG);
        return logReverser.reverseLog(CASTLING_LOG);
    }

    @Override
    public String createLosingLog(LosingType type) {
        String losingLog = switch (type) {
            case TIME_IS_UP -> LOSING_TIME_IS_UP_LOG;
            case GIVE_UP -> LOSING_GIVE_UP_LOG;
            case MAT -> LOSING_MAT_LOG;
        };
        log(losingLog);
        return logReverser.reverseLog(losingLog);
    }

    @Override
    public void log(String log) {
        gameStateLoggers.forEach(e -> e.log(log));
    }
}

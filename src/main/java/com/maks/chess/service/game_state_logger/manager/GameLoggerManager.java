package com.maks.chess.service.game_state_logger.manager;

import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.LosingType;
import com.maks.chess.model.Coordinate;

public interface GameLoggerManager {
    String createMoveLog(Coordinate from, Coordinate to);
    String createEatLog(Coordinate from, Coordinate to);
    String createKingInDangerLog();
    String createPawnEvolvedLog(FigureType type);
    String createCastlingLog();
    String createLosingLog(LosingType type);
    void log(String log);
}

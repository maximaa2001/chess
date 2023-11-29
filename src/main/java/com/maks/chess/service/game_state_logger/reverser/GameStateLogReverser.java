package com.maks.chess.service.game_state_logger.reverser;

import com.maks.chess.model.Coordinate;
import com.maks.chess.service.transformer.CoordinateTransformer;
import com.maks.chess.service.transformer.ToEnemyCoordinateTransformer;

public class GameStateLogReverser implements LogReverser {
    private static final CoordinateTransformer coordinateTransformer = new ToEnemyCoordinateTransformer();

    @Override
    public String reverseLog(String log) {
        log = log.replaceFirst("Вы", "Противник");
        for (int i = 0; i < log.length(); i++) {
            char symbol = log.charAt(i);
            if (symbol == '(') {
                String substring = log.substring(i + 1, i + 4);
                log = log.replaceFirst(substring, replaceCoordinate(substring));
            }
        }
        return log;
    }

    private String replaceCoordinate(String substring) {
        String[] split = substring.split(":");
        Coordinate newCoordinate = coordinateTransformer.transform(new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
        return String.format("%s:%s", newCoordinate.getRow(), newCoordinate.getColumn());
    }
}

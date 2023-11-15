package com.maks.chess.model;

import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.figure.Figure;
import com.maks.chess.service.initializer.FigureInitializer;
import com.maks.chess.service.initializer.StartPositionFigureInitializer;
import com.maks.chess.service.transformer.CoordinateTransformer;
import com.maks.chess.service.transformer.ToEnemyCoordinateTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessModel {
    private static final Figure[][] ALL_FIGURES = new Figure[AppConstant.BOARD_SIDE_SIZE][AppConstant.BOARD_SIDE_SIZE];
    private final CoordinateTransformer coordinateTransformer;

    public ChessModel(GamerColor myColor, GamerColor enemyColor) {
        this(myColor, enemyColor, new StartPositionFigureInitializer(), new ToEnemyCoordinateTransformer());
    }

    public ChessModel(GamerColor myColor, GamerColor enemyColor, FigureInitializer startFigureInitializer, CoordinateTransformer coordinateTransformer) {
        this.coordinateTransformer = coordinateTransformer;
        List<Figure> myFigures = startFigureInitializer.createFigures(myColor);
        List<Figure> enemyFigures = startFigureInitializer.createFigures(enemyColor);
        initFigures(myFigures, enemyFigures);
    }

    public Map<Figure, Coordinate> getRemainingFigures() {
        Map<Figure, Coordinate> map = new HashMap<>();
        for (int i = 0; i < ALL_FIGURES.length; i++) {
            for (int j = 0; j < ALL_FIGURES[i].length; j++) {
                if (ALL_FIGURES[i][j] != null) {
                    map.put(ALL_FIGURES[i][j], new Coordinate(i, j));
                }
            }
        }
        return map;
    }

    private void initFigures(List<Figure> myFigures, List<Figure> enemyFigures) {
        for (Figure figure : myFigures) {
            Coordinate coordinate = figure.getStartCoordinate();
            ALL_FIGURES[coordinate.getRow()][coordinate.getColumn()] = figure;
        }
        for (Figure figure : enemyFigures) {
            Coordinate transformedCoordinate = coordinateTransformer.transform(figure.getStartCoordinate());
            ALL_FIGURES[transformedCoordinate.getRow()][transformedCoordinate.getColumn()] = figure;
        }
    }
}

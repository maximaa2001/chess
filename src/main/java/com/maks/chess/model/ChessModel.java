package com.maks.chess.model;

import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.figure.Figure;
import com.maks.chess.service.initializer.FigureInitializer;
import com.maks.chess.service.initializer.StartPositionFigureInitializer;
import com.maks.chess.service.transformer.CoordinateTransformer;
import com.maks.chess.service.transformer.ToEnemyCoordinateTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChessModel {
    private static final Logger logger = LoggerFactory.getLogger(ChessModel.class);
    private final Figure[][] ALL_FIGURES = new Figure[AppConstant.BOARD_SIDE_SIZE][AppConstant.BOARD_SIDE_SIZE];
    private final CoordinateTransformer coordinateTransformer;

    public ChessModel() {
        this.coordinateTransformer = new ToEnemyCoordinateTransformer();
    }

    public ChessModel(GamerColor myColor, GamerColor enemyColor) {
        this(myColor, enemyColor, new StartPositionFigureInitializer(), new ToEnemyCoordinateTransformer());
    }

    public ChessModel(GamerColor myColor, GamerColor enemyColor, FigureInitializer startFigureInitializer, CoordinateTransformer coordinateTransformer) {
        this.coordinateTransformer = coordinateTransformer;
        List<Figure> myFigures = startFigureInitializer.createFigures(myColor);
        List<Figure> enemyFigures = startFigureInitializer.createFigures(enemyColor);
        initFigures(myFigures, enemyFigures);
    }

    public Figure move(Coordinate from, Coordinate to) {
        Figure figure = ALL_FIGURES[from.getRow()][from.getColumn()];
        logger.info("move {} from {} to {}", figure, from, to);
        if (figure != null) {
            ALL_FIGURES[to.getRow()][to.getColumn()] = figure;
            ALL_FIGURES[from.getRow()][from.getColumn()] = null;
        }
        return figure;
    }

    public Figure getByCoordinate(Coordinate coordinate) {
        return ALL_FIGURES[coordinate.getRow()][coordinate.getColumn()];
    }

    public Map<Coordinate, Figure> getRemainingFigures() {
        Map<Coordinate, Figure> map = new HashMap<>();
        for (int i = 0; i < ALL_FIGURES.length; i++) {
            for (int j = 0; j < ALL_FIGURES[i].length; j++) {
                if (ALL_FIGURES[i][j] != null) {
                    map.put(new Coordinate(i, j), ALL_FIGURES[i][j]);
                }
            }
        }
        return map;
    }

    public void addFigure(Figure figure, Coordinate coordinate) {
        ALL_FIGURES[coordinate.getRow()][coordinate.getColumn()] = figure;
    }

    public ChessModel reverseModel() {
        ChessModel reversedChessModel = new ChessModel();
        for (int i = 0; i < ALL_FIGURES.length; i++) {
            for (int j = 0; j < ALL_FIGURES[i].length; j++) {
                reverseFigure(reversedChessModel, i, j);
            }
        }
        return reversedChessModel;
    }

    public CoordinateTransformer getCoordinateTransformer() {
        return coordinateTransformer;
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

    private void reverseFigure(ChessModel reversedModel, int row, int column) {
        Figure figure = ALL_FIGURES[row][column];
        if (figure != null) {
            Coordinate coordinate = new Coordinate(row, column);
            Coordinate transformedCoordinate = coordinateTransformer.transform(coordinate);
            reversedModel.addFigure(figure, transformedCoordinate);
        }
    }
}

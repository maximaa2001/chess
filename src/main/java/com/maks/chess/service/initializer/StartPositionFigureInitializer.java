package com.maks.chess.service.initializer;

import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.figure.Figure;
import com.maks.chess.service.factory.figure.FigureFactory;
import com.maks.chess.service.factory.figure.FigureFactoryCreator;

import java.util.ArrayList;
import java.util.List;

public class StartPositionFigureInitializer implements FigureInitializer {
    @Override
    public List<Figure> createFigures(GamerColor color) {
        List<Figure> figures = new ArrayList<>();
        FigureFactory figureFactory = FigureFactoryCreator.createFigureFactory(color);
        FigureType[] allTypes = FigureType.values();
        for (FigureType type : allTypes) {
            List<Coordinate> defaultStartCoordinates = null;
            if (type.equals(FigureType.QUEEN) || type.equals(FigureType.KING)) {
                defaultStartCoordinates = List.of(customBehavior(type, color));
            } else {
                defaultStartCoordinates = type.getDefaultStartCoordinate();
            }
            figures.addAll(figureFactory.createFigures(type, defaultStartCoordinates));
        }
        return figures;
    }

    private Coordinate customBehavior(FigureType type, GamerColor color) {
        Coordinate coordinates = null;
        if (type.equals(FigureType.QUEEN)) {
            coordinates = type.getDefaultStartCoordinate().get(0);
            if (color.equals(GamerColor.BLACK)) {
                coordinates.setColumn(coordinates.getColumn() + 1);
            }
        } else if (type.equals(FigureType.KING)) {
            coordinates = type.getDefaultStartCoordinate().get(0);
            if (color.equals(GamerColor.BLACK)) {
                coordinates.setColumn(coordinates.getColumn() - 1);
            }
        }
        return coordinates;
    }
}

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
            List<Coordinate> defaultStartCoordinates = type.getDefaultStartCoordinate();
            figures.addAll(figureFactory.createFigures(type, defaultStartCoordinates));
        }
        return figures;
    }
}

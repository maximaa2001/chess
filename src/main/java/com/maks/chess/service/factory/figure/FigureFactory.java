package com.maks.chess.service.factory.figure;

import com.maks.chess.constant.define.FigureType;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.figure.Figure;

import java.util.List;
import java.util.stream.Collectors;

public interface FigureFactory {

    default List<Figure> createFigures(FigureType type, List<Coordinate> coordinates) {
        return coordinates.stream().map(e -> createFigure(type, e)).collect(Collectors.toList());
    }

    Figure createFigure(FigureType type, Coordinate coordinate);
}

package com.maks.chess.service.factory.figure;

import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.figure.*;

public class BlackFigureFactory implements FigureFactory {
    @Override
    public Figure createFigure(FigureType type, Coordinate coordinate) {
        return switch (type) {
            case PAWN -> new Pawn(GamerColor.BLACK, coordinate);
            case CASTLE -> new Castle(GamerColor.BLACK, coordinate);
            case KNIGHT -> new Knight(GamerColor.BLACK, coordinate);
            case BISHOP -> new Bishop(GamerColor.BLACK, coordinate);
            case QUEEN -> new Queen(GamerColor.BLACK, coordinate);
            case KING -> new King(GamerColor.BLACK, coordinate);
        };
    }
}

package com.maks.chess.service.factory.figure;

import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.figure.*;

public class WhiteFigureFactory implements FigureFactory {
    @Override
    public Figure createFigure(FigureType type, Coordinate coordinate) {
        return switch (type) {
            case PAWN -> new Pawn(GamerColor.WHITE, coordinate);
            case CASTLE -> new Castle(GamerColor.WHITE, coordinate);
            case KNIGHT -> new Knight(GamerColor.WHITE, coordinate);
            case BISHOP -> new Bishop(GamerColor.WHITE, coordinate);
            case QUEEN -> new Queen(GamerColor.WHITE, coordinate);
            case KING -> new King(GamerColor.WHITE, coordinate);
        };
    }
}

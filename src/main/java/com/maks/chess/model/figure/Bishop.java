package com.maks.chess.model.figure;

import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.Coordinate;

public class Bishop extends Figure {
    public Bishop(GamerColor color, Coordinate coordinate) {
        super(color, coordinate);
    }

    @Override
    public FigureType getType() {
        return FigureType.BISHOP;
    }
}

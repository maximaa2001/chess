package com.maks.chess.model.figure;

import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.Coordinate;

import java.util.Objects;

public abstract class Figure {
    private final GamerColor color;
    private final Coordinate startCoordinate;

    public Figure(GamerColor color, Coordinate startCoordinate) {
        this.color = color;
        this.startCoordinate = startCoordinate;
    }


    public Coordinate getStartCoordinate() {
        return startCoordinate;
    }

    public GamerColor getColor() {
        return color;
    }

    public abstract FigureType getType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Figure figure)) return false;
        return color == figure.color && Objects.equals(startCoordinate, figure.startCoordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, startCoordinate);
    }
}

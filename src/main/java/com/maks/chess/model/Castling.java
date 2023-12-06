package com.maks.chess.model;

import java.io.Serializable;

public class Castling implements Serializable {
    private final Coordinate castleCoordinate;
    private final Coordinate oldKingCoordinate;
    private final Coordinate newKingCoordinate;

    public Castling(Coordinate castleCoordinate, Coordinate oldKingCoordinate, Coordinate newKingCoordinate) {
        this.castleCoordinate = castleCoordinate;
        this.oldKingCoordinate = oldKingCoordinate;
        this.newKingCoordinate = newKingCoordinate;
    }

    public Coordinate getCastleCoordinate() {
        return castleCoordinate;
    }

    public Coordinate getOldKingCoordinate() {
        return oldKingCoordinate;
    }

    public Coordinate getNewKingCoordinate() {
        return newKingCoordinate;
    }

    @Override
    public String toString() {
        return "Castling{" +
                "castleCoordinate=" + castleCoordinate +
                ", oldKingCoordinate=" + oldKingCoordinate +
                ", newKingCoordinate=" + newKingCoordinate +
                '}';
    }
}

package com.maks.chess.model;

import com.maks.chess.constant.define.FigureActivity;
import com.maks.chess.constant.define.FigureType;

import java.io.Serializable;

public class PawnEvolution implements Serializable {
    private final Coordinate from;
    private final Coordinate to;
    private final FigureActivity activity;
    private final FigureType type;

    public PawnEvolution(Coordinate from, Coordinate to, FigureActivity activity, FigureType type) {
        this.from = from;
        this.to = to;
        this.activity = activity;
        this.type = type;
    }

    public Coordinate getFrom() {
        return from;
    }

    public Coordinate getTo() {
        return to;
    }

    public FigureActivity getActivity() {
        return activity;
    }

    public FigureType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "PawnEvolution{" +
                "from=" + from +
                ", to=" + to +
                ", activity=" + activity +
                ", type=" + type +
                '}';
    }
}

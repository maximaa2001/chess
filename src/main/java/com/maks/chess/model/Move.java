package com.maks.chess.model;

import com.maks.chess.constant.define.FigureActivity;

import java.io.Serializable;

public class Move implements Serializable {
    private final Coordinate from;
    private final Coordinate to;
    private final FigureActivity activity;

    public Move(Coordinate from, Coordinate to, FigureActivity activity) {
        this.from = from;
        this.to = to;
        this.activity = activity;
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

    @Override
    public String toString() {
        return "Move{" +
                "from=" + from +
                ", to=" + to +
                ", activity=" + activity +
                '}';
    }
}

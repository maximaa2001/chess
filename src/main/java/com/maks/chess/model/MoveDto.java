package com.maks.chess.model;

import com.maks.chess.constant.define.FigureActivity;

import java.io.Serializable;
import java.util.List;

public class MoveDto implements Serializable {
    private final Coordinate from;
    private final Coordinate to;
    private final FigureActivity activity;
    private final boolean kingInDanger;
    private final PawnEvolutionDto pawnEvolutionDto;
    private final List<String> logs;

    public MoveDto(Coordinate from, Coordinate to, FigureActivity activity, boolean kingInDanger,
                   PawnEvolutionDto pawnEvolutionDto, List<String> logs) {
        this.from = from;
        this.to = to;
        this.activity = activity;
        this.kingInDanger = kingInDanger;
        this.pawnEvolutionDto = pawnEvolutionDto;
        this.logs = logs;
    }

    public MoveDto(Coordinate from, Coordinate to, FigureActivity activity, boolean kingInDanger, List<String> logs) {
        this(from, to, activity, kingInDanger, null, logs);
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

    public Boolean getKingInDanger() {
        return kingInDanger;
    }

    public PawnEvolutionDto getPawnEvolutionDto() {
        return pawnEvolutionDto;
    }

    public List<String> getLogs() {
        return logs;
    }

    @Override
    public String toString() {
        return "MoveDto{" +
                "from=" + from +
                ", to=" + to +
                ", activity=" + activity +
                ", kingInDanger=" + kingInDanger +
                '}';
    }
}

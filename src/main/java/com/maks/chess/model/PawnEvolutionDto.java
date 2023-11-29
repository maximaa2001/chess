package com.maks.chess.model;

import com.maks.chess.constant.define.FigureType;

import java.io.Serializable;

public class PawnEvolutionDto implements Serializable {
    private final FigureType type;

    public PawnEvolutionDto(FigureType type) {
        this.type = type;
    }

    public FigureType getType() {
        return type;
    }
}

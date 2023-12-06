package com.maks.chess.model;

import java.io.Serializable;
import java.util.List;

public class MoveDto implements Serializable {
    private final Move move;
    private final Castling castling;
    private final PawnEvolution pawnEvolution;
    private final List<String> logs;
    private final Boolean endGame;

    public MoveDto(Move move, PawnEvolution pawnEvolution, Castling castling, List<String> logs, Boolean endGame) {
        this.move = move;
        this.castling = castling;
        this.pawnEvolution = pawnEvolution;
        this.logs = logs;
        this.endGame = endGame;
    }

    public MoveDto(Move move, List<String> logs) {
        this(move, null, null, logs, false);
    }

    public MoveDto(Castling castling, List<String> logs) {
        this(null, null, castling, logs, false);
    }

    public MoveDto(String losingLog) {
        this(null, null, null, List.of(losingLog), true);
    }

    public Move getMove() {
        return move;
    }

    public Castling getCastling() {
        return castling;
    }

    public PawnEvolution getPawnEvolutionDto() {
        return pawnEvolution;
    }

    public List<String> getLogs() {
        return logs;
    }

    public Boolean getEndGame() {
        return endGame;
    }

    @Override
    public String toString() {
        return "MoveDto{" +
                "move=" + move +
                ", castling=" + castling +
                ", pawnEvolution=" + pawnEvolution +
                ", logs=" + logs +
                ", endGame=" + endGame +
                '}';
    }
}

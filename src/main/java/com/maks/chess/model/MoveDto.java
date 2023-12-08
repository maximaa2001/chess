package com.maks.chess.model;

import java.io.Serializable;
import java.util.List;

public class MoveDto implements Serializable {
    private final Move move;
    private final Castling castling;
    private final PawnEvolution pawnEvolution;
    private final List<String> logs;
    private final Boolean endGame;
    private final Boolean kingInDanger;

    public MoveDto(Move move, PawnEvolution pawnEvolution, Castling castling, List<String> logs, Boolean endGame, Boolean kingInDanger) {
        this.move = move;
        this.castling = castling;
        this.pawnEvolution = pawnEvolution;
        this.logs = logs;
        this.endGame = endGame;
        this.kingInDanger = kingInDanger;
    }

    public MoveDto(Move move, List<String> logs, Boolean kingInDanger) {
        this(move, null, null, logs, false, kingInDanger);
    }

    public MoveDto(Castling castling, List<String> logs, Boolean kingInDanger) {
        this(null, null, castling, logs, false, kingInDanger);
    }

    public MoveDto(String losingLog) {
        this(null, null, null, List.of(losingLog), true, false);
    }

    public Move getMove() {
        return move;
    }

    public Castling getCastling() {
        return castling;
    }

    public PawnEvolution getPawnEvolution() {
        return pawnEvolution;
    }

    public List<String> getLogs() {
        return logs;
    }

    public Boolean getEndGame() {
        return endGame;
    }

    public Boolean getKingInDanger() {
        return kingInDanger;
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

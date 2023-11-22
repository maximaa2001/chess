package com.maks.chess.model.figure;

import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.ChessModel;
import com.maks.chess.model.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Bishop extends Figure {

    public Bishop(GamerColor color, Coordinate coordinate) {
        super(color, coordinate);
    }

    @Override
    public FigureType getType() {
        return FigureType.BISHOP;
    }

    @Override
    public List<Coordinate> checkMoves(ChessModel chessModel, Coordinate from) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        Arrays.stream(Direction.values())
                .forEach(e -> checkMovingDirection(from, e.getROW_CHANGER(), e.getCOLUMN_CHANGER(), possibleMoves, chessModel));
        return possibleMoves;
    }

    @Override
    public List<Coordinate> checkPossibilityEat(ChessModel chessModel, Coordinate from) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        Arrays.stream(Direction.values())
                .forEach(e -> checkEatingDirection(from, e.getROW_CHANGER(), e.getCOLUMN_CHANGER(), possibleMoves, chessModel));
        return possibleMoves;
    }

    private enum Direction {
        LEFT_TOP((row) -> --row, (column) -> --column),
        LEFT_BOTTOM((row) -> ++row, (column) -> --column),
        RIGHT_TOP((row) -> --row, (column) -> ++column),
        RIGHT_BOTTOM((row) -> ++row, (column) -> ++column);

        Direction(Function<Integer, Integer> ROW_CHANGER, Function<Integer, Integer> COLUMN_CHANGER) {
            this.ROW_CHANGER = ROW_CHANGER;
            this.COLUMN_CHANGER = COLUMN_CHANGER;
        }

        private final Function<Integer, Integer> ROW_CHANGER;
        private final Function<Integer, Integer> COLUMN_CHANGER;

        public Function<Integer, Integer> getROW_CHANGER() {
            return ROW_CHANGER;
        }

        public Function<Integer, Integer> getCOLUMN_CHANGER() {
            return COLUMN_CHANGER;
        }
    }
}

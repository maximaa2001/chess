package com.maks.chess.model.figure;

import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.ChessModel;
import com.maks.chess.model.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class King extends Figure {
    private boolean moved = false;
    public King(GamerColor color, Coordinate coordinate) {
        super(color, coordinate);
    }

    @Override
    public FigureType getType() {
        return FigureType.KING;
    }

    @Override
    public List<Coordinate> checkMoves(ChessModel chessModel, Coordinate from) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        Arrays.stream(Direction.values())
                .forEach(e -> checkMovingDirection(from, e.getROW_CHANGER(), e.getCOLUMN_CHANGER(), possibleMoves, chessModel));
        return possibleMoves;
    }

    @Override
    public List<Coordinate> checkPossibilityEat(ChessModel chessModel, Coordinate from, GamerColor colorToEat) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        Arrays.stream(Direction.values())
                .forEach(e -> checkEatingDirection(from, e.getROW_CHANGER(), e.getCOLUMN_CHANGER(), possibleMoves, chessModel, colorToEat));
        return possibleMoves;
    }

    @Override
    protected void checkMovingDirection(Coordinate from, Function<Integer, Integer> rowChanger,
                                        Function<Integer, Integer> columnChanger,
                                        List<Coordinate> possibleMoves, ChessModel chessModel) {
        int row = rowChanger.apply(from.getRow());
        int column = columnChanger.apply(from.getColumn());
        if (row >= 0 && row < AppConstant.BOARD_SIDE_SIZE && column >= 0 && column < AppConstant.BOARD_SIDE_SIZE) {
            Coordinate possibleCoordinate = new Coordinate(row, column);
            if (chessModel.getByCoordinate(possibleCoordinate) == null) {
                possibleMoves.add(possibleCoordinate);
            }
        }
    }

    @Override
    protected void checkEatingDirection(Coordinate from, Function<Integer, Integer> rowChanger,
                                        Function<Integer, Integer> columnChanger,
                                        List<Coordinate> possibleMoves, ChessModel chessModel, GamerColor colorToEat) {
        int row = rowChanger.apply(from.getRow());
        int column = columnChanger.apply(from.getColumn());
        if (row >= 0 && row < AppConstant.BOARD_SIDE_SIZE && column >= 0 && column < AppConstant.BOARD_SIDE_SIZE) {
            Coordinate possibleCoordinate = new Coordinate(row, column);
            Figure figureOnPossibleCoordinate = chessModel.getByCoordinate(possibleCoordinate);
            if (figureOnPossibleCoordinate != null && figureOnPossibleCoordinate.getColor().equals(colorToEat)) {
                possibleMoves.add(possibleCoordinate);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof King king)) return false;
        return color == king.color && Objects.equals(startCoordinate, king.startCoordinate);
    }

    public void setMovedFlag() {
        this.moved = true;
    }

    public boolean isMoved() {
        return moved;
    }
    private enum Direction {
        LEFT((row) -> row, (column) -> --column),
        RIGHT((row) -> row, (column) -> ++column),
        TOP((row) -> --row, (column) -> column),
        BOTTOM((row) -> ++row, (column) -> column),
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

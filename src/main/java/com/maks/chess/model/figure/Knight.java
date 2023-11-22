package com.maks.chess.model.figure;

import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.ChessModel;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.data.DataStoreFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Knight extends Figure {
    public Knight(GamerColor color, Coordinate coordinate) {
        super(color, coordinate);
    }

    @Override
    public FigureType getType() {
        return FigureType.KNIGHT;
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
                                        List<Coordinate> possibleMoves, ChessModel chessModel) {
        int row = rowChanger.apply(from.getRow());
        int column = columnChanger.apply(from.getColumn());
        if (row >= 0 && row < AppConstant.BOARD_SIDE_SIZE && column >= 0 && column < AppConstant.BOARD_SIDE_SIZE) {
            Coordinate possibleCoordinate = new Coordinate(row, column);
            Figure figureOnPossibleCoordinate = chessModel.getByCoordinate(possibleCoordinate);
            if (figureOnPossibleCoordinate != null && !figureOnPossibleCoordinate.getColor().equals(DataStoreFactory.getDataStore().getColor())) {
                possibleMoves.add(possibleCoordinate);
            }
        }
    }

    private enum Direction {
        TOP_LEFT((row) -> row - 2, (column) -> --column),
        TOP_RIGHT((row) -> row - 2, (column) -> ++column),
        BOTTOM_LEFT((row) -> row + 2, (column) -> --column),
        BOTTOM_RIGHT((row) -> row + 2, (column) -> ++column),
        LEFT_TOP((row) -> --row, (column) -> column - 2),
        LEFT_BOTTOM((row) -> ++row, (column) -> column - 2),
        RIGHT_TOP((row) -> --row, (column) -> column + 2),
        RIGHT_BOTTOM((row) -> ++row, (column) -> column + 2);

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

package com.maks.chess.model.figure;

import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.ChessModel;
import com.maks.chess.model.Coordinate;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public abstract class Figure {
    protected final GamerColor color;
    protected final Coordinate startCoordinate;

    public Figure(GamerColor color, Coordinate startCoordinate) {
        this.color = color;
        this.startCoordinate = startCoordinate;
    }


    public Coordinate getStartCoordinate() {
        return startCoordinate;
    }

    public GamerColor getColor() {
        return color;
    }

    protected void checkMovingDirection(Coordinate from, Function<Integer, Integer> rowChanger,
                                        Function<Integer, Integer> columnChanger,
                                        List<Coordinate> possibleMoves, ChessModel chessModel) {
        int row = rowChanger.apply(from.getRow());
        int column = columnChanger.apply(from.getColumn());
        while (row >= 0 && row < AppConstant.BOARD_SIDE_SIZE && column >= 0 && column < AppConstant.BOARD_SIDE_SIZE) {
            Coordinate possibleCoordinate = new Coordinate(row, column);
            if (chessModel.getByCoordinate(possibleCoordinate) != null) {
                break;
            }
            possibleMoves.add(possibleCoordinate);
            row = rowChanger.apply(row);
            column = columnChanger.apply(column);
        }
    }

    protected void checkEatingDirection(Coordinate from, Function<Integer, Integer> rowChanger,
                                        Function<Integer, Integer> columnChanger,
                                        List<Coordinate> possibleMoves, ChessModel chessModel, GamerColor colorToEat) {
        int row = rowChanger.apply(from.getRow());
        int column = columnChanger.apply(from.getColumn());
        while (row >= 0 && row < AppConstant.BOARD_SIDE_SIZE && column >= 0 && column < AppConstant.BOARD_SIDE_SIZE) {
            Coordinate possibleCoordinate = new Coordinate(row, column);
            Figure figureOnPossibleCoordinate = chessModel.getByCoordinate(possibleCoordinate);
            if (figureOnPossibleCoordinate != null) {
                if (figureOnPossibleCoordinate.getColor().equals(colorToEat)) {
                    possibleMoves.add(possibleCoordinate);
                }
                break;
            }
            row = rowChanger.apply(row);
            column = columnChanger.apply(column);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, startCoordinate);
    }

    public abstract FigureType getType();

    public abstract List<Coordinate> checkMoves(ChessModel chessModel, Coordinate from);

    public abstract List<Coordinate> checkPossibilityEat(ChessModel chessModel, Coordinate from, GamerColor colorToEat);
}

package com.maks.chess.model.figure;

import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.ChessModel;
import com.maks.chess.model.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Pawn extends Figure {

    public Pawn(GamerColor color, Coordinate coordinate) {
        super(color, coordinate);
    }

    @Override
    public FigureType getType() {
        return FigureType.PAWN;
    }

    @Override
    public List<Coordinate> checkMoves(ChessModel chessModel, Coordinate from) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        if (from.getRow().equals(AppConstant.BOARD_SIDE_SIZE - 2)) {
            for (int i = from.getRow() - 1; i >= from.getRow() - 2; i--) {
                Coordinate possibleCoordinate = new Coordinate(i, from.getColumn());
                Figure figureOnPossibleCoordinate = chessModel.getByCoordinate(possibleCoordinate);
                if (figureOnPossibleCoordinate != null) {
                    break;
                }
                possibleMoves.add(possibleCoordinate);
            }
        } else {
            Coordinate possibleCoordinate = new Coordinate(from.getRow() - 1, from.getColumn());
            Figure figureOnPossibleCoordinate = chessModel.getByCoordinate(possibleCoordinate);
            if (figureOnPossibleCoordinate == null) {
                possibleMoves.add(possibleCoordinate);
            }
        }
        return possibleMoves;
    }

    @Override
    public List<Coordinate> checkPossibilityEat(ChessModel chessModel, Coordinate from, GamerColor colorToEat) {
        List<Coordinate> possibleMoves = new ArrayList<>();
        int newRow = from.getRow() - 1;
        if (newRow >= 0) {
            List<Integer> newColumns = List.of(from.getColumn() - 1, from.getColumn() + 1);
            for (Integer column : newColumns) {
                if (column >= 0 && column < AppConstant.BOARD_SIDE_SIZE) {
                    Coordinate possibleCoordinate = new Coordinate(newRow, column);
                    Figure figureOnPossibleCoordinate = chessModel.getByCoordinate(possibleCoordinate);
                    if (figureOnPossibleCoordinate != null && figureOnPossibleCoordinate.getColor().equals(colorToEat)) {
                        possibleMoves.add(possibleCoordinate);
                    }
                }
            }
        }
        return possibleMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pawn pawn)) return false;
        return color == pawn.color && Objects.equals(startCoordinate, pawn.startCoordinate);
    }
}

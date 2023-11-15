package com.maks.chess.service.transformer;

import com.maks.chess.constant.AppConstant;
import com.maks.chess.model.Coordinate;

public class ToEnemyCoordinateTransformer implements CoordinateTransformer {
    @Override
    public Coordinate transform(Coordinate coordinate) {
        final int row = AppConstant.BOARD_SIDE_SIZE - 1 - coordinate.getRow();
        final int column = AppConstant.BOARD_SIDE_SIZE - 1 - coordinate.getColumn();
        return new Coordinate(row, column);
    }
}

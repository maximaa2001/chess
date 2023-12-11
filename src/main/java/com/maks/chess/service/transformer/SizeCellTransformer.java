package com.maks.chess.service.transformer;

import com.maks.chess.constant.AppConstant;
import com.maks.chess.model.Coordinate;
import javafx.util.Pair;

public class SizeCellTransformer implements CellTransformer {
    @Override
    public Pair<Double, Double> transform(Coordinate coordinate) {
        final int CELL_SIZE = AppConstant.CELL_SIZE;
        return new Pair<>((double) coordinate.getRow() * CELL_SIZE, (double) coordinate.getColumn() * CELL_SIZE);
    }
}

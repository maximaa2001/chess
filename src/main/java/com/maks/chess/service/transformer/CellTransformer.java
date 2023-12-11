package com.maks.chess.service.transformer;

import com.maks.chess.model.Coordinate;
import javafx.util.Pair;

public interface CellTransformer {
    Pair<Double, Double> transform(Coordinate coordinate);
}

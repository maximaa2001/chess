package com.maks.chess.service.transformer;

import com.maks.chess.model.Coordinate;

public interface CoordinateTransformer {
    Coordinate transform(Coordinate coordinate);
}

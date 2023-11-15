package com.maks.chess.service.initializer;

import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.figure.Figure;

import java.util.List;

public interface FigureInitializer {
    List<Figure> createFigures(GamerColor color);
}

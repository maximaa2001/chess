package com.maks.chess.service.factory.sprite;

import com.maks.chess.constant.define.FigureType;
import javafx.geometry.Rectangle2D;

public interface SpriteResolver {
    Rectangle2D createSprite(FigureType type);
}

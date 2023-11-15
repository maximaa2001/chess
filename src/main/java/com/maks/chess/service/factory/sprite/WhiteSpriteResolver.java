package com.maks.chess.service.factory.sprite;

import com.maks.chess.constant.define.FigureType;
import javafx.geometry.Rectangle2D;

public class WhiteSpriteResolver implements SpriteResolver {
    @Override
    public Rectangle2D createSprite(FigureType type) {
        return switch (type) {
            case PAWN -> new Rectangle2D(435, 15, 44, 59);
            case CASTLE -> new Rectangle2D(349, 15, 51, 57);
            case KNIGHT ->  new Rectangle2D(260, 12, 61, 62);
            case BISHOP ->  new Rectangle2D(177, 9, 62, 63);
            case QUEEN ->  new Rectangle2D(10, 10, 62, 63);
            case KING ->  new Rectangle2D(89, 9, 70, 66);
        };
    }
}

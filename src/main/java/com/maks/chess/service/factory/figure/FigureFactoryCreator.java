package com.maks.chess.service.factory.figure;

import com.maks.chess.constant.define.GamerColor;

public final class FigureFactoryCreator {
    public static FigureFactory createFigureFactory(GamerColor color) {
        return switch (color) {
            case WHITE -> new WhiteFigureFactory();
            case BLACK -> new BlackFigureFactory();
        };
    }
}

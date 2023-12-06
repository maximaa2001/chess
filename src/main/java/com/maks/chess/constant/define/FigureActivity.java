package com.maks.chess.constant.define;

import javafx.scene.paint.Paint;

import java.io.Serializable;
import java.util.Arrays;

public enum FigureActivity implements Serializable {
    CHOOSE(Paint.valueOf("#FFF360")),
    MOVE(Paint.valueOf("#ABF7A3")),
    EAT(Paint.valueOf("red")),
    CASTLING(Paint.valueOf("22FFFC"));

    FigureActivity(Paint cellColor) {
        this.cellColor = cellColor;
    }

    private final Paint cellColor;

    public Paint getCellColor() {
        return cellColor;
    }

    public static FigureActivity of(Paint paint) {
        return Arrays.stream(FigureActivity.values())
                .filter(e -> e.getCellColor().equals(paint))
                .findAny()
                .orElse(null);
    }
}

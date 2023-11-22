package com.maks.chess.util;

import com.maks.chess.model.Coordinate;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class AppUtils {
    public static String generateRectangleId(Coordinate coordinate) {
        return String.format("%s_%s", coordinate.getRow(), coordinate.getColumn());
    }

    public static String generateImageViewId(Coordinate coordinate) {
        return String.format("%s_%s", coordinate.getRow(), coordinate.getColumn());
    }

    public static Coordinate resolverRectangleId(Rectangle rectangle) {
        String id = rectangle.getId();
        String[] split = id.split("_");
        return new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    public static Coordinate resolverImageViewId(ImageView imageView) {
        String id = imageView.getId();
        String[] split = id.split("_");
        return new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}

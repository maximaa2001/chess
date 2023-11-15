package com.maks.chess.service.factory.sprite;

import com.maks.chess.MainApplication;
import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

public class ImageViewCreator {
    private static final URL PATH = MainApplication.class.getResource(AppConstant.WHITE_AND_BLACK_FIGURES_FILE_NAME);

    public static ImageView createImageViewResolver(FigureType type, GamerColor color) {
        ImageView imageView = null;
        try {
            imageView = new ImageView(new Image(new FileInputStream(PATH.getPath())));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        SpriteResolver spriteResolver = getSpriteResolver(color);
        Rectangle2D sprite = spriteResolver.createSprite(type);
        imageView.setViewport(sprite);
        return imageView;
    }

    private static SpriteResolver getSpriteResolver(GamerColor color) {
        return switch (color) {
            case WHITE -> new WhiteSpriteResolver();
            case BLACK -> new BlackSpriteResolver();
        };
    }
}

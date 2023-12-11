package com.maks.chess.service.transition;

import com.maks.chess.model.Coordinate;
import com.maks.chess.service.transformer.CellTransformer;
import com.maks.chess.util.VoidSmth;
import javafx.animation.TranslateTransition;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.util.Pair;

public class DefaultImageViewTransitionService implements ImageViewTransitionService {
    private static final Duration ANIMATION_TIME = javafx.util.Duration.seconds(1);
    private final CellTransformer cellTransformer;

    public DefaultImageViewTransitionService(CellTransformer cellTransformer) {
        this.cellTransformer = cellTransformer;
    }

    @Override
    public void transition(ImageView imageView, Coordinate fromCell, Coordinate toCell, VoidSmth onFinishCallback) {
        Pair<Double, Double> toTransform = cellTransformer.transform(toCell);
        Pair<Double, Double> fromTransform = cellTransformer.transform(fromCell);
        transition(imageView, toTransform.getValue() - fromTransform.getValue(), toTransform.getKey() - fromTransform.getKey(), onFinishCallback);
    }

    @Override
    public void transition(ImageView node, Double translateX, Double translateY, VoidSmth onFinishCallback) {
        TranslateTransition transition = new TranslateTransition(ANIMATION_TIME, node);
        transition.setToX(translateX);
        transition.setToY(translateY);
        transition.setOnFinished(action -> {
            if(onFinishCallback != null) {
                onFinishCallback.execute();
            }
            resetTransitionCoordinates(node);
        });
        transition.play();
    }

    private void resetTransitionCoordinates(ImageView imageView) {
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
    }
}

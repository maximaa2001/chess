package com.maks.chess.service.transition;

import com.maks.chess.model.Coordinate;
import com.maks.chess.util.VoidSmth;
import javafx.scene.image.ImageView;

public interface ImageViewTransitionService extends TransitionService<ImageView> {
    void transition(ImageView imageView, Coordinate fromCell, Coordinate toCell, VoidSmth onFinishCallback);
}

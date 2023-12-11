package com.maks.chess.service.transition;

import com.maks.chess.util.VoidSmth;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

public interface TransitionService<T extends Node> {
    void transition(T node, Double translateX, Double translateY, VoidSmth onFinishCallback);
}

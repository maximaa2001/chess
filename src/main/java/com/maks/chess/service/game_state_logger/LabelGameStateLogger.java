package com.maks.chess.service.game_state_logger;

import com.maks.chess.MainApplication;
import com.maks.chess.constant.StyleConstant;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;

public class LabelGameStateLogger implements GameStateLogger {
    private final VBox scrollContentPane;

    public LabelGameStateLogger(VBox scrollContentPane) {
        this.scrollContentPane = scrollContentPane;
    }

    @Override
    public void log(String log) {
        Label label = new Label(log);
        label.setPrefHeight(25);
        label.setFont(new Font(16));
        label.getStyleClass().add(resolveStyles(log));
        scrollContentPane.getChildren().add(label);
    }

    private String resolveStyles(String log) {
        if(log.contains("Вы")) {
            return StyleConstant.YOU_LOG_CLASS_NAME;
        }
        return StyleConstant.ENEMY_LOG_CLASS_NAME;
    }
}

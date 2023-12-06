package com.maks.chess.view.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class GiveUpAlert {
    private static final String HEADER_TEXT = "Вы действительно хотите сдаться?";
    private static final String CONTENT_TEXT = "Сдаться?";
    private final Alert alert;

    public GiveUpAlert() {
        this.alert = new Alert(Alert.AlertType.CONFIRMATION);
        this.alert.setHeaderText(HEADER_TEXT);
        this.alert.setContentText(CONTENT_TEXT);
    }

    public Optional<ButtonType> show() {
        return alert.showAndWait();
    }

}

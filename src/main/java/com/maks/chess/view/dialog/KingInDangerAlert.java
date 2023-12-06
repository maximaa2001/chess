package com.maks.chess.view.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class KingInDangerAlert {
    private static final String HEADER_TEXT = "Вы не можете сделать этот ход";
    private static final String CONTENT_TEXT = "Ваш король будет находиться под угрозой";
    private final Alert alert;

    public KingInDangerAlert() {
        this.alert = new Alert(Alert.AlertType.INFORMATION);
        this.alert.setHeaderText(HEADER_TEXT);
        this.alert.setContentText(CONTENT_TEXT);
    }

    public Optional<ButtonType> show() {
        return alert.showAndWait();
    }
}

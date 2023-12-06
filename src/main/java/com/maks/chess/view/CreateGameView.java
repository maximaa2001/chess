package com.maks.chess.view;

import com.maks.chess.MainApplication;
import com.maks.chess.constant.StyleConstant;
import com.maks.chess.constant.ViewConstant;
import com.maks.chess.controller.Controller;
import com.maks.chess.controller.CreateGameController;
import com.maks.chess.util.AppUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

public class CreateGameView extends AbstractView {
    private static final URL FXML = MainApplication.class.getResource(ViewConstant.CREATE_GAME_FXML);
    private Label mainLabel;
    private Label messageLabel;
    private Button createButton;
    private Button cancelButton;
    private TextField portInput;

    public CreateGameView(Stage stage) {
        super(stage);
    }

    public void initElements(List<Label> labels, List<Button> buttons, TextField textField) {
        this.mainLabel = labels.get(0);
        this.messageLabel = labels.get(1);
        this.createButton = buttons.get(0);
        this.cancelButton = buttons.get(1);
        this.portInput = textField;
        addFont(List.of(mainLabel, createButton, cancelButton));
        disableCancelButton(true);
    }

    public String getPort() {
        return portInput.getText();
    }

    public void setErrorText(String error) {
        messageLabel.getStyleClass().remove(StyleConstant.SUCCESS_MESSAGE_CLASS_NAME);
        messageLabel.getStyleClass().add(StyleConstant.ERROR_MESSAGE_CLASS_NAME);
        setText(error);
    }

    public void setSuccessText(String message) {
        messageLabel.getStyleClass().remove(StyleConstant.ERROR_MESSAGE_CLASS_NAME);
        messageLabel.getStyleClass().add(StyleConstant.SUCCESS_MESSAGE_CLASS_NAME);
        setText(message);
    }

    private void setText(String message) {
        AppUtils.executeGui(() -> messageLabel.setText(message));
    }

    public void disableCancelButton(boolean isDisable) {
        cancelButton.setDisable(isDisable);
    }

    @Override
    protected URL getFxmlUrl() {
        return FXML;
    }

    @Override
    protected Controller getController() {
        return new CreateGameController(this);
    }

}

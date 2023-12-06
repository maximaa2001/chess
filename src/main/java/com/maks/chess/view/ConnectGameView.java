package com.maks.chess.view;

import com.maks.chess.MainApplication;
import com.maks.chess.constant.StyleConstant;
import com.maks.chess.constant.ViewConstant;
import com.maks.chess.controller.ConnectGameController;
import com.maks.chess.controller.Controller;
import com.maks.chess.util.AppUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;

public class ConnectGameView extends AbstractView {
    private static final URL FXML = MainApplication.class.getResource(ViewConstant.CONNECT_GAME_FXML);
    private TextField addressInput;
    private Button connectBtn;
    private Label mainLabel;
    private Label messageLabel;

    public ConnectGameView(Stage stage) {
        super(stage);
    }

    public void initElements(List<Label> labels, Button button, TextField textField) {
        this.mainLabel = labels.get(0);
        this.messageLabel = labels.get(1);
        this.connectBtn = button;
        this.addressInput = textField;
        addFont(List.of(mainLabel, connectBtn));
    }

    public String getAddress() {
        return addressInput.getText();
    }

    public void setErrorText(String error) {
        messageLabel.getStyleClass().remove(StyleConstant.SUCCESS_MESSAGE_CLASS_NAME);
        messageLabel.getStyleClass().add(StyleConstant.ERROR_MESSAGE_CLASS_NAME);
        setText(error);
    }

    private void setText(String message) {
        AppUtils.executeGui(() -> messageLabel.setText(message));
    }

    @Override
    protected URL getFxmlUrl() {
        return FXML;
    }

    @Override
    protected Controller getController() {
        return new ConnectGameController(this);
    }
}

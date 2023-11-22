package com.maks.chess.controller;

import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.data.DataStoreFactory;
import com.maks.chess.model.socket.SocketStoreFactory;
import com.maks.chess.view.ConnectGameView;
import com.maks.chess.view.View;
import com.maks.chess.view.ViewFactory;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ResourceBundle;

public class ConnectGameController implements Controller {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField addressInput;

    @FXML
    private Button connectBtn;

    @FXML
    private Label mainLabel;

    @FXML
    private Label messageLabel;

    private final ConnectGameView connectGameView;
    private final KeyboardEventHandler keyboardEventHandler = new KeyboardEventHandler();
    private final MouseEventHandler mouseEventHandler = new MouseEventHandler();

    public ConnectGameController(ConnectGameView connectGameView) {
        this.connectGameView = connectGameView;
    }

    @FXML
    void initialize() {
        connectGameView.initElements(List.of(mainLabel, messageLabel), connectBtn, addressInput);
        connectGameView.addKeyHandler(keyboardEventHandler);
        connectBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);
    }

    private class MouseEventHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
                Button button = (Button) mouseEvent.getSource();
                if (button == connectBtn) {
                    String address = connectGameView.getAddress();
                    String[] split = address.split(":");
                    if (split.length != 2) {
                        connectGameView.setErrorText("Введите корректный ip адрес и порт");
                    } else {
                        try {
                            Socket socket = new Socket(split[0], Integer.parseInt(split[1]));
                            DataStoreFactory.getDataStore().setColor(GamerColor.BLACK);
                            SocketStoreFactory.getSocketFactory().saveSocket(socket);
                            connectGameView.removeKeyHandler(keyboardEventHandler);
                            ViewFactory.transition(View.BOARD, connectGameView.getStage());
                        } catch (UnknownHostException e) {
                            connectGameView.setErrorText("Введите корректный ip адрес и порт");
                        } catch (IOException e) {
                            connectGameView.setErrorText("Соединение прервано");
                        }
                    }
                }
            }
        }

    }

    private class KeyboardEventHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                connectGameView.removeKeyHandler(keyboardEventHandler);
                ViewFactory.transition(View.MENU, connectGameView.getStage());
            }
        }
    }
}

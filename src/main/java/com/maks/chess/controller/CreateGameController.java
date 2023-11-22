package com.maks.chess.controller;

import com.maks.chess.model.socket.DefaultSocketStore;
import com.maks.chess.model.socket.SocketStore;
import com.maks.chess.service.thread.WaitingConnectionThread;
import com.maks.chess.service.thread.ServerSocketCallable;
import com.maks.chess.view.CreateGameView;
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

import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CreateGameController implements Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button cancelButton;

    @FXML
    private Button createButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Label mainLabel;

    @FXML
    private TextField portInput;

    private final CreateGameView createGameView;
    private final KeyboardEventHandler keyboardEventHandler = new KeyboardEventHandler();
    private final MouseEventHandler mouseEventHandler = new MouseEventHandler();

    private ServerSocketCallable serverSocketCallable;
    private WaitingConnectionThread waitingConnectionThread;
    private final SocketStore socketStore;


    public CreateGameController(CreateGameView createGameView) {
        this.createGameView = createGameView;
        socketStore = DefaultSocketStore.getSocketStore();
    }

    @FXML
    void initialize() {
        createGameView.initElements(List.of(mainLabel, messageLabel), List.of(createButton, cancelButton), portInput);
        createGameView.addKeyHandler(keyboardEventHandler);
        createButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);
        cancelButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);
    }

    private class KeyboardEventHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                closeOpenConnection();
                createGameView.removeKeyHandler(keyboardEventHandler);
                ViewFactory.transition(View.MENU, createGameView.getStage());
            }
        }
    }

    private class MouseEventHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (mouseEvent.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
                Button button = (Button) mouseEvent.getSource();
                if (button == createButton) {
                    String port = createGameView.getPort().trim();
                    Integer portNumber = null;
                    try {
                        portNumber = Integer.parseInt(port);
                    } catch (NumberFormatException e) {
                        createGameView.setErrorText("Введите корректный порт");
                    }
                    if (portNumber == null || portNumber <= 0) {
                        createGameView.setErrorText("Введите корректный порт");
                    } else {
                        ExecutorService executorService = Executors.newSingleThreadExecutor();
                        serverSocketCallable = new ServerSocketCallable(portNumber);
                        Future<Socket> futureSocket = executorService.submit(serverSocketCallable);
                        waitingConnectionThread = new WaitingConnectionThread(futureSocket, (error) -> {
                            createGameView.setErrorText(error);
                            closeOpenConnection();
                        }, () -> {
                            createGameView.removeKeyHandler(keyboardEventHandler);
                            ViewFactory.transition(View.BOARD, createGameView.getStage());
                        });
                        waitingConnectionThread.start();
                        executorService.shutdown();
                        createGameView.setSuccessText("Ожидание подключения второго игрока");
                        createGameView.disableCancelButton(false);
                    }
                } else if (button == cancelButton) {
                    closeOpenConnection();
                    createGameView.disableCancelButton(true);
                }
            }
        }
    }

    private void closeOpenConnection() {
        if (serverSocketCallable != null) {
            serverSocketCallable.closeConnection();
        }
    }

}
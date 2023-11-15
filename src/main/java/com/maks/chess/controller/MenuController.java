package com.maks.chess.controller;

import com.maks.chess.view.CreateGameView;
import com.maks.chess.view.MenuView;
import com.maks.chess.view.View;
import com.maks.chess.view.ViewFactory;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController implements Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label createGameLabel;

    @FXML
    private Label findGameLabel;

    private final MenuView menuView;

    private int activeMenuItem = 0;
    private final KeyboardEventHandler keyboardEventHandler = new KeyboardEventHandler();

    public MenuController(MenuView menuView) {
        this.menuView = menuView;
    }

    @FXML
    void initialize() {
        menuView.setMenuItems(List.of(createGameLabel, findGameLabel));
        menuView.chooseMenu(activeMenuItem);
        menuView.addKeyHandler(keyboardEventHandler);
    }

    public class KeyboardEventHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.UP) {
                activeMenuItem = (activeMenuItem == 0) ? 1 : 0;
                menuView.chooseMenu(activeMenuItem);
            } else if (keyEvent.getCode() == KeyCode.ENTER) {
                menuView.removeKeyHandler(keyboardEventHandler);
                if (activeMenuItem == 0) {
                    ViewFactory.transition(View.CREATE_GAME, menuView.getStage());
                } else if (activeMenuItem == 1) {
                    ViewFactory.transition(View.CONNECT_GAME, menuView.getStage());
                }
            }
        }
    }

}

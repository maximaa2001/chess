package com.maks.chess;

import com.maks.chess.view.MenuView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) {
        MenuView menuView = new MenuView(stage);
        menuView.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
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
//        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("menu.fxml"));
//        Parent root = fxmlLoader.load();
//        Scene scene = new Scene(root, AppConstant.APP_WIDTH, AppConstant.APP_HEIGHT);
//        stage.setTitle("Chess");
//        stage.setScene(scene);
//        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
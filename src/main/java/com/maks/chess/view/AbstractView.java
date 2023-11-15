package com.maks.chess.view;

import com.maks.chess.MainApplication;
import com.maks.chess.constant.AppConstant;
import com.maks.chess.controller.Controller;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public abstract class AbstractView {
    protected final Stage stage;

    private static final Map<Class<? extends Labeled>, Integer> elementsToFontSize = Map.ofEntries(
            Map.entry(Label.class, 45),
            Map.entry(Button.class, 12)
    );

    public AbstractView(Stage stage) {
        this.stage = stage;
        stage.setResizable(false);
        FXMLLoader fxmlLoader = getFxmlLoader();
        Controller controller = getController();
        fxmlLoader.setController(controller);
        try {
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addKeyHandler(EventHandler<KeyEvent> handler) {
        stage.addEventHandler(KeyEvent.KEY_PRESSED, handler);
    }

    public void removeKeyHandler(EventHandler<KeyEvent> handler) {
        stage.removeEventHandler(KeyEvent.KEY_PRESSED, handler);
    }

    protected void addFont(List<Labeled> labels) {
        labels.forEach(e -> {
            Font font = Font.loadFont(MainApplication.class.getResourceAsStream(AppConstant.FONT_PATH), elementsToFontSize.get(e.getClass()));
            e.setFont(font);
        });
    }

    public Stage getStage() {
        return stage;
    }

    public void show() {
        stage.show();
    }

    private FXMLLoader getFxmlLoader() {
        return new FXMLLoader(getFxmlUrl());
    }

    protected abstract URL getFxmlUrl();

    protected abstract Controller getController();


}

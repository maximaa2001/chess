package com.maks.chess.controller;

import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.ChessModel;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.data.DataStore;
import com.maks.chess.model.data.DefaultDataStore;
import com.maks.chess.model.figure.Figure;
import com.maks.chess.service.factory.sprite.ImageViewCreator;
import com.maks.chess.view.BoardView;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class BoardController implements Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BorderPane borderPane;

    @FXML
    private GridPane gridPane;
    private final BoardView boardView;
    private final DataStore dataStore = DefaultDataStore.getDataStore();

    private ChessModel chessModel;

    public BoardController(BoardView boardView) {
        this.boardView = boardView;
    }

    @FXML
    void initialize() {
        boardView.initBoard(gridPane);
        chessModel = new ChessModel(dataStore.getColor(), (dataStore.getColor().equals(GamerColor.WHITE)) ? GamerColor.BLACK : GamerColor.WHITE);
        renderImages();
    }

    private void renderImages() {
        Map<Figure, Coordinate> remainingFigures = chessModel.getRemainingFigures();
        for (Map.Entry<Figure, Coordinate> next : remainingFigures.entrySet()) {
            ImageView imageView = ImageViewCreator.createImageViewResolver(next.getKey().getType(), next.getKey().getColor());
            boardView.placeOn(imageView, next.getValue());
        }
    }

}

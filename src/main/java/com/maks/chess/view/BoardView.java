package com.maks.chess.view;

import com.maks.chess.MainApplication;
import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.ViewConstant;
import com.maks.chess.controller.BoardController;
import com.maks.chess.controller.Controller;
import com.maks.chess.model.Coordinate;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;

public class BoardView extends AbstractView {
    private static final URL FXML = MainApplication.class.getResource(ViewConstant.BOARD);
    private static final String LIGHT_COLOR = "#FFFFFF";
    private static final String DARK_COLOR = "#5b5b5b";

    private GridPane gridPane;
    private StackPane[][] cells;

    public BoardView(Stage stage) {
        super(stage);
    }

    public void initBoard(GridPane gridPane) {
        this.gridPane = gridPane;
        this.cells = initBoard();
    }

    public void placeOn(ImageView imageView, Coordinate coordinate) {
        placeOn(imageView, coordinate.getRow(), coordinate.getColumn());
    }

    public void placeOn(ImageView imageView, int row, int column) {
        cells[row][column].getChildren().add(imageView);
    }

    private StackPane[][] initBoard() {
        final int BOARD_SIDE_SIZE = AppConstant.BOARD_SIDE_SIZE;
        StackPane[][] stackPanes = new StackPane[BOARD_SIDE_SIZE][BOARD_SIDE_SIZE];
        for (int i = 0; i < stackPanes.length; i++) {
            String currentColor = (i % 2 == 0) ? LIGHT_COLOR : DARK_COLOR;
            for (int j = 0; j < stackPanes[i].length; j++) {
                StackPane stackPane = new StackPane(new Rectangle(100, 100, Paint.valueOf(currentColor)));
                stackPanes[i][j] = stackPane;
                gridPane.add(stackPane, j, i);
                currentColor = (currentColor.equals(LIGHT_COLOR)) ? DARK_COLOR : LIGHT_COLOR;
            }
        }
        return stackPanes;
    }

    @Override
    protected URL getFxmlUrl() {
        return FXML;
    }

    @Override
    protected Controller getController() {
        return new BoardController(this);
    }
}

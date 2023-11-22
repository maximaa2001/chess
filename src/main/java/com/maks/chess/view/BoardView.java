package com.maks.chess.view;

import com.maks.chess.MainApplication;
import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.ViewConstant;
import com.maks.chess.constant.define.FigureActivity;
import com.maks.chess.controller.BoardController;
import com.maks.chess.controller.Controller;
import com.maks.chess.model.Coordinate;
import com.maks.chess.util.AppUtils;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class BoardView extends AbstractView {
    private static final URL FXML = MainApplication.class.getResource(ViewConstant.BOARD);
    private static final String LIGHT_COLOR = "#FFFFFF";
    private static final String DARK_COLOR = "#5b5b5b";

    private GridPane gridPane;
    private StackPane[][] cells;

    public BoardView(Stage stage) {
        super(stage);
    }

    public void initBoard(GridPane gridPane, EventHandler<MouseEvent> rectangleMouseEventHandler) {
        this.gridPane = gridPane;
        this.cells = initBoard(rectangleMouseEventHandler);
    }

    public void placeOn(ImageView imageView, Coordinate coordinate) {
        placeOn(imageView, coordinate.getRow(), coordinate.getColumn());
    }

    public void placeOn(ImageView imageView, int row, int column) {
        cells[row][column].getChildren().add(imageView);
    }

    public void move(Coordinate from, Coordinate to) {
        ImageView imageView = resolveImageView(from.getRow(), from.getColumn());
        cells[from.getRow()][from.getColumn()].getChildren().remove(imageView);
        placeOn(imageView, to);
    }

    public void eat(Coordinate from, Coordinate to) {
        ImageView fromImage = resolveImageView(from.getRow(), from.getColumn());
        ImageView toImage = resolveImageView(to.getRow(), to.getColumn());
        cells[from.getRow()][from.getColumn()].getChildren().remove(fromImage);
        cells[to.getRow()][to.getColumn()].getChildren().remove(toImage);
        placeOn(fromImage, to);
    }


    public void makeActiveCells(Coordinate chooseCoordinate, List<Coordinate> moveCoordinates, List<Coordinate> eatCoordinate) {
        resetBoardToDefaultState();
        if (chooseCoordinate != null) {
            makeActiveCell(chooseCoordinate.getRow(), chooseCoordinate.getColumn(), FigureActivity.CHOOSE.getCellColor());
        }
        moveCoordinates.forEach(e -> makeActiveCell(e.getRow(), e.getColumn(), FigureActivity.MOVE.getCellColor()));
        eatCoordinate.forEach(e -> makeActiveCell(e.getRow(), e.getColumn(), FigureActivity.EAT.getCellColor()));
    }

    public Coordinate getCoordinate(ImageView imageView) {
        String imageViewId = imageView.getId();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                ObservableList<Node> children = cells[i][j].getChildren();
                Optional<Node> any = children.stream().filter(e -> e instanceof ImageView).findAny();
                if (any.isPresent() && any.get().getId().equals(imageViewId)) {
                    for (int k = 0; k < children.size(); k++) {
                        if (children.get(k) instanceof Rectangle rectangle) {
                            return AppUtils.resolverRectangleId(rectangle);
                        }
                    }
                }
            }
        }
        return null;
    }

    private void makeActiveCell(int row, int column, Paint paint) {
        Rectangle rectangle = resolveRectangle(row, column);
        if (rectangle != null) {
            rectangle.setFill(paint);
            rectangle.setStroke(Paint.valueOf("black"));
            rectangle.setStrokeWidth(2);
            rectangle.setStrokeType(StrokeType.INSIDE);
        }
    }

    private StackPane[][] initBoard(EventHandler<MouseEvent> rectangleMouseEventHandler) {
        final int BOARD_SIDE_SIZE = AppConstant.BOARD_SIDE_SIZE;
        StackPane[][] stackPanes = new StackPane[BOARD_SIDE_SIZE][BOARD_SIDE_SIZE];
        for (int i = 0; i < stackPanes.length; i++) {
            String currentColor = (i % 2 == 0) ? LIGHT_COLOR : DARK_COLOR;
            for (int j = 0; j < stackPanes[i].length; j++) {
                Rectangle rectangle = new Rectangle(100, 100, Paint.valueOf(currentColor));
                rectangle.setId(AppUtils.generateRectangleId(new Coordinate(i, j)));
                rectangle.addEventHandler(MouseEvent.MOUSE_CLICKED, rectangleMouseEventHandler);
                StackPane stackPane = new StackPane(rectangle);
                stackPanes[i][j] = stackPane;
                gridPane.add(stackPane, j, i);
                currentColor = (currentColor.equals(LIGHT_COLOR)) ? DARK_COLOR : LIGHT_COLOR;
            }
        }
        return stackPanes;
    }

    private void resetBoardToDefaultState() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                Rectangle rectangle = resolveRectangle(i, j);
                if (rectangle != null) {
                    rectangle.setFill(getDefaultCellPaint(i, j));
                    rectangle.setStroke(null);
                }
            }
        }
    }

    private Rectangle resolveRectangle(int row, int column) {
        StackPane stackPane = cells[row][column];
        return (Rectangle) stackPane.getChildren()
                .stream()
                .filter(e -> e instanceof Rectangle)
                .findAny()
                .orElse(null);
    }

    private ImageView resolveImageView(int row, int column) {
        StackPane stackPane = cells[row][column];
        return (ImageView) stackPane.getChildren()
                .stream()
                .filter(e -> e instanceof ImageView)
                .findAny()
                .orElse(null);
    }

    private Paint getDefaultCellPaint(int row, int column) {
        String currentColor = (row % 2 == 0) ? LIGHT_COLOR : DARK_COLOR;
        if (column != 0) {
            for (int i = 1; i <= column; i++) {
                currentColor = currentColor.equals(LIGHT_COLOR) ? DARK_COLOR : LIGHT_COLOR;
            }
        }
        return Paint.valueOf(currentColor);
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

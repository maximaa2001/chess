package com.maks.chess.view;

import com.maks.chess.MainApplication;
import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.ViewConstant;
import com.maks.chess.constant.define.FigureActivity;
import com.maks.chess.controller.BoardController;
import com.maks.chess.controller.Controller;
import com.maks.chess.model.Coordinate;
import com.maks.chess.service.transformer.SizeCellTransformer;
import com.maks.chess.service.transition.DefaultImageViewTransitionService;
import com.maks.chess.service.transition.ImageViewTransitionService;
import com.maks.chess.util.AppUtils;
import com.maks.chess.util.Timer;
import com.maks.chess.util.VoidSmth;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;

public class BoardView extends AbstractView implements Timer {
    private static final URL FXML = MainApplication.class.getResource(ViewConstant.BOARD);
    private static final String LIGHT_COLOR = "#FFFFFF";
    private static final String DARK_COLOR = "#5b5b5b";
    private static final ImageViewTransitionService imageViewTransitionService = new DefaultImageViewTransitionService(new SizeCellTransformer());

    private GridPane gridPane;
    private HBox hBox;
    private Label moveLabel;
    private Label timeLabel;
    private Button giveUpBtn;
    private Button menuBtn;
    private StackPane[][] cells;
    private Button activeButton;

    public BoardView(Stage stage) {
        super(stage);
    }

    public void initBoard(GridPane gridPane, HBox hBox, List<Label> labels, List<Button> buttons, EventHandler<MouseEvent> rectangleMouseEventHandler) {
        this.gridPane = gridPane;
        this.hBox = hBox;
        this.moveLabel = labels.get(0);
        this.timeLabel = labels.get(1);
        this.giveUpBtn = buttons.get(0);
        this.menuBtn = buttons.get(1);
        this.cells = initBoard(rectangleMouseEventHandler);
        menuBtn.setPrefWidth(giveUpBtn.getPrefWidth());
        menuBtn.setPrefHeight(giveUpBtn.getPrefHeight());
        menuBtn.setText("В главное меню");
        activeButton = giveUpBtn;
    }

    public void placeOn(ImageView imageView, Coordinate coordinate) {
        placeOn(imageView, coordinate.getRow(), coordinate.getColumn());
    }

    public void placeOn(ImageView imageView, int row, int column) {
        cells[row][column].getChildren().add(imageView);
    }

    public void move(Coordinate from, Coordinate to) {
        VoidSmth onFinishCallback = () -> defaultMove(from, to);
        invokeAnimationService(from, to, onFinishCallback);
    }

    public void eat(Coordinate from, Coordinate to) {
        VoidSmth onFinishCallback = () -> defaultEat(from, to);
        invokeAnimationService(from, to, onFinishCallback);
    }

    public void castling(Coordinate castleCoordinate, Coordinate oldKingCoordinate, Coordinate newKingCoordinate) {
        Coordinate tempCoordinate = new Coordinate(newKingCoordinate.getRow(), (newKingCoordinate.getColumn() > oldKingCoordinate.getColumn() ? newKingCoordinate.getColumn() + 1 : newKingCoordinate.getColumn() - 1));
        invokeAnimationService(castleCoordinate, tempCoordinate, () -> {
            defaultMove(castleCoordinate, tempCoordinate);
            invokeAnimationService(oldKingCoordinate, newKingCoordinate, () -> {
                defaultMove(oldKingCoordinate, newKingCoordinate);
                invokeAnimationService(tempCoordinate, oldKingCoordinate, () -> defaultMove(tempCoordinate, oldKingCoordinate));
            });
        });
    }

    public void moveAndPawnEvolved(Coordinate from, Coordinate to, ImageView newImageView) {
        VoidSmth onFinishCallback = () -> {
            defaultMove(from, to);
            changeOn(newImageView, to);
        };
        invokeAnimationService(from, to, onFinishCallback);
    }

    public void eatAndPawnEvolved(Coordinate from, Coordinate to, ImageView newImageView) {
        VoidSmth onFinishCallback = () -> {
            defaultEat(from, to);
            changeOn(newImageView, to);
        };
        invokeAnimationService(from, to, onFinishCallback);
    }

    public void makeActiveCells(Coordinate chooseCoordinate, List<Coordinate> moveCoordinates, List<Coordinate> eatCoordinate, Coordinate castlingCoordinate) {
        resetBoardToDefaultState();
        if (chooseCoordinate != null) {
            makeActiveCell(chooseCoordinate.getRow(), chooseCoordinate.getColumn(), FigureActivity.CHOOSE.getCellColor());
        }
        moveCoordinates.forEach(e -> makeActiveCell(e.getRow(), e.getColumn(), FigureActivity.MOVE.getCellColor()));
        eatCoordinate.forEach(e -> makeActiveCell(e.getRow(), e.getColumn(), FigureActivity.EAT.getCellColor()));
        if (castlingCoordinate != null) {
            makeActiveCell(castlingCoordinate.getRow(), castlingCoordinate.getColumn(), FigureActivity.CASTLING.getCellColor());
        }
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

    public void replaceButton() {
        AppUtils.executeGui(() -> {
            hBox.getChildren().remove(activeButton);
            activeButton = (activeButton == giveUpBtn) ? menuBtn : giveUpBtn;
            hBox.getChildren().add(activeButton);
        });
    }

    @Override
    public void updateMessage(String message) {
        AppUtils.executeGui(() -> moveLabel.setText(message));
    }

    @Override
    public void updateTime(Integer time) {
        AppUtils.executeGui(() -> timeLabel.setText(String.valueOf(time)));
    }

    private ImageView takeFrom(Coordinate from) {
        ImageView fromImage = resolveImageView(from.getRow(), from.getColumn());
        cells[from.getRow()][from.getColumn()].getChildren().remove(fromImage);
        return fromImage;
    }

    private void defaultMove(Coordinate from, Coordinate to) {
        ImageView fromImage = takeFrom(from);
        placeOn(fromImage, to);
    }

    private void defaultEat(Coordinate from, Coordinate to) {
        ImageView fromImage = takeFrom(from);
        takeFrom(to);
        placeOn(fromImage, to);
    }

    private void changeOn(ImageView newImageView, Coordinate from) {
        takeFrom(from);
        placeOn(newImageView, from);
    }

    private void invokeAnimationService(Coordinate from, Coordinate to, VoidSmth onFinishCallback) {
        cells[from.getRow()][from.getColumn()].toFront();
        ImageView imageView = resolveImageView(from.getRow(), from.getColumn());
        imageViewTransitionService.transition(imageView, from, to, onFinishCallback);
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

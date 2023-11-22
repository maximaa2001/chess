package com.maks.chess.controller;

import com.maks.chess.constant.define.FigureActivity;
import com.maks.chess.constant.define.GameState;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.figure.Figure;
import com.maks.chess.service.GameLogic;
import com.maks.chess.service.factory.sprite.ImageViewCreator;
import com.maks.chess.service.thread.WaitEnemyMoveThread;
import com.maks.chess.service.transformer.CoordinateTransformer;
import com.maks.chess.service.transformer.ToEnemyCoordinateTransformer;
import com.maks.chess.util.AppUtils;
import com.maks.chess.view.BoardView;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class BoardController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private BorderPane borderPane;

    @FXML
    private GridPane gridPane;
    private final BoardView boardView;
    private final ImageMouseEventHandler imageMouseEventHandler = new ImageMouseEventHandler();
    private final RectangleMouseEventHandler rectangleMouseEventHandler = new RectangleMouseEventHandler();
    private final KeyboardEventHandler keyboardEventHandler = new KeyboardEventHandler();
    private GameLogic gameLogic;


    public BoardController(BoardView boardView) {
        this.boardView = boardView;
    }

    @FXML
    void initialize() {
        boardView.initBoard(gridPane, rectangleMouseEventHandler);
        boardView.addKeyHandler(keyboardEventHandler);
        gameLogic = new GameLogic();
        renderImages();
        if (gameLogic.getGameState().equals(GameState.WAIT_FOR_ENEMY)) {
            waitEnemyMove();
        }
    }

    private void renderImages() {
        Map<Figure, Coordinate> remainingFigures = gameLogic.getFiguresOnBoard();
        for (Map.Entry<Figure, Coordinate> next : remainingFigures.entrySet()) {
            Figure key = next.getKey();
            Coordinate value = next.getValue();
            ImageView imageView = ImageViewCreator.createImageView(key.getType(), key.getColor());
            imageView.setId(AppUtils.generateImageViewId(value));
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, imageMouseEventHandler);
            boardView.placeOn(imageView, value);
        }
    }

    private void clearActiveCoordinates() {
        gameLogic.getActivity2Coordinate().clear();
        boardView.makeActiveCells(null, List.of(), List.of());
    }

    private void waitEnemyMove() {
        new WaitEnemyMoveThread(gameLogic.getSocketWrapper(), (moveDto) -> {
            CoordinateTransformer coordinateTransformer = new ToEnemyCoordinateTransformer();
            Coordinate transformedFrom = coordinateTransformer.transform(moveDto.getFrom());
            Coordinate transformedTo = coordinateTransformer.transform(moveDto.getTo());
            gameLogic.readEnemyMove(transformedFrom, transformedTo);
            Platform.runLater(() -> {
                if (moveDto.getActivity().equals(FigureActivity.MOVE)) {
                    boardView.move(transformedFrom, transformedTo);
                } else if (moveDto.getActivity().equals(FigureActivity.EAT)) {
                    boardView.eat(transformedFrom, transformedTo);
                }
            });
            gameLogic.setGameState(GameState.CHOOSE_FIGURE);
        }).start();
    }

    private void handleMouseClick(Coordinate clickedCoordinate) {
        if (gameLogic.getGameState().equals(GameState.CHOOSE_FIGURE)) {
            logger.debug("clicked coordinate {}", clickedCoordinate);
            if (gameLogic.canMoveThisFigure(clickedCoordinate)) {
                Map<FigureActivity, List<Coordinate>> figureActivityListMap = gameLogic.accessibleMovesAllTypes(clickedCoordinate);
                logger.debug("{}", figureActivityListMap);
                boardView.makeActiveCells(
                        figureActivityListMap.get(FigureActivity.CHOOSE).get(0),
                        figureActivityListMap.get(FigureActivity.MOVE),
                        figureActivityListMap.get(FigureActivity.EAT)
                );
                gameLogic.setGameState(GameState.CHOOSE_CELL);
            }
        } else if (gameLogic.getGameState().equals(GameState.CHOOSE_CELL)) {
            Map<FigureActivity, List<Coordinate>> activity2Coordinate = gameLogic.getActivity2Coordinate();
            if (activity2Coordinate != null && activity2Coordinate.get(FigureActivity.MOVE).contains(clickedCoordinate)) {
                Coordinate from = activity2Coordinate.get(FigureActivity.CHOOSE).get(0);
                gameLogic.move(from, clickedCoordinate);
                boardView.move(from, clickedCoordinate);
                clearActiveCoordinates();
                gameLogic.setGameState(GameState.WAIT_FOR_ENEMY);
                waitEnemyMove();
            } else if (activity2Coordinate != null && activity2Coordinate.get(FigureActivity.EAT).contains(clickedCoordinate)) {
                Coordinate from = activity2Coordinate.get(FigureActivity.CHOOSE).get(0);
                gameLogic.eat(from, clickedCoordinate);
                boardView.eat(from, clickedCoordinate);
                clearActiveCoordinates();
                gameLogic.setGameState(GameState.WAIT_FOR_ENEMY);
                waitEnemyMove();
            } else if(gameLogic.canMoveThisFigure(clickedCoordinate)) {
                clearActiveCoordinates();
                Map<FigureActivity, List<Coordinate>> figureActivityListMap = gameLogic.accessibleMovesAllTypes(clickedCoordinate);
                boardView.makeActiveCells(
                        figureActivityListMap.get(FigureActivity.CHOOSE).get(0),
                        figureActivityListMap.get(FigureActivity.MOVE),
                        figureActivityListMap.get(FigureActivity.EAT)
                );
            }
        }
    }

    private class ImageMouseEventHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            ImageView imageView = (ImageView) mouseEvent.getSource();
            Coordinate coordinate = boardView.getCoordinate(imageView);
            handleMouseClick(coordinate);
        }

    }

    private class RectangleMouseEventHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Rectangle rectangle = (Rectangle) mouseEvent.getSource();
            handleMouseClick(AppUtils.resolverRectangleId(rectangle));
        }
    }


    private class KeyboardEventHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (gameLogic.getGameState().equals(GameState.CHOOSE_CELL)) {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    clearActiveCoordinates();
                    gameLogic.setGameState(GameState.CHOOSE_FIGURE);
                }
            }
        }
    }
}

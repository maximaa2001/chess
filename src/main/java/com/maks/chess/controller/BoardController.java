package com.maks.chess.controller;

import com.maks.chess.constant.define.FigureActivity;
import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GameState;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.data.DataStoreFactory;
import com.maks.chess.model.figure.Figure;
import com.maks.chess.model.figure.Pawn;
import com.maks.chess.service.GameLogic;
import com.maks.chess.service.factory.figure.FigureFactory;
import com.maks.chess.service.factory.figure.FigureFactoryCreator;
import com.maks.chess.service.factory.sprite.ImageViewCreator;
import com.maks.chess.service.game_state_logger.LabelGameStateLogger;
import com.maks.chess.service.game_state_logger.manager.DefaultGameLoggerManager;
import com.maks.chess.service.game_state_logger.reverser.GameStateLogReverser;
import com.maks.chess.service.thread.WaitEnemyMoveThread;
import com.maks.chess.service.transformer.CoordinateTransformer;
import com.maks.chess.service.transformer.ToEnemyCoordinateTransformer;
import com.maks.chess.util.AppUtils;
import com.maks.chess.view.BoardView;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
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
    private VBox scrollContent;

    @FXML
    private GridPane gridPane;
    private final BoardView boardView;
    private final ImageViewMouseEventHandler imageViewMouseEventHandler = new ImageViewMouseEventHandler();
    private final RectangleMouseEventHandler rectangleMouseEventHandler = new RectangleMouseEventHandler();
    private final KeyboardEventHandler keyboardEventHandler = new KeyboardEventHandler();
    private GameLogic gameLogic;
    private static int a = 0;


    public BoardController(BoardView boardView) {
        this.boardView = boardView;
    }

    @FXML
    void initialize() {
        boardView.initBoard(gridPane, rectangleMouseEventHandler);
        boardView.addKeyHandler(keyboardEventHandler);
        gameLogic = new GameLogic(new DefaultGameLoggerManager(List.of(new LabelGameStateLogger(scrollContent)), new GameStateLogReverser()));
        initBoardViewByImages();
        if (gameLogic.getGameState().equals(GameState.WAIT_FOR_ENEMY)) {
            waitEnemyMove();
        }
        scrollContent.setOnMouseClicked(e -> {
            scrollContent.getChildren().add(new Label(String.valueOf(++a)));
        });
    }

    private void initBoardViewByImages() {
        Map<Coordinate, Figure> remainingFigures = gameLogic.getFiguresOnBoard();
        for (Map.Entry<Coordinate, Figure> next : remainingFigures.entrySet()) {
            Coordinate coordinate = next.getKey();
            Figure figure = next.getValue();
            ImageView imageView = createImageView(figure.getType(), figure.getColor(), coordinate);
            boardView.placeOn(imageView, coordinate);
        }
    }

    private ImageView createImageView(FigureType type, GamerColor color, Coordinate coordinate) {
        ImageView imageView = ImageViewCreator.createImageView(type, color);
        imageView.setId(AppUtils.generateImageViewId(coordinate));
        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, imageViewMouseEventHandler);
        return imageView;
    }

    private void clearActiveCoordinates() {
        gameLogic.getActivity2Coordinate().clear();
        boardView.makeActiveCells(null, List.of(), List.of());
    }

    private void makeMove(Coordinate from, Coordinate to) {
        Figure movedFigure = gameLogic.move(from, to);
        String moveLog = gameLogic.getGameLoggerManager().createMoveLog(from, to);
        if (to.getRow() == 0 && movedFigure instanceof Pawn) {
            movedFigure = gameLogic.pawnAtEndBoard(to);
            boardView.move(from, to);
            boardView.changeOn(createImageView(movedFigure.getType(), movedFigure.getColor(), to), to);
            String pawnEvolvedLog = gameLogic.getGameLoggerManager().createPawnEvolvedLog(movedFigure.getType());
            gameLogic.sendToGamer(from, to, FigureActivity.MOVE, movedFigure.getType(), new ArrayList<>(List.of(moveLog, pawnEvolvedLog)));
        } else {
            boardView.move(from, to);
            gameLogic.sendToGamer(from, to, FigureActivity.MOVE, new ArrayList<>(List.of(moveLog)));
        }
    }

    private void makeEat(Coordinate from, Coordinate to) {
        Figure movedFigure = gameLogic.eat(from, to);
        String eatLog = gameLogic.getGameLoggerManager().createEatLog(from, to);
        if (to.getRow() == 0 && movedFigure instanceof Pawn) {
            movedFigure = gameLogic.pawnAtEndBoard(to);
            boardView.eat(from, to);
            boardView.changeOn(createImageView(movedFigure.getType(), movedFigure.getColor(), to), to);
            String pawnEvolvedLog = gameLogic.getGameLoggerManager().createPawnEvolvedLog(movedFigure.getType());
            gameLogic.sendToGamer(from, to, FigureActivity.EAT, movedFigure.getType(), new ArrayList<>(List.of(eatLog, pawnEvolvedLog)));
        } else {
            boardView.eat(from, to);
            gameLogic.sendToGamer(from, to, FigureActivity.EAT, new ArrayList<>(List.of(eatLog)));
        }
    }

    private void waitEnemyMove() {
        new WaitEnemyMoveThread(gameLogic.getSocketWrapper(), (moveDto) -> {
            logger.debug("get by socket {}", moveDto);
            AppUtils.executeGui(() -> moveDto.getLogs().forEach(e -> gameLogic.getGameLoggerManager().log(e)));
            CoordinateTransformer coordinateTransformer = new ToEnemyCoordinateTransformer();
            Coordinate transformedFrom = coordinateTransformer.transform(moveDto.getFrom());
            Coordinate transformedTo = coordinateTransformer.transform(moveDto.getTo());
            gameLogic.move(transformedFrom, transformedTo);
            Platform.runLater(() -> {
                if (moveDto.getActivity().equals(FigureActivity.MOVE)) {
                    boardView.move(transformedFrom, transformedTo);
                } else if (moveDto.getActivity().equals(FigureActivity.EAT)) {
                    boardView.eat(transformedFrom, transformedTo);
                }
            });
            if (moveDto.getPawnEvolutionDto() != null) {
                FigureFactory figureFactory = FigureFactoryCreator.createFigureFactory(gameLogic.getEnemyColor());
                Figure figure = figureFactory.createFigure(moveDto.getPawnEvolutionDto().getType(), transformedTo);
                gameLogic.addFigure(figure, transformedTo);
                Platform.runLater(() -> boardView.changeOn(createImageView(moveDto.getPawnEvolutionDto().getType(), gameLogic.getEnemyColor(), transformedTo), transformedTo));
            }
            DataStoreFactory.getDataStore().setKingInDanger(moveDto.getKingInDanger());
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
            if (activity2Coordinate != null) {
                List<Coordinate> coordinates2Moves = activity2Coordinate.get(FigureActivity.MOVE);
                List<Coordinate> coordinates2Eat = activity2Coordinate.get(FigureActivity.EAT);
                if (coordinates2Moves.contains(clickedCoordinate) || coordinates2Eat.contains(clickedCoordinate)) {
                    Coordinate from = activity2Coordinate.get(FigureActivity.CHOOSE).get(0);
                    boolean isPossibleMove = gameLogic.imagineMove(from, clickedCoordinate);
                    if (isPossibleMove) {
                        if (coordinates2Moves.contains(clickedCoordinate)) {
                            makeMove(from, clickedCoordinate);
                        } else {
                            makeEat(from, clickedCoordinate);
                        }
                        clearActiveCoordinates();
                        gameLogic.setGameState(GameState.WAIT_FOR_ENEMY);
                        logger.debug("move from {} to {}", from, clickedCoordinate);
                        waitEnemyMove();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Вы не можете сделать этот ход");
                        alert.setContentText("Ваш король будет находится под угрозой");
                        alert.showAndWait();
                    }
                } else if (gameLogic.canMoveThisFigure(clickedCoordinate)) {
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
    }

    private class ImageViewMouseEventHandler implements EventHandler<MouseEvent> {
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

package com.maks.chess.controller;

import com.maks.chess.constant.define.*;
import com.maks.chess.model.Castling;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.Move;
import com.maks.chess.model.PawnEvolution;
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
import com.maks.chess.service.thread.TimerThread;
import com.maks.chess.service.thread.WaitEnemyMoveThread;
import com.maks.chess.service.transformer.CoordinateTransformer;
import com.maks.chess.service.transformer.ToEnemyCoordinateTransformer;
import com.maks.chess.util.AppUtils;
import com.maks.chess.view.BoardView;
import com.maks.chess.view.View;
import com.maks.chess.view.ViewFactory;
import com.maks.chess.view.dialog.GiveUpAlert;
import com.maks.chess.view.dialog.KingInDangerAlert;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

public class BoardController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private VBox scrollContent;

    @FXML
    private HBox topHbox;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label moveLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Button giveUpBtn;

    private final Button menuBtn = new Button();

    private final BoardView boardView;
    private final ImageViewMouseEventHandler imageViewMouseEventHandler = new ImageViewMouseEventHandler();
    private final RectangleMouseEventHandler rectangleMouseEventHandler = new RectangleMouseEventHandler();
    private final ButtonEventHandler buttonEventHandler = new ButtonEventHandler();
    private final KeyboardEventHandler keyboardEventHandler = new KeyboardEventHandler();
    private GameLogic gameLogic;
    private TimerThread timerThread;


    public BoardController(BoardView boardView) {
        this.boardView = boardView;
    }

    @FXML
    void initialize() {
        boardView.initBoard(gridPane, topHbox, List.of(moveLabel, timeLabel), List.of(giveUpBtn, menuBtn), rectangleMouseEventHandler);
        giveUpBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, buttonEventHandler);
        menuBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, buttonEventHandler);
        boardView.addKeyHandler(keyboardEventHandler);
        gameLogic = new GameLogic(new DefaultGameLoggerManager(List.of(new LabelGameStateLogger(scrollContent)), new GameStateLogReverser()));
        initBoardViewByImages();
        timerThread = new TimerThread(boardView, gameLogic, this::losing);
        timerThread.start();
        if (gameLogic.getGameState().equals(GameState.WAIT_FOR_ENEMY)) {
            waitEnemyMove();
        }
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
        boardView.makeActiveCells(null, List.of(), List.of(), null);
    }

    private void clearGlobalData() {
        DataStoreFactory.getDataStore().setColor(null);
        DataStoreFactory.getDataStore().setSocket(null);
    }

    private void makeMove(Coordinate from, Coordinate to) {
        Figure movedFigure = gameLogic.move(from, to);
        String moveLog = gameLogic.getGameLoggerManager().createMoveLog(from, to);
        if (to.getRow() == 0 && movedFigure instanceof Pawn) {
            movedFigure = gameLogic.pawnAtEndBoard(to);
            boardView.moveAndPawnEvolved(from, to, createImageView(movedFigure.getType(), movedFigure.getColor(), to));
            String pawnEvolvedLog = gameLogic.getGameLoggerManager().createPawnEvolvedLog(movedFigure.getType());
            gameLogic.sendToGamer(from, to, FigureActivity.MOVE, movedFigure.getType(), new ArrayList<>(List.of(moveLog, pawnEvolvedLog)));
            logger.debug("pawn evolved");
        } else {
            boardView.move(from, to);
            gameLogic.sendToGamer(from, to, FigureActivity.MOVE, new ArrayList<>(List.of(moveLog)));
            logger.debug("move from {} to {}", from, to);
        }
        timerThread.resetTimer();
    }

    private void makeEat(Coordinate from, Coordinate to) {
        Figure movedFigure = gameLogic.eat(from, to);
        String eatLog = gameLogic.getGameLoggerManager().createEatLog(from, to);
        if (to.getRow() == 0 && movedFigure instanceof Pawn) {
            movedFigure = gameLogic.pawnAtEndBoard(to);
            boardView.eatAndPawnEvolved(from, to, createImageView(movedFigure.getType(), movedFigure.getColor(), to));
            String pawnEvolvedLog = gameLogic.getGameLoggerManager().createPawnEvolvedLog(movedFigure.getType());
            gameLogic.sendToGamer(from, to, FigureActivity.EAT, movedFigure.getType(), new ArrayList<>(List.of(eatLog, pawnEvolvedLog)));
            logger.debug("pawn evolved");
        } else {
            boardView.eat(from, to);
            gameLogic.sendToGamer(from, to, FigureActivity.EAT, new ArrayList<>(List.of(eatLog)));
            logger.debug("eat from {} to {}", from, to);
        }
        timerThread.resetTimer();
    }

    private void makeCastling(Coordinate castleCoordinate, Coordinate oldKingCoordinate, Coordinate newKingCoordinate) {
        gameLogic.move(oldKingCoordinate, newKingCoordinate);
        gameLogic.move(castleCoordinate, oldKingCoordinate);
        String castlingLog = gameLogic.getGameLoggerManager().createCastlingLog();
        boardView.castling(castleCoordinate, oldKingCoordinate, newKingCoordinate);
        gameLogic.sendToGamer(castleCoordinate, oldKingCoordinate, newKingCoordinate, new ArrayList<>(List.of(castlingLog)));
        timerThread.resetTimer();
    }

    private void waitEnemyMove() {
        new WaitEnemyMoveThread(gameLogic.getSocketWrapper(), (moveDto) -> {
            AppUtils.executeGui(() -> moveDto.getLogs().forEach(e -> gameLogic.getGameLoggerManager().log(e)));
            if (moveDto.getEndGame()) {
                logger.debug("end game {}", moveDto);
                gameLogic.setGameState(GameState.END_GAME);
                timerThread.stopTimer();
                boardView.replaceButton();
                gameLogic.getSocketWrapper().closeConnection();
                clearGlobalData();
            } else {
                CoordinateTransformer coordinateTransformer = new ToEnemyCoordinateTransformer();
                if (moveDto.getMove() != null || moveDto.getPawnEvolution() != null) {
                    Coordinate from;
                    Coordinate to;
                    FigureActivity activity;
                    if (moveDto.getMove() != null) {
                        Move move = moveDto.getMove();
                        logger.debug("move {}", move);
                        from = move.getFrom();
                        to = move.getTo();
                        activity = move.getActivity();
                    } else {
                        PawnEvolution pawnEvolutionDto = moveDto.getPawnEvolution();
                        logger.debug("pawnEvolution {}", pawnEvolutionDto);
                        from = pawnEvolutionDto.getFrom();
                        to = pawnEvolutionDto.getTo();
                        activity = pawnEvolutionDto.getActivity();
                    }
                    Coordinate transformedFrom = coordinateTransformer.transform(from);
                    Coordinate transformedTo = coordinateTransformer.transform(to);
                    if (activity.equals(FigureActivity.MOVE)) {
                        gameLogic.move(transformedFrom, transformedTo);
                    } else if (activity.equals(FigureActivity.EAT)) {
                        gameLogic.eat(transformedFrom, transformedTo);
                    }
                    if (moveDto.getPawnEvolution() != null) {
                        PawnEvolution pawnEvolutionDto = moveDto.getPawnEvolution();
                        FigureFactory figureFactory = FigureFactoryCreator.createFigureFactory(gameLogic.getEnemyColor());
                        Figure figure = figureFactory.createFigure(pawnEvolutionDto.getType(), transformedTo);
                        gameLogic.addFigure(figure, transformedTo);
                        AppUtils.executeGui(() -> {
                            if (pawnEvolutionDto.getActivity().equals(FigureActivity.MOVE)) {
                                boardView.moveAndPawnEvolved(transformedFrom, transformedTo, createImageView(pawnEvolutionDto.getType(), gameLogic.getEnemyColor(), transformedTo));
                            } else if (pawnEvolutionDto.getActivity().equals(FigureActivity.EAT)) {
                                boardView.eatAndPawnEvolved(transformedFrom, transformedTo, createImageView(pawnEvolutionDto.getType(), gameLogic.getEnemyColor(), transformedTo));
                            }
                        });
                    } else {
                        AppUtils.executeGui(() -> {
                            if (activity.equals(FigureActivity.MOVE)) {
                                boardView.move(transformedFrom, transformedTo);
                            } else if (activity.equals(FigureActivity.EAT)) {
                                boardView.eat(transformedFrom, transformedTo);
                            }
                        });
                    }
                } else if (moveDto.getCastling() != null) {
                    Castling castling = moveDto.getCastling();
                    logger.debug("castling {}", castling);
                    Coordinate transformedCastleCoordinate = coordinateTransformer.transform(castling.getCastleCoordinate());
                    Coordinate transformedOldKingCoordinate = coordinateTransformer.transform(castling.getOldKingCoordinate());
                    Coordinate transformedNewKingCoordinate = coordinateTransformer.transform(castling.getNewKingCoordinate());
                    gameLogic.move(transformedOldKingCoordinate, transformedNewKingCoordinate);
                    gameLogic.move(transformedCastleCoordinate, transformedOldKingCoordinate);
                    AppUtils.executeGui(() -> boardView.castling(transformedCastleCoordinate, transformedOldKingCoordinate, transformedNewKingCoordinate));
                }
                if (moveDto.getKingInDanger()) {
                    boolean isMat = gameLogic.checkMat();
                    if (isMat) {
                        losing(LosingType.MAT);
                        gameLogic.setGameState(GameState.END_GAME);
                    } else {
                        gameLogic.setGameState(GameState.CHOOSE_FIGURE);
                        timerThread.resetTimer();
                    }
                } else {
                    gameLogic.setGameState(GameState.CHOOSE_FIGURE);
                    timerThread.resetTimer();
                }
            }
        }).start();
    }

    private void losing(LosingType type) {
        logger.debug("you are lose {}", type);
        boardView.replaceButton();
        gameLogic.sendToGamer(type);
        gameLogic.getSocketWrapper().closeConnection();
        clearGlobalData();
        logger.debug("cleared global data socket {}, color {}", DataStoreFactory.getDataStore().getSocket(), DataStoreFactory.getDataStore().getColor());
    }

    private void handleMouseClick(Coordinate clickedCoordinate) {
        if (gameLogic.getGameState().equals(GameState.CHOOSE_FIGURE)) {
            if (gameLogic.canMoveThisFigure(clickedCoordinate)) {
                Map<FigureActivity, List<Coordinate>> figureActivityListMap = gameLogic.accessibleMovesAllTypes(clickedCoordinate);
                boardView.makeActiveCells(
                        figureActivityListMap.get(FigureActivity.CHOOSE).get(0),
                        figureActivityListMap.get(FigureActivity.MOVE),
                        figureActivityListMap.get(FigureActivity.EAT),
                        figureActivityListMap.get(FigureActivity.CASTLING).isEmpty() ? null : figureActivityListMap.get(FigureActivity.CASTLING).get(0)
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
                        waitEnemyMove();
                    } else {
                        KingInDangerAlert kingInDangerAlert = new KingInDangerAlert();
                        kingInDangerAlert.show();
                    }
                } else if (activity2Coordinate.get(FigureActivity.CASTLING).contains(clickedCoordinate)) {
                    Coordinate from = activity2Coordinate.get(FigureActivity.CHOOSE).get(0);
                    Coordinate to = activity2Coordinate.get(FigureActivity.CASTLING).get(0);
                    Coordinate newKingCoordinate;
                    if (to.getColumn() > from.getColumn()) {
                        newKingCoordinate = new Coordinate(from.getRow(), to.getColumn() - 1);
                    } else {
                        newKingCoordinate = new Coordinate(from.getRow(), to.getColumn() + 1);
                    }
                    boolean isPossibleMove = gameLogic.imagineCastling(from, to, newKingCoordinate);
                    if (isPossibleMove) {
                        makeCastling(from, to, newKingCoordinate);
                        clearActiveCoordinates();
                        gameLogic.setGameState(GameState.WAIT_FOR_ENEMY);
                        waitEnemyMove();
                    } else {
                        KingInDangerAlert kingInDangerAlert = new KingInDangerAlert();
                        kingInDangerAlert.show();
                    }
                } else if (gameLogic.canMoveThisFigure(clickedCoordinate)) {
                    clearActiveCoordinates();
                    Map<FigureActivity, List<Coordinate>> figureActivityListMap = gameLogic.accessibleMovesAllTypes(clickedCoordinate);
                    boardView.makeActiveCells(
                            figureActivityListMap.get(FigureActivity.CHOOSE).get(0),
                            figureActivityListMap.get(FigureActivity.MOVE),
                            figureActivityListMap.get(FigureActivity.EAT),
                            figureActivityListMap.get(FigureActivity.CASTLING).isEmpty() ? null : figureActivityListMap.get(FigureActivity.CASTLING).get(0)
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

    private class ButtonEventHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            Button source = (Button) event.getSource();
            if (source == giveUpBtn) {
                GiveUpAlert giveUpAlert = new GiveUpAlert();
                Optional<ButtonType> type = giveUpAlert.show();
                type.ifPresent(e -> {
                    if (e == ButtonType.OK) {
                        timerThread.stopTimer();
                        losing(LosingType.GIVE_UP);
                    }
                });
            } else if (source == menuBtn) {
                ViewFactory.transition(View.MENU, boardView.getStage());
            }
        }
    }
}

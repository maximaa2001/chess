package com.maks.chess.service;

import com.maks.chess.constant.define.FigureActivity;
import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GameState;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.ChessModel;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.MoveDto;
import com.maks.chess.model.PawnEvolutionDto;
import com.maks.chess.model.data.DataStoreFactory;
import com.maks.chess.model.figure.Figure;
import com.maks.chess.model.figure.King;
import com.maks.chess.service.game_state_logger.manager.GameLoggerManager;
import com.maks.chess.service.initializer.FigureInitializer;
import com.maks.chess.service.initializer.PawnEvolutionFigureInitializer;
import com.maks.chess.service.transformer.CoordinateTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogic {
    private static final Logger logger = LoggerFactory.getLogger(GameLogic.class);
    private final Map<FigureActivity, List<Coordinate>> activity2Coordinate = new HashMap<>();
    private final ChessModel gameChessModel;
    private final SocketWrapper socketWrapper;
    private final GameLoggerManager gameLoggerManager;
    private GameState gameState;


    public GameLogic(GameLoggerManager gameLoggerManager) {
        this.gameChessModel = new ChessModel(getMyColor(), getEnemyColor());
        this.socketWrapper = new SocketWrapper();
        this.gameLoggerManager = gameLoggerManager;
        this.gameState = getMyColor().equals(GamerColor.WHITE) ? GameState.CHOOSE_FIGURE : GameState.WAIT_FOR_ENEMY;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Map<Coordinate, Figure> getFiguresOnBoard() {
        return gameChessModel.getRemainingFigures();
    }

    public GamerColor getMyColor() {
        return DataStoreFactory.getDataStore().getColor();
    }

    public GamerColor getEnemyColor() {
        GamerColor myColor = getMyColor();
        return myColor.equals(GamerColor.WHITE) ? GamerColor.BLACK : GamerColor.WHITE;
    }

    public boolean isEmptyCell(Coordinate coordinate) {
        Figure figure = gameChessModel.getByCoordinate(coordinate);
        return figure == null;
    }

    public boolean isMyFigure(Coordinate coordinate) {
        Figure figure = gameChessModel.getByCoordinate(coordinate);
        return figure.getColor().equals(getMyColor());
    }

    public boolean canMoveThisFigure(Coordinate coordinate) {
        return !isEmptyCell(coordinate) && isMyFigure(coordinate);
    }

    public Map<FigureActivity, List<Coordinate>> accessibleMovesAllTypes(Coordinate from) {
        activity2Coordinate.put(FigureActivity.MOVE, accessibleMoves(from));
        activity2Coordinate.put(FigureActivity.EAT, accessibleEatFigure(from));
        activity2Coordinate.put(FigureActivity.CHOOSE, List.of(from));
        return activity2Coordinate;
    }

    public Map<FigureActivity, List<Coordinate>> getActivity2Coordinate() {
        return activity2Coordinate;
    }

    private List<Coordinate> accessibleMoves(Coordinate from) {
        Figure chosenFigure = gameChessModel.getByCoordinate(from);
        return chosenFigure.checkMoves(gameChessModel, from);
    }

    private List<Coordinate> accessibleEatFigure(Coordinate from) {
        Figure chosenFigure = gameChessModel.getByCoordinate(from);
        return chosenFigure.checkPossibilityEat(gameChessModel, from, getEnemyColor());
    }

    public Figure move(Coordinate from, Coordinate to) {
        return gameChessModel.move(from, to);
    }

    public boolean imagineMove(Coordinate from, Coordinate to) {
        ChessModel copyChessModel = gameChessModel.reverseModel();
        CoordinateTransformer coordinateTransformer = copyChessModel.getCoordinateTransformer();
        copyChessModel.move(coordinateTransformer.transform(from), coordinateTransformer.transform(to));
        Coordinate myKingCoordinate = findMyKingCoordinate(copyChessModel);
        Map<Coordinate, Figure> remainingFigures = copyChessModel.getRemainingFigures();
        for (Map.Entry<Coordinate, Figure> entry : remainingFigures.entrySet()) {
            Figure figure = entry.getValue();
            if (figure.getColor().equals(getEnemyColor())) {
                List<Coordinate> coordinatesAtRisk = figure.checkPossibilityEat(copyChessModel, entry.getKey(), getMyColor());
                if (coordinatesAtRisk.contains(myKingCoordinate)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Figure eat(Coordinate from, Coordinate to) {
        return gameChessModel.move(from, to);
    }

    public void addFigure(Figure newFigure, Coordinate to) {
        gameChessModel.addFigure(newFigure, to);
    }

    public void sendToGamer(Coordinate from, Coordinate to, FigureActivity activity, List<String> logs) {
        sendToGamer(from, to, activity, null, logs);
    }

    public void sendToGamer(Coordinate from, Coordinate to, FigureActivity activity, FigureType type, List<String> logs) {
        boolean inDanger = checkEnemyKingInDanger(to);
        if (inDanger) {
            logs.add(gameLoggerManager.createKingInDangerLog());
        }
        socketWrapper.writeMove(new MoveDto(from, to, activity, inDanger, (type != null) ? new PawnEvolutionDto(type): null, logs));
    }

    public SocketWrapper getSocketWrapper() {
        return socketWrapper;
    }

    public GameLoggerManager getGameLoggerManager() {
        return gameLoggerManager;
    }

    public Figure pawnAtEndBoard(Coordinate to) {
        FigureInitializer figureInitializer = new PawnEvolutionFigureInitializer(to);
        List<Figure> figures = figureInitializer.createFigures(getMyColor());
        Figure newFigure = figures.get(0);
        logger.info("pawn evolved in {}", newFigure.getType());
        addFigure(newFigure, to);
        return newFigure;
    }

    private boolean checkEnemyKingInDanger(Coordinate to) {
        Coordinate enemyKingCoordinate = findEnemyKingCoordinate(gameChessModel);
        Figure figure = gameChessModel.getByCoordinate(to);
        List<Coordinate> possibleCoordinates = figure.checkPossibilityEat(gameChessModel, to, getEnemyColor());
        return possibleCoordinates.contains(enemyKingCoordinate);
    }

    private Coordinate findMyKingCoordinate(ChessModel currentChessModel) {
        return findKingCoordinate(currentChessModel, getMyColor());
    }

    private Coordinate findEnemyKingCoordinate(ChessModel currentChessModel) {
        return findKingCoordinate(currentChessModel, getEnemyColor());
    }

    private Coordinate findKingCoordinate(ChessModel currentChessModel, GamerColor color) {
        Map<Coordinate, Figure> remainingFigures = currentChessModel.getRemainingFigures();
        for (Map.Entry<Coordinate, Figure> entry : remainingFigures.entrySet()) {
            Figure key = entry.getValue();
            if (key instanceof King && key.getColor().equals(color)) {
                return entry.getKey();
            }
        }
        return null;
    }
}

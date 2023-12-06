package com.maks.chess.service;

import com.maks.chess.constant.AppConstant;
import com.maks.chess.constant.define.*;
import com.maks.chess.model.*;
import com.maks.chess.model.data.DataStoreFactory;
import com.maks.chess.model.figure.Castle;
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
        Coordinate castlingCoordinate = accessibleCastling(from);
        activity2Coordinate.put(FigureActivity.CASTLING, castlingCoordinate == null ? List.of() : List.of(castlingCoordinate));
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

    private Coordinate accessibleCastling(Coordinate from) {
        Figure chosenFigure = gameChessModel.getByCoordinate(from);
        final int row = from.getRow();
        if (chosenFigure instanceof Castle castle) {
            if (!castle.isMoved()) {
                Coordinate myKingCoordinate = findMyKingCoordinate(gameChessModel);
                Figure kingFigure = gameChessModel.getByCoordinate(myKingCoordinate);
                King king = (King) kingFigure;
                if (!king.isMoved() && !myKingInDanger()) {
                    if (castle.getStartCoordinate().getColumn() == 0) {
                        for (int i = 1; i < king.getStartCoordinate().getColumn(); i++) {
                            Figure figureByCoordinate = gameChessModel.getByCoordinate(new Coordinate(row, i));
                            if (figureByCoordinate != null) {
                                return null;
                            }
                        }
                    } else if (castle.getStartCoordinate().getColumn() == AppConstant.BOARD_SIDE_SIZE - 1) {
                        for (int i = castle.getStartCoordinate().getColumn(); i > 0; i--) {
                            Figure figureByCoordinate = gameChessModel.getByCoordinate(new Coordinate(row, i));
                            if (figureByCoordinate != null) {
                                return null;
                            }
                        }
                    }
                    return myKingCoordinate;
                }
            }
        }
        return null;
    }

    public Figure move(Coordinate from, Coordinate to) {
        Figure movedFigure = gameChessModel.move(from, to);
        if (movedFigure instanceof Castle castle) {
            castle.setMovedFlag();
        } else if (movedFigure instanceof King king) {
            king.setMovedFlag();
        }
        return movedFigure;
    }

    public boolean imagineMove(Coordinate from, Coordinate to) {
        ChessModel copyChessModel = gameChessModel.reverseModel();
        CoordinateTransformer coordinateTransformer = copyChessModel.getCoordinateTransformer();
        copyChessModel.move(coordinateTransformer.transform(from), coordinateTransformer.transform(to));
        boolean inDanger = myKingInDanger(copyChessModel);
        return !inDanger;
    }

    public boolean imagineCastling(Coordinate castleCoordinate, Coordinate kingCoordinate, Coordinate newKingCoordinate) {
        ChessModel copyChessModel = gameChessModel.reverseModel();
        CoordinateTransformer coordinateTransformer = copyChessModel.getCoordinateTransformer();
        copyChessModel.move(coordinateTransformer.transform(kingCoordinate), coordinateTransformer.transform(newKingCoordinate));
        copyChessModel.move(coordinateTransformer.transform(castleCoordinate), coordinateTransformer.transform(kingCoordinate));
        boolean inDanger = myKingInDanger(copyChessModel);
        return !inDanger;
    }

    public Figure eat(Coordinate from, Coordinate to) {
        Figure movedFigure = gameChessModel.move(from, to);
        if (movedFigure instanceof Castle castle) {
            castle.setMovedFlag();
        } else if (movedFigure instanceof King king) {
            king.setMovedFlag();
        }
        return movedFigure;
    }

    public void addFigure(Figure newFigure, Coordinate to) {
        gameChessModel.addFigure(newFigure, to);
    }

    public void sendToGamer(Coordinate from, Coordinate to, FigureActivity activity, List<String> logs) {
        boolean inDanger = checkEnemyKingInDangerAfterMove(to);
        if (inDanger) {
            logs.add(gameLoggerManager.createKingInDangerLog());
        }
        sendToGamer(new Move(from, to, activity), null, null, logs);
    }

    public void sendToGamer(Coordinate from, Coordinate to, FigureActivity activity, FigureType type, List<String> logs) {
        boolean inDanger = checkEnemyKingInDangerAfterMove(to);
        if (inDanger) {
            logs.add(gameLoggerManager.createKingInDangerLog());
        }
        sendToGamer(null, new PawnEvolution(from, to, activity, type), null, logs);
    }

    public void sendToGamer(Coordinate castleCoordinate, Coordinate oldKingCoordinate, Coordinate newKingCoordinate, List<String> logs) {
        boolean inDanger = enemyKingInDanger();
        if (inDanger) {
            logs.add(gameLoggerManager.createKingInDangerLog());
        }
        sendToGamer(null, null, new Castling(castleCoordinate, oldKingCoordinate, newKingCoordinate), logs);
    }

    private void sendToGamer(Move move, PawnEvolution pawnEvolution, Castling castling, List<String> logs) {
        socketWrapper.writeMove(new MoveDto(move, pawnEvolution, castling, logs, false));
    }

    public void sendToGamer(LosingType type) {
        String losingLog = gameLoggerManager.createLosingLog(type);
        socketWrapper.writeMove(new MoveDto(losingLog));
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

    private boolean checkEnemyKingInDangerAfterMove(Coordinate to) {
        Coordinate enemyKingCoordinate = findEnemyKingCoordinate(gameChessModel);
        Figure figure = gameChessModel.getByCoordinate(to);
        List<Coordinate> possibleCoordinates = figure.checkPossibilityEat(gameChessModel, to, getEnemyColor());
        return possibleCoordinates.contains(enemyKingCoordinate);
    }


    private boolean myKingInDanger() {
        return myKingInDanger(gameChessModel.reverseModel());
    }
    private boolean myKingInDanger(ChessModel reversedChessModel) {
        Coordinate myKingCoordinate = findMyKingCoordinate(reversedChessModel);
        return kingInDanger(reversedChessModel, myKingCoordinate);
    }

    private boolean enemyKingInDanger() {
        Coordinate enemyKingCoordinate = findEnemyKingCoordinate(gameChessModel);
        return kingInDanger(gameChessModel, enemyKingCoordinate);
    }

    private boolean kingInDanger(ChessModel chessModel, Coordinate kingCoordinate) {
        Map<Coordinate, Figure> remainingFigures = chessModel.getRemainingFigures();
        for (Map.Entry<Coordinate, Figure> entry : remainingFigures.entrySet()) {
            Figure figure = entry.getValue();
            if (figure.getColor().equals(getEnemyColor())) {
                List<Coordinate> coordinatesAtRisk = figure.checkPossibilityEat(chessModel, entry.getKey(), getMyColor());
                if (coordinatesAtRisk.contains(kingCoordinate)) {
                    return true;
                }
            }
        }
        return false;
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

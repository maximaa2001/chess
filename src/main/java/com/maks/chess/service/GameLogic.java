package com.maks.chess.service;

import com.maks.chess.constant.define.FigureActivity;
import com.maks.chess.constant.define.GameState;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.ChessModel;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.MoveDto;
import com.maks.chess.model.data.DataStoreFactory;
import com.maks.chess.model.figure.Figure;
import com.maks.chess.service.transformer.CoordinateTransformer;
import com.maks.chess.service.transformer.ToEnemyCoordinateTransformer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogic {
    private final ChessModel chessModel;
    private final SocketWrapper socketWrapper;
    private GameState gameState;
    private final Map<FigureActivity, List<Coordinate>> activity2Coordinate = new HashMap<>();

    public GameLogic() {
        this.chessModel = new ChessModel(getMyColor(), getEnemyColor());
        this.socketWrapper = new SocketWrapper();
        this.gameState = getMyColor().equals(GamerColor.WHITE) ? GameState.CHOOSE_FIGURE : GameState.WAIT_FOR_ENEMY;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Map<Figure, Coordinate> getFiguresOnBoard() {
        return chessModel.getRemainingFigures();
    }

    public GamerColor getMyColor() {
        return DataStoreFactory.getDataStore().getColor();
    }

    public GamerColor getEnemyColor() {
        GamerColor myColor = getMyColor();
        return myColor.equals(GamerColor.WHITE) ? GamerColor.BLACK : GamerColor.WHITE;
    }

    public boolean isEmptyCell(Coordinate coordinate) {
        Figure figure = chessModel.getByCoordinate(coordinate);
        return figure == null;
    }

    public boolean isMyFigure(Coordinate coordinate) {
        Figure figure = chessModel.getByCoordinate(coordinate);
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
        Figure chosenFigure = chessModel.getByCoordinate(from);
        return chosenFigure.checkMoves(chessModel, from);
    }

    private List<Coordinate> accessibleEatFigure(Coordinate from) {
        Figure chosenFigure = chessModel.getByCoordinate(from);
        return chosenFigure.checkPossibilityEat(chessModel, from);
    }

    public void move(Coordinate from, Coordinate to) {
        chessModel.move(from, to);
        socketWrapper.writeMove(new MoveDto(from, to, FigureActivity.MOVE));
    }

    public void eat(Coordinate from, Coordinate to) {
        chessModel.move(from, to);
        socketWrapper.writeMove(new MoveDto(from, to, FigureActivity.EAT));
    }

    public void readEnemyMove(Coordinate from, Coordinate to) {
        chessModel.move(from, to);
    }

    public SocketWrapper getSocketWrapper() {
        return socketWrapper;
    }
}

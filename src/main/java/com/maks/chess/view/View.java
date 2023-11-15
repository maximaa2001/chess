package com.maks.chess.view;

public enum View {
    MENU(MenuView.class),
    CREATE_GAME(CreateGameView.class),
    CONNECT_GAME(ConnectGameView.class),
    BOARD(BoardView.class);

    View(Class<? extends AbstractView> clazz) {
        this.clazz = clazz;
    }

    private final Class<? extends AbstractView> clazz;

    public Class<? extends AbstractView> getClazz() {
        return clazz;
    }
}

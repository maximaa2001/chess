package com.maks.chess.model.data;

public final class DataStoreFactory {
    public static DataStore getDataStore() {
        return DefaultDataStore.getDataStore();
    }
}

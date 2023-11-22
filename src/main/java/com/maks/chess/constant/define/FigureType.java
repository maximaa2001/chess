package com.maks.chess.constant.define;

import com.maks.chess.constant.AppConstant;
import com.maks.chess.model.Coordinate;

import java.util.List;
import java.util.stream.IntStream;

public enum FigureType {
    PAWN {
        @Override
        public List<Coordinate> getDefaultStartCoordinate() {
            final int row = AppConstant.BOARD_SIDE_SIZE - 2;
            return IntStream.range(0, AppConstant.BOARD_SIDE_SIZE)
                    .mapToObj(e -> new Coordinate(row, e))
                    .toList();
        }
    },
    CASTLE {
        @Override
        public List<Coordinate> getDefaultStartCoordinate() {
            final int row = AppConstant.BOARD_SIDE_SIZE - 1;
            return List.of(new Coordinate(row, 0), new Coordinate(row, AppConstant.BOARD_SIDE_SIZE - 1));
        }
    },
    KNIGHT {
        @Override
        public List<Coordinate> getDefaultStartCoordinate() {
            final int row = AppConstant.BOARD_SIDE_SIZE - 1;
            return List.of(new Coordinate(row, 1), new Coordinate(row, AppConstant.BOARD_SIDE_SIZE - 2));
        }
    },
    BISHOP {
        @Override
        public List<Coordinate> getDefaultStartCoordinate() {
            final int row = AppConstant.BOARD_SIDE_SIZE - 1;
            return List.of(new Coordinate(row, 2), new Coordinate(row, AppConstant.BOARD_SIDE_SIZE - 3));
        }
    },
    QUEEN {
        @Override
        public List<Coordinate> getDefaultStartCoordinate() {
            final int row = AppConstant.BOARD_SIDE_SIZE - 1;
            return List.of(new Coordinate(row, 3));
        }
    },
    KING {
        @Override
        public List<Coordinate> getDefaultStartCoordinate() {
            final int row = AppConstant.BOARD_SIDE_SIZE - 1;
            return List.of(new Coordinate(row, 4));
        }
    };

    public abstract List<Coordinate> getDefaultStartCoordinate();
}

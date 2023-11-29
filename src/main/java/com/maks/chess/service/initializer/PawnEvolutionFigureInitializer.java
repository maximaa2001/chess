package com.maks.chess.service.initializer;

import com.maks.chess.constant.define.FigureType;
import com.maks.chess.constant.define.GamerColor;
import com.maks.chess.model.Coordinate;
import com.maks.chess.model.figure.Figure;
import com.maks.chess.service.factory.figure.FigureFactory;
import com.maks.chess.service.factory.figure.FigureFactoryCreator;
import com.maks.chess.view.dialog.ChooseFigureDialog;

import java.util.List;
import java.util.Optional;

public class PawnEvolutionFigureInitializer implements FigureInitializer {
    private final Coordinate pawnCoordinate;

    public PawnEvolutionFigureInitializer(Coordinate pawnCoordinate) {
        this.pawnCoordinate = pawnCoordinate;
    }

    @Override
    public List<Figure> createFigures(GamerColor color) {
        ChooseFigureDialog dialog = new ChooseFigureDialog();
        Optional<String> show = dialog.show();
        while (show.isEmpty()) {
            show = dialog.show();
        }
        String figureName = show.get();
        FigureType figureType = dialog.of(figureName);
        FigureFactory figureFactory = FigureFactoryCreator.createFigureFactory(color);
        return figureFactory.createFigures(figureType, List.of(pawnCoordinate));
    }
}

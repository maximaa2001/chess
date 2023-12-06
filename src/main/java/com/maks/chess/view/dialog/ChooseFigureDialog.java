package com.maks.chess.view.dialog;

import com.maks.chess.constant.define.FigureType;
import javafx.scene.control.ChoiceDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ChooseFigureDialog {
    private static final Map<FigureType, String> availableFigures = new HashMap<>();
    private final ChoiceDialog<String> dialog;

    static {
        availableFigures.put(FigureType.CASTLE, "Ладья");
        availableFigures.put(FigureType.KNIGHT, "Конь");
        availableFigures.put(FigureType.BISHOP, "Слон");
        availableFigures.put(FigureType.QUEEN, "Ферзь");
    }

    public ChooseFigureDialog() {
        this.dialog = new ChoiceDialog<>(availableFigures.get(FigureType.CASTLE), availableFigures.values().stream().toList());
        dialog.setHeaderText("Необходимо выбрать новую фигуру");
        dialog.setContentText("Выбрать фигуру");
    }

    public FigureType of(String figureName) {
        return availableFigures
                .entrySet()
                .stream()
                .filter(e -> e.getValue().equals(figureName))
                .findAny()
                .map(Map.Entry::getKey)
                .orElse(FigureType.QUEEN);
    }

    public Optional<String> show() {
        return dialog.showAndWait();
    }
}

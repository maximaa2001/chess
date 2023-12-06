package com.maks.chess.view;

import com.maks.chess.util.AppUtils;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;

public final class ViewFactory {

    public static void transition(View view, Stage stage) {
        AppUtils.executeGui(() -> {
            Class<? extends AbstractView> clazz = view.getClazz();
            Constructor<?> declaredConstructor = clazz.getDeclaredConstructors()[0];
            try {
                AbstractView abstractView = (AbstractView) declaredConstructor.newInstance(stage);
                abstractView.show();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}

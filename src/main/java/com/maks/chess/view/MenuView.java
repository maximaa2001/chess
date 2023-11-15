package com.maks.chess.view;

import com.maks.chess.MainApplication;
import com.maks.chess.constant.StyleConstant;
import com.maks.chess.constant.ViewConstant;
import com.maks.chess.controller.Controller;
import com.maks.chess.controller.MenuController;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MenuView extends AbstractView {
    private static final URL FXML = MainApplication.class.getResource(ViewConstant.MENU_FXML);
    private List<Label> menuItems;

    public MenuView(Stage stage) {
        super(stage);
    }

    @Override
    protected URL getFxmlUrl() {
        return FXML;
    }

    @Override
    protected Controller getController() {
        return new MenuController(this);
    }

    public void setMenuItems(List<Label> menuItems) {
        this.menuItems = menuItems;
        addFont(new ArrayList<>(menuItems));
    }

    public void chooseMenu(int number) {
        for (int i = 0; i < menuItems.size(); i++) {
            if (i == number) {
                menuItems.get(i).getStyleClass().add(StyleConstant.ACTIVE_MENU_ITEM_CLASS_NAME);
            } else {
                menuItems.get(i).getStyleClass().remove(StyleConstant.ACTIVE_MENU_ITEM_CLASS_NAME);
            }
        }
    }

}

package com.kanbanice.desktop.view.components;

import javafx.scene.control.Button;

public class StyledButton {

    public static Button primary(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("btn-primary");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    public static Button delete(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("btn-delete");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    public static Button cancel(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("btn-cancel");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    public static Button dashed(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("btn-dashed");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    public static Button ghost(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("btn-ghost");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }
}

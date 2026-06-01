package com.kanbanice.desktop.view.components;

import com.kanbanice.desktop.model.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TaskCard extends VBox {

    public TaskCard(Task task) {
        super(8);
        getStyleClass().add("task-card");
        setPadding(new Insets(16));

        Label titleLbl = new Label(task.getTitle() != null ? task.getTitle() : "");
        titleLbl.getStyleClass().add("task-title");
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(Double.MAX_VALUE);

        getChildren().add(titleLbl);

        if (task.getDescription() != null && !task.getDescription().isBlank()) {
            Label descLbl = new Label(task.getDescription());
            descLbl.getStyleClass().add("task-description");
            descLbl.setWrapText(true);
            descLbl.setMaxWidth(Double.MAX_VALUE);
            getChildren().add(descLbl);
        }

        // Footer: status tag
        HBox footer = new HBox();
        footer.setPadding(new Insets(8, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_LEFT);

        Label tagLbl = new Label(formatStatus(task.getStatus()));
        tagLbl.getStyleClass().add("task-tag");
        tagLbl.setStyle(tagLbl.getStyle() + getTagStyle(task.getStatus()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        footer.getChildren().addAll(tagLbl, spacer);

        getChildren().add(footer);
    }

    private String formatStatus(String status) {
        if (status == null) return "Ожидание";
        return switch (status) {
            case "PENDING"     -> "Ожидание";
            case "IN_PROGRESS" -> "В работе";
            case "COMPLETED"   -> "Готово";
            default            -> status;
        };
    }

    private String getTagStyle(String status) {
        if (status == null) return "";
        return switch (status) {
            case "IN_PROGRESS" -> "-fx-background-color: #dbeafe; -fx-text-fill: #1e40af;";
            case "COMPLETED"   -> "-fx-background-color: #dcfce7; -fx-text-fill: #166534;";
            default            -> "";
        };
    }
}

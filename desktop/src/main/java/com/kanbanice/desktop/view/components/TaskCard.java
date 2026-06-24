package com.kanbanice.desktop.view.components;

import com.kanbanice.desktop.api.ApiClient;
import com.kanbanice.desktop.model.Task;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Map;

public class TaskCard extends VBox {

    public TaskCard(Task task, Runnable onRefresh) {
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

        HBox footer = new HBox(8);
        footer.setPadding(new Insets(8, 0, 0, 0));
        footer.setAlignment(Pos.CENTER_LEFT);

        Label tagLbl = new Label(formatStatus(task.getStatus()));
        tagLbl.getStyleClass().add("task-tag");
        tagLbl.setStyle(tagLbl.getStyle() + getTagStyle(task.getStatus()));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button toggleBtn = new Button("🔄");
        styleIconBtn(toggleBtn);
        toggleBtn.setTooltip(new Tooltip("Переключить статус"));
        toggleBtn.setOnAction(e -> toggleStatus(task, onRefresh));

        Button editBtn = new Button("✏️");
        styleIconBtn(editBtn);
        editBtn.setTooltip(new Tooltip("Редактировать"));
        editBtn.setOnAction(e -> showEditDialog(task, onRefresh));

        Button deleteBtn = new Button("🗑️");
        styleIconBtn(deleteBtn);
        deleteBtn.setStyle(deleteBtn.getStyle() + "-fx-text-fill: #ef4444;");
        deleteBtn.setTooltip(new Tooltip("Удалить задачу"));
        deleteBtn.setOnAction(e -> confirmDelete(task, onRefresh));

        footer.getChildren().addAll(tagLbl, spacer, toggleBtn, editBtn, deleteBtn);
        getChildren().add(footer);
    }

    private void toggleStatus(Task task, Runnable onRefresh) {
        String newStatus = "DONE".equals(task.getStatus()) ? "TODO" : "DONE";
        new Thread(() -> {
            try {
                ApiClient.get().put(
                    "/api/projects/tasks/" + task.getId(),
                    Map.of("status", newStatus),
                    Object.class
                );
                Platform.runLater(onRefresh);
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Не удалось изменить статус: " + ex.getMessage()));
            }
        }).start();
    }

    private void showEditDialog(Task task, Runnable onRefresh) {
        DialogHelper.show("Редактировать задачу", 440, (content, dialog) -> {
            Label tl = new Label("Заголовок");
            tl.getStyleClass().add("form-label-dark");
            TextField titleField = new TextField(task.getTitle() != null ? task.getTitle() : "");
            titleField.getStyleClass().add("form-input");

            Label dl = new Label("Описание");
            dl.getStyleClass().add("form-label-dark");
            TextArea descField = new TextArea(task.getDescription() != null ? task.getDescription() : "");
            descField.setPromptText("Описание (необязательно)");
            descField.setPrefRowCount(3);
            descField.setWrapText(true);
            descField.setStyle(
                "-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb;" +
                "-fx-border-width: 1px; -fx-border-radius: 10px; -fx-background-radius: 10px;" +
                "-fx-font-size: 14px; -fx-pref-width: 380px;"
            );

            Label errorLbl = new Label("");
            errorLbl.getStyleClass().add("error-text");

            Button saveBtn = StyledButton.primary("Сохранить");
            saveBtn.setOnAction(e -> {
                if (titleField.getText().isBlank()) { errorLbl.setText("Заголовок не может быть пустым"); return; }
                saveBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        ApiClient.get().put(
                            "/api/projects/tasks/" + task.getId(),
                            Map.of("title", titleField.getText().trim(), "description", descField.getText()),
                            Object.class
                        );
                        Platform.runLater(() -> { dialog.close(); onRefresh.run(); });
                    } catch (Exception ex) {
                        Platform.runLater(() -> { errorLbl.setText("Ошибка: " + ex.getMessage()); saveBtn.setDisable(false); });
                    }
                }).start();
            });

            content.getChildren().addAll(tl, titleField, dl, descField, errorLbl, saveBtn);
        });
    }

    private void confirmDelete(Task task, Runnable onRefresh) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление задачи");
        alert.setHeaderText(null);
        alert.setContentText("Удалить задачу «" + task.getTitle() + "»?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                new Thread(() -> {
                    try {
                        ApiClient.get().delete("/api/projects/tasks/" + task.getId());
                        Platform.runLater(onRefresh);
                    } catch (Exception ex) {
                        Platform.runLater(() -> showError("Не удалось удалить: " + ex.getMessage()));
                    }
                }).start();
            }
        });
    }

    private String formatStatus(String status) {
        if (status == null) return "—";
        return switch (status) {
            case "TODO" -> "В работе";
            case "DONE" -> "Готово";
            default     -> status;
        };
    }

    private String getTagStyle(String status) {
        if (status == null) return "";
        return switch (status) {
            case "TODO" -> "-fx-background-color: #dbeafe; -fx-text-fill: #1e40af;";
            case "DONE" -> "-fx-background-color: #dcfce7; -fx-text-fill: #166534;";
            default     -> "";
        };
    }

    private void styleIconBtn(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent; -fx-cursor: hand;" +
            "-fx-font-size: 14px; -fx-padding: 2 4 2 4; -fx-background-radius: 6px;"
        );
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
}

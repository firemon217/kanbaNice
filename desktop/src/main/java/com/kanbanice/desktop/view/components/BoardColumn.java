package com.kanbanice.desktop.view.components;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kanbanice.desktop.api.ApiClient;
import com.kanbanice.desktop.model.Board;
import com.kanbanice.desktop.model.Task;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;

public class BoardColumn extends VBox {

    private final Board board;
    private final VBox tasksContainer;
    private final Label titleLbl;
    private final Label countLabel;
    private Runnable onAddTaskRequest;
    private Runnable onDeleteRequest;

    public BoardColumn(Board board) {
        super(0);
        this.board = board;
        getStyleClass().add("board-column");

        // Title + count
        VBox titleBox = new VBox(4);
        titleLbl = new Label(board.getName());
        titleLbl.getStyleClass().add("board-title");
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(200);

        countLabel = new Label("0 задач");
        countLabel.getStyleClass().add("task-count");
        titleBox.getChildren().addAll(titleLbl, countLabel);

        // Edit board button
        Button editBtn = new Button("✏️");
        styleBoardBtn(editBtn);
        editBtn.setTooltip(new Tooltip("Переименовать доску"));
        editBtn.setOnAction(e -> showRenameBoardDialog());

        // Delete board button
        Button deleteBtn = new Button("🗑️");
        styleBoardBtn(deleteBtn);
        deleteBtn.setStyle(deleteBtn.getStyle() + "-fx-text-fill: #ef4444;");
        deleteBtn.setTooltip(new Tooltip("Удалить доску"));
        deleteBtn.setOnAction(e -> confirmDeleteBoard());

        HBox actions = new HBox(4);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.getChildren().addAll(editBtn, deleteBtn);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 14, 0));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer, actions);

        // Add task button
        Button addBtn = StyledButton.dashed("+ Добавить задачу");
        addBtn.setOnAction(e -> { if (onAddTaskRequest != null) onAddTaskRequest.run(); });

        // Tasks scroll area
        tasksContainer = new VBox(12);
        tasksContainer.setPadding(new Insets(12, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(tasksContainer);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPrefHeight(420);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(header, addBtn, scroll);
        loadTasks();
    }

    public void setOnAddTaskRequest(Runnable r) { this.onAddTaskRequest = r; }
    public void setOnDeleteRequest(Runnable r) { this.onDeleteRequest = r; }
    public Long getBoardId() { return board.getId(); }

    public void loadTasks() {
        new Thread(() -> {
            try {
                List<Task> tasks = ApiClient.get().get(
                    "/api/projects/boards/" + board.getId() + "/tasks",
                    new TypeReference<List<Task>>() {}
                );
                Platform.runLater(() -> {
                    tasksContainer.getChildren().clear();
                    for (Task t : tasks) {
                        tasksContainer.getChildren().add(new TaskCard(t, this::loadTasks));
                    }
                    int n = tasks.size();
                    countLabel.setText(n + (n == 1 ? " задача" : " задач"));
                });
            } catch (Exception ignored) {}
        }).start();
    }

    // ─── Rename board ─────────────────────────────────────────────────

    private void showRenameBoardDialog() {
        DialogHelper.show("Переименовать доску", 400, (content, dialog) -> {
            Label lbl = new Label("Новое название");
            lbl.getStyleClass().add("form-label-dark");
            TextField nameField = new TextField(board.getName());
            nameField.getStyleClass().add("form-input");

            Label errorLbl = new Label("");
            errorLbl.getStyleClass().add("error-text");

            Button saveBtn = StyledButton.primary("Сохранить");
            saveBtn.setOnAction(e -> {
                if (nameField.getText().isBlank()) { errorLbl.setText("Введите название"); return; }
                saveBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        ApiClient.get().put(
                            "/api/projects/boards/" + board.getId(),
                            Map.of("name", nameField.getText().trim()),
                            Object.class
                        );
                        String newName = nameField.getText().trim();
                        Platform.runLater(() -> {
                            board.setName(newName);
                            titleLbl.setText(newName);
                            dialog.close();
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            errorLbl.setText("Ошибка: " + ex.getMessage());
                            saveBtn.setDisable(false);
                        });
                    }
                }).start();
            });

            content.getChildren().addAll(lbl, nameField, errorLbl, saveBtn);
        });
    }

    // ─── Delete board ─────────────────────────────────────────────────

    private void confirmDeleteBoard() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление доски");
        alert.setHeaderText(null);
        alert.setContentText("Удалить доску «" + board.getName() + "»?\nВсе задачи будут удалены.");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                new Thread(() -> {
                    try {
                        ApiClient.get().delete("/api/projects/boards/" + board.getId());
                        Platform.runLater(() -> { if (onDeleteRequest != null) onDeleteRequest.run(); });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            Alert err = new Alert(Alert.AlertType.ERROR);
                            err.setHeaderText(null);
                            err.setContentText("Не удалось удалить доску: " + ex.getMessage());
                            err.showAndWait();
                        });
                    }
                }).start();
            }
        });
    }

    private void styleBoardBtn(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent; -fx-cursor: hand;" +
            "-fx-font-size: 13px; -fx-padding: 2 4 2 4; -fx-background-radius: 6px;"
        );
    }
}

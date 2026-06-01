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

public class BoardColumn extends VBox {

    private final Board board;
    private final VBox tasksContainer;
    private final Label countLabel;
    private Runnable onAddTaskRequest;

    public BoardColumn(Board board) {
        super(0);
        this.board = board;
        getStyleClass().add("board-column");

        // Header
        VBox titleBox = new VBox(4);
        Label titleLbl = new Label(board.getName());
        titleLbl.getStyleClass().add("board-title");
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(240);

        countLabel = new Label("0 задач");
        countLabel.getStyleClass().add("task-count");
        titleBox.getChildren().addAll(titleLbl, countLabel);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 14, 0));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer);

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

    public void setOnAddTaskRequest(Runnable r) {
        this.onAddTaskRequest = r;
    }

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
                        tasksContainer.getChildren().add(new TaskCard(t));
                    }
                    int n = tasks.size();
                    countLabel.setText(n + (n == 1 ? " задача" : " задач"));
                });
            } catch (Exception ignored) {
                // silently ignore if tasks can't be loaded
            }
        }).start();
    }

    public Long getBoardId() { return board.getId(); }
}

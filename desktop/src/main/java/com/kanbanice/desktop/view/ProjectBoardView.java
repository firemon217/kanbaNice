package com.kanbanice.desktop.view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kanbanice.desktop.api.ApiClient;
import com.kanbanice.desktop.model.Board;
import com.kanbanice.desktop.model.Project;
import com.kanbanice.desktop.state.AppState;
import com.kanbanice.desktop.view.components.BoardColumn;
import com.kanbanice.desktop.view.components.DialogHelper;
import com.kanbanice.desktop.view.components.StyledButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;

public class ProjectBoardView {

    private final VBox root;
    private final HBox boardsRow;
    private Long currentBoardIdForTask;

    public ProjectBoardView() {
        root = new VBox(0);
        root.setStyle("-fx-background-color: #15554e;");

        boardsRow = new HBox(16);
        boardsRow.setPadding(new Insets(0, 0, 16, 0));
        boardsRow.setAlignment(Pos.TOP_LEFT);

        Project project = AppState.getInstance().getCurrentProject();

        // Header
        HBox header = buildHeader(project);
        header.setPadding(new Insets(36, 36, 20, 36));

        // Boards scroll area
        ScrollPane scrollPane = new ScrollPane(boardsRow);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        scrollPane.setPadding(new Insets(0, 36, 36, 36));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(header, scrollPane);

        if (project != null) loadBoards(project.getId());
    }

    public VBox getRoot() { return root; }

    private HBox buildHeader(Project project) {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        String projectName = project != null ? project.getName() : "Проект";
        Label title = new Label(projectName);
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Управляйте досками и задачами");
        subtitle.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBoardBtn = StyledButton.primary("+ Добавить доску");
        addBoardBtn.setMaxWidth(Region.USE_PREF_SIZE);
        if (project != null) {
            addBoardBtn.setOnAction(e -> showCreateBoardDialog(project.getId()));
        }

        header.getChildren().addAll(titleBox, spacer, addBoardBtn);
        return header;
    }

    private void loadBoards(Long projectId) {
        new Thread(() -> {
            try {
                List<Board> boards = ApiClient.get().get(
                    "/api/projects/" + projectId + "/boards",
                    new TypeReference<List<Board>>() {}
                );
                Platform.runLater(() -> renderBoards(boards, projectId));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    Label err = new Label("Ошибка загрузки досок: " + ex.getMessage());
                    err.setStyle("-fx-text-fill: #ef4444;");
                    boardsRow.getChildren().add(err);
                });
            }
        }).start();
    }

    private void renderBoards(List<Board> boards, Long projectId) {
        boardsRow.getChildren().clear();
        for (Board b : boards) {
            BoardColumn col = new BoardColumn(b);
            col.setOnAddTaskRequest(() -> showCreateTaskDialog(b.getId(), col));
            boardsRow.getChildren().add(col);
        }
    }

    private void showCreateBoardDialog(Long projectId) {
        DialogHelper.show("Новая доска", 420, (content, dialog) -> {
            Label nameLbl = new Label("Название доски");
            nameLbl.getStyleClass().add("form-label-dark");

            TextField nameField = new TextField();
            nameField.getStyleClass().add("form-input");
            nameField.setPromptText("Например: В работе");

            Label errorLbl = new Label(""); errorLbl.getStyleClass().add("error-text");

            Button createBtn = StyledButton.primary("Создать");
            createBtn.setOnAction(e -> {
                if (nameField.getText().isBlank()) { errorLbl.setText("Введите название"); return; }
                createBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        ApiClient.get().post(
                            "/api/projects/" + projectId + "/boards",
                            Map.of("name", nameField.getText()), Board.class
                        );
                        Platform.runLater(() -> { dialog.close(); loadBoards(projectId); });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            errorLbl.setText("Ошибка: " + ex.getMessage());
                            createBtn.setDisable(false);
                        });
                    }
                }).start();
            });

            content.getChildren().addAll(nameLbl, nameField, errorLbl, createBtn);
        });
    }

    private void showCreateTaskDialog(Long boardId, BoardColumn col) {
        DialogHelper.show("Добавить задачу", 440, (content, dialog) -> {
            Label titleLbl = new Label("Заголовок");
            titleLbl.getStyleClass().add("form-label-dark");
            TextField titleField = new TextField();
            titleField.getStyleClass().add("form-input");
            titleField.setPromptText("Введите заголовок задачи");

            Label descLbl = new Label("Описание");
            descLbl.getStyleClass().add("form-label-dark");
            TextArea descField = new TextArea();
            descField.getStyleClass().add("form-input");
            descField.setPromptText("Введите описание (необязательно)");
            descField.setPrefRowCount(3);
            descField.setWrapText(true);
            descField.setStyle("-fx-background-color: #f9fafb; -fx-border-color: #e5e7eb; " +
                    "-fx-border-width: 1px; -fx-border-radius: 10px; -fx-background-radius: 10px; " +
                    "-fx-font-size: 14px; -fx-pref-width: 380px;");

            Label errorLbl = new Label(""); errorLbl.getStyleClass().add("error-text");

            Button addBtn = StyledButton.primary("Добавить");
            addBtn.setOnAction(e -> {
                if (titleField.getText().isBlank()) { errorLbl.setText("Введите заголовок"); return; }
                addBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        ApiClient.get().post(
                            "/api/projects/boards/" + boardId + "/tasks",
                            Map.of("title", titleField.getText(),
                                   "description", descField.getText()),
                            Object.class
                        );
                        Platform.runLater(() -> { dialog.close(); col.loadTasks(); });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            errorLbl.setText("Ошибка: " + ex.getMessage());
                            addBtn.setDisable(false);
                        });
                    }
                }).start();
            });

            content.getChildren().addAll(titleLbl, titleField, descLbl, descField, errorLbl, addBtn);
        });
    }
}

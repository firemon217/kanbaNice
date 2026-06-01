package com.kanbanice.desktop.view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kanbanice.desktop.App;
import com.kanbanice.desktop.api.ApiClient;
import com.kanbanice.desktop.model.Project;
import com.kanbanice.desktop.state.AppState;
import com.kanbanice.desktop.view.components.DialogHelper;
import com.kanbanice.desktop.view.components.ProjectCard;
import com.kanbanice.desktop.view.components.StyledButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;
import java.util.Map;

public class ProjectsView {

    private final ScrollPane root;
    private final FlowPane projectsGrid;
    private final Label emptyLbl;

    public ProjectsView() {
        projectsGrid = new FlowPane(16, 16);
        projectsGrid.setStyle("-fx-background-color: transparent;");

        emptyLbl = new Label("У вас пока нет проектов. Создайте первый!");
        emptyLbl.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 15px;");
        emptyLbl.setVisible(false);

        VBox page = buildPage();
        root = new ScrollPane(page);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #15554e; -fx-background: #15554e; -fx-border-color: transparent;");

        loadProjects();
    }

    public ScrollPane getRoot() { return root; }

    private VBox buildPage() {
        VBox page = new VBox(28);
        page.setPadding(new Insets(36));
        page.setStyle("-fx-background-color: #15554e;");

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label("Проекты");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Управляйте вашими проектами");
        subtitle.getStyleClass().add("page-subtitle");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button newProjectBtn = StyledButton.primary("+ Новый проект");
        newProjectBtn.setMaxWidth(Region.USE_PREF_SIZE);
        newProjectBtn.setOnAction(e -> showCreateDialog());

        header.getChildren().addAll(titleBox, spacer, newProjectBtn);

        page.getChildren().addAll(header, emptyLbl, projectsGrid);
        return page;
    }

    private void loadProjects() {
        new Thread(() -> {
            try {
                List<Project> projects = ApiClient.get().get(
                    "/api/projects", new TypeReference<List<Project>>() {});
                AppState.getInstance().setProjects(projects);
                Platform.runLater(() -> renderProjects(projects));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    emptyLbl.setText("Ошибка загрузки: " + ex.getMessage());
                    emptyLbl.setVisible(true);
                });
            }
        }).start();
    }

    private void renderProjects(List<Project> projects) {
        projectsGrid.getChildren().clear();
        if (projects == null || projects.isEmpty()) {
            emptyLbl.setVisible(true);
            return;
        }
        emptyLbl.setVisible(false);
        for (Project p : projects) {
            ProjectCard card = new ProjectCard(
                p,
                () -> openProject(p),
                () -> showDeleteDialog(p)
            );
            projectsGrid.getChildren().add(card);
        }
    }

    private void openProject(Project project) {
        AppState.getInstance().setCurrentProject(project);
        App.navigateTo("board");
    }

    private void showCreateDialog() {
        DialogHelper.show("Новый проект", 420, (content, dialog) -> {
            Label nameLbl = new Label("Название проекта");
            nameLbl.getStyleClass().add("form-label-dark");

            TextField nameField = new TextField();
            nameField.getStyleClass().add("form-input");
            nameField.setPromptText("Введите название проекта");

            Label errorLbl = new Label(""); errorLbl.getStyleClass().add("error-text");

            Button createBtn = StyledButton.primary("Создать");
            createBtn.setOnAction(e -> {
                if (nameField.getText().isBlank()) { errorLbl.setText("Введите название"); return; }
                createBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        ApiClient.get().post("/api/projects",
                            Map.of("name", nameField.getText()), Project.class);
                        Platform.runLater(() -> { dialog.close(); loadProjects(); });
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

    private void showDeleteDialog(Project project) {
        DialogHelper.show("Удалить проект", 400, (content, dialog) -> {
            Label msg = new Label("Удалить проект \"" + project.getName() + "\"? Все доски и задачи будут удалены.");
            msg.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px;"); msg.setWrapText(true);

            Button delBtn = StyledButton.delete("Удалить");
            delBtn.setOnAction(e -> {
                delBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        ApiClient.get().delete("/api/projects/" + project.getId());
                        Platform.runLater(() -> { dialog.close(); loadProjects(); });
                    } catch (Exception ex) {
                        Platform.runLater(() -> delBtn.setDisable(false));
                    }
                }).start();
            });

            Button cancelBtn = StyledButton.cancel("Отмена");
            cancelBtn.setOnAction(e -> dialog.close());

            content.getChildren().addAll(msg, delBtn, cancelBtn);
        });
    }
}

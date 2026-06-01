package com.kanbanice.desktop.view;

import com.kanbanice.desktop.App;
import com.kanbanice.desktop.api.ApiClient;
import com.kanbanice.desktop.model.UserProfile;
import com.kanbanice.desktop.state.AppState;
import com.kanbanice.desktop.view.components.DialogHelper;
import com.kanbanice.desktop.view.components.StyledButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.Map;

public class ProfileView {

    private final ScrollPane root;
    private VBox container;

    public ProfileView() {
        container = buildContent();
        root = new ScrollPane(container);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #15554e; -fx-background: #15554e; -fx-border-color: transparent;");
    }

    public ScrollPane getRoot() { return root; }

    private VBox buildContent() {
        VBox page = new VBox(24);
        page.setPadding(new Insets(36));
        page.setStyle("-fx-background-color: #15554e;");

        UserProfile user = AppState.getInstance().getUser();
        if (user == null) {
            page.getChildren().add(new Label("Загрузка..."));
            return page;
        }

        // Header with avatar
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = makeAvatar(user.getName());

        VBox userInfo = new VBox(4);
        Label nameLbl = new Label(user.getName() != null ? user.getName() : "—");
        nameLbl.getStyleClass().add("page-title");
        Label usernameLbl = new Label("@" + (user.getUsername() != null ? user.getUsername() : "—"));
        usernameLbl.getStyleClass().add("page-subtitle");
        userInfo.getChildren().addAll(nameLbl, usernameLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = StyledButton.primary("✏  Редактировать");
        editBtn.setMaxWidth(Region.USE_PREF_SIZE);
        editBtn.setOnAction(e -> showEditDialog(user));

        header.getChildren().addAll(avatar, userInfo, spacer, editBtn);

        // Info card
        VBox infoCard = new VBox(16);
        infoCard.getStyleClass().add("info-card");
        infoCard.setPadding(new Insets(24));

        Label infoTitle = new Label("Информация о профиле");
        infoTitle.getStyleClass().add("section-title");

        infoCard.getChildren().addAll(
            infoTitle,
            infoRow("Имя", user.getName()),
            infoRow("Логин", user.getUsername()),
            infoRow("Email", user.getEmail()),
            infoRow("Роль", formatRole(user.getUserType()))
        );

        // Danger zone
        VBox dangerCard = new VBox(12);
        dangerCard.getStyleClass().add("info-card");
        dangerCard.setPadding(new Insets(24));
        Label dangerTitle = new Label("Опасная зона");
        dangerTitle.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label dangerDesc = new Label("Удаление аккаунта — необратимое действие.");
        dangerDesc.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 13px;");
        Button deleteBtn = StyledButton.delete("Удалить аккаунт");
        deleteBtn.setMaxWidth(200);
        deleteBtn.setOnAction(e -> showDeleteDialog());
        dangerCard.getChildren().addAll(dangerTitle, dangerDesc, deleteBtn);

        page.getChildren().addAll(header, infoCard, dangerCard);
        return page;
    }

    private HBox infoRow(String label, String value) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label + ":");
        lbl.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 14px; -fx-min-width: 80px;");
        Label val = new Label(value != null ? value : "—");
        val.setStyle("-fx-text-fill: #fafafa; -fx-font-size: 14px; -fx-font-weight: bold;");
        row.getChildren().addAll(lbl, val);
        return row;
    }

    private StackPane makeAvatar(String name) {
        StackPane sp = new StackPane();
        sp.setMinSize(72, 72);
        sp.setMaxSize(72, 72);
        sp.setStyle("-fx-background-color: #6bfff3; -fx-background-radius: 36px;");
        String ch = (name != null && !name.isBlank()) ? name.substring(0, 1).toUpperCase() : "?";
        Label lbl = new Label(ch);
        lbl.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0a332e;");
        sp.getChildren().add(lbl);
        return sp;
    }

    private String formatRole(String userType) {
        if ("LEADER".equalsIgnoreCase(userType)) return "Организатор";
        if ("STANDART".equalsIgnoreCase(userType)) return "Работник";
        return userType != null ? userType : "—";
    }

    private void showEditDialog(UserProfile user) {
        DialogHelper.show("Редактировать профиль", 440, (content, dialog) -> {
            TextField nameField = new TextField(user.getName() != null ? user.getName() : "");
            nameField.getStyleClass().add("form-input");
            nameField.setPromptText("Полное имя");

            TextField emailField = new TextField(user.getEmail() != null ? user.getEmail() : "");
            emailField.getStyleClass().add("form-input");
            emailField.setPromptText("Email");

            PasswordField passField = new PasswordField();
            passField.getStyleClass().add("form-input");
            passField.setPromptText("Новый пароль (оставьте пустым, чтобы не менять)");

            Label errorLbl = new Label("");
            errorLbl.getStyleClass().add("error-text");

            Button saveBtn = StyledButton.primary("Сохранить");
            saveBtn.setOnAction(e -> {
                Map<String, Object> body = new HashMap<>();
                if (!nameField.getText().isBlank()) body.put("name", nameField.getText());
                if (!emailField.getText().isBlank()) body.put("email", emailField.getText());
                if (!passField.getText().isBlank()) body.put("password", passField.getText());

                saveBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        var updated = ApiClient.get().put("/api/users/profile", body, UserProfile.class);
                        AppState.getInstance().setUser(updated);
                        Platform.runLater(() -> {
                            dialog.close();
                            refreshView();
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            errorLbl.setText("Ошибка: " + ex.getMessage());
                            saveBtn.setDisable(false);
                        });
                    }
                }).start();
            });

            Label nameLbl = new Label("Имя"); nameLbl.getStyleClass().add("form-label-dark");
            Label emailLbl = new Label("Email"); emailLbl.getStyleClass().add("form-label-dark");
            Label passLbl = new Label("Пароль"); passLbl.getStyleClass().add("form-label-dark");

            content.getChildren().addAll(
                nameLbl, nameField, emailLbl, emailField,
                passLbl, passField, errorLbl, saveBtn
            );
        });
    }

    private void showDeleteDialog() {
        DialogHelper.show("Удалить аккаунт", 400, (content, dialog) -> {
            Label msg = new Label("Вы уверены? Это действие необратимо.");
            msg.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px;");
            msg.setWrapText(true);

            Button confirmBtn = StyledButton.delete("Да, удалить аккаунт");
            confirmBtn.setOnAction(e -> {
                confirmBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        ApiClient.get().delete("/api/users/profile");
                        Platform.runLater(() -> { dialog.close(); App.navigateToLogin(); });
                    } catch (Exception ex) {
                        Platform.runLater(() -> confirmBtn.setDisable(false));
                    }
                }).start();
            });

            Button cancelBtn = StyledButton.cancel("Отмена");
            cancelBtn.setOnAction(e -> dialog.close());

            content.getChildren().addAll(msg, confirmBtn, cancelBtn);
        });
    }

    private void refreshView() {
        container = buildContent();
        root.setContent(container);
    }
}

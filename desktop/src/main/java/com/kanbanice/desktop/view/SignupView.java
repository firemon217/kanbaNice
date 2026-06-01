package com.kanbanice.desktop.view;

import com.kanbanice.desktop.App;
import com.kanbanice.desktop.api.ApiClient;
import com.kanbanice.desktop.view.components.StyledButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Map;

public class SignupView {

    private final StackPane root;

    public SignupView() {
        root = new StackPane();
        root.setStyle("-fx-background-color: #15554e;");

        ScrollPane scroll = new ScrollPane(buildCard());
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        root.getChildren().add(scroll);
    }

    public StackPane getRoot() { return root; }

    private VBox buildCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setMaxWidth(420);
        card.setPadding(new Insets(40));

        // Header
        Label title = new Label("Создать аккаунт");
        title.getStyleClass().add("login-title");
        Label subtitle = new Label("Зарегистрируйтесь для работы с системой");
        subtitle.getStyleClass().add("login-subtitle");
        VBox header = new VBox(6, title, subtitle);
        header.setAlignment(Pos.TOP_CENTER);

        // Fields
        TextField nameField = createField("Полное имя", "Иванов Иван");
        TextField usernameField = createField("Имя пользователя", "ivanov");
        TextField emailField = createField("Email", "ivanov@gmail.com");

        Label roleLbl = new Label("Роль");
        roleLbl.getStyleClass().add("form-label-dark");
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Работник", "Организатор");
        roleBox.setPromptText("Выберите роль");
        roleBox.setMaxWidth(Double.MAX_VALUE);
        roleBox.setStyle(
            "-fx-background-color: #f9fafb;" +
            "-fx-border-color: #e5e7eb;" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 10px;" +
            "-fx-background-radius: 10px;" +
            "-fx-font-size: 14px;"
        );

        Label passLbl = new Label("Пароль");
        passLbl.getStyleClass().add("form-label-dark");
        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("form-input");
        passwordField.setPromptText("Минимум 6 символов");

        Label errorLbl = new Label("");
        errorLbl.getStyleClass().add("error-text");
        errorLbl.setWrapText(true);
        errorLbl.setMaxWidth(360);

        Button signupBtn = StyledButton.primary("Зарегистрироваться");
        signupBtn.setOnAction(e -> {
            int idx = roleBox.getSelectionModel().getSelectedIndex();
            if (idx < 0) { errorLbl.setText("Выберите роль"); return; }
            doSignup(
                nameField.getText(), usernameField.getText(),
                emailField.getText(), passwordField.getText(),
                idx, signupBtn, errorLbl
            );
        });

        Button loginLink = new Button("Войти");
        loginLink.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #15554e;" +
            "-fx-underline: true;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 0;"
        );
        loginLink.setOnAction(e -> App.getStage().getScene().setRoot(new LoginView().getRoot()));

        Label footerLbl = new Label("Уже есть аккаунт? ");
        footerLbl.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13px;");
        HBox footer = new HBox(4, footerLbl, loginLink);
        footer.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                header,
                nameField, usernameField, emailField,
                roleLbl, roleBox,
                passLbl, passwordField,
                errorLbl, signupBtn, footer
        );

        // Wrap in StackPane to center
        StackPane wrapper = new StackPane(card);
        wrapper.setStyle("-fx-background-color: #15554e;");
        wrapper.setPadding(new Insets(40));

        VBox outer = new VBox(wrapper);
        VBox.setVgrow(wrapper, Priority.ALWAYS);
        return outer;
    }

    private TextField createField(String label, String prompt) {
        TextField tf = new TextField();
        tf.getStyleClass().add("form-input");
        tf.setPromptText(prompt);
        return tf;
    }

    private void doSignup(String name, String username, String email, String password,
                          int roleIdx, Button btn, Label errorLbl) {
        if (name.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
            errorLbl.setText("Заполните все поля"); return;
        }
        if (password.length() < 6) {
            errorLbl.setText("Пароль должен содержать минимум 6 символов"); return;
        }
        btn.setDisable(true);
        btn.setText("Создание аккаунта...");
        errorLbl.setText("");

        new Thread(() -> {
            try {
                var body = Map.of(
                    "name", name, "username", username,
                    "email", email, "password", password,
                    "userType", roleIdx
                );
                ApiClient.get().post("/auth/signup", body, Object.class);
                Platform.runLater(() -> App.getStage().getScene().setRoot(new LoginView().getRoot()));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    errorLbl.setText("Ошибка: " + ex.getMessage());
                    btn.setDisable(false);
                    btn.setText("Зарегистрироваться");
                });
            }
        }).start();
    }
}

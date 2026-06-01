package com.kanbanice.desktop.view;

import com.kanbanice.desktop.App;
import com.kanbanice.desktop.api.ApiClient;
import com.kanbanice.desktop.model.AuthResponse;
import com.kanbanice.desktop.model.UserProfile;
import com.kanbanice.desktop.state.AppState;
import com.kanbanice.desktop.view.components.StyledButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Map;

public class LoginView {

    private final StackPane root;

    public LoginView() {
        root = new StackPane();
        root.setStyle("-fx-background-color: #15554e;");
        root.getChildren().add(buildCard());
    }

    public StackPane getRoot() { return root; }

    private VBox buildCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setMaxWidth(420);
        card.setPadding(new Insets(40));

        // Header
        Label title = new Label("KanbaNice");
        title.getStyleClass().add("login-title");
        Label subtitle = new Label("Войдите, чтобы продолжить работу");
        subtitle.getStyleClass().add("login-subtitle");
        VBox header = new VBox(6, title, subtitle);
        header.setAlignment(Pos.TOP_CENTER);

        // Username
        Label usernameLbl = new Label("Имя пользователя");
        usernameLbl.getStyleClass().add("form-label-dark");
        TextField usernameField = new TextField();
        usernameField.getStyleClass().add("form-input");
        usernameField.setPromptText("Введите имя пользователя");

        // Password
        Label passwordLbl = new Label("Пароль");
        passwordLbl.getStyleClass().add("form-label-dark");
        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("form-input");
        passwordField.setPromptText("••••••••");

        // Error label
        Label errorLbl = new Label("");
        errorLbl.getStyleClass().add("error-text");
        errorLbl.setWrapText(true);
        errorLbl.setMaxWidth(360);

        // Login button
        Button loginBtn = StyledButton.primary("Войти");
        loginBtn.setOnAction(e ->
                doLogin(usernameField.getText(), passwordField.getText(), loginBtn, errorLbl));
        passwordField.setOnAction(e -> loginBtn.fire());

        // Divider
        HBox divider = new HBox();
        divider.setAlignment(Pos.CENTER);
        Label divLbl = new Label("— ИЛИ —");
        divLbl.getStyleClass().add("divider-label");
        divider.getChildren().add(divLbl);

        // Footer — go to signup
        Button signupLink = new Button("Зарегистрироваться");
        signupLink.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #15554e;" +
            "-fx-underline: true;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;" +
            "-fx-padding: 0;"
        );
        signupLink.setOnAction(e -> App.getStage().getScene().setRoot(new SignupView().getRoot()));

        Label footerLbl = new Label("Нет аккаунта? ");
        footerLbl.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 13px;");
        HBox footer = new HBox(4, footerLbl, signupLink);
        footer.setAlignment(Pos.CENTER);

        card.getChildren().addAll(
                header,
                usernameLbl, usernameField,
                passwordLbl, passwordField,
                errorLbl,
                loginBtn,
                divider,
                footer
        );
        return card;
    }

    private void doLogin(String username, String password, Button btn, Label errorLbl) {
        if (username.isBlank() || password.isBlank()) {
            errorLbl.setText("Заполните все поля");
            return;
        }
        btn.setDisable(true);
        btn.setText("Вход...");
        errorLbl.setText("");

        new Thread(() -> {
            try {
                var body = Map.of("username", username, "password", password);
                var auth = ApiClient.get().post("/auth/login", body, AuthResponse.class);
                AppState.getInstance().setToken(auth.getToken());
                System.out.println("=== DEBUG LOGIN ===");
                System.out.println("Raw Response Object: " + auth.toString());
                System.out.println("Extracted Token: " + auth.getToken());
                System.out.println("===================");
                var user = ApiClient.get().get("/api/users/profile", UserProfile.class);
                AppState.getInstance().setUser(user);

                Platform.runLater(() -> App.navigateToMain("projects"));
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    errorLbl.setText(ex.getMessage());
                    btn.setDisable(false);
                    btn.setText("Войти");
                });
            }
        }).start();
    }
}

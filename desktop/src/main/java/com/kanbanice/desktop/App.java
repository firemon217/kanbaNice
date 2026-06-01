package com.kanbanice.desktop;

import com.kanbanice.desktop.state.AppState;
import com.kanbanice.desktop.view.LoginView;
import com.kanbanice.desktop.view.MainLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;
    private static MainLayout mainLayout;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("KanbaNice");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setWidth(1280);
        stage.setHeight(760);

        navigateToLogin();
        stage.show();
    }

    public static void navigateToLogin() {
        AppState.getInstance().clear();
        mainLayout = null;
        Scene scene = new Scene(new LoginView().getRoot());
        applyStyles(scene);
        primaryStage.setScene(scene);
    }

    public static void navigateToMain(String initialView) {
        mainLayout = new MainLayout();
        Scene scene = new Scene(mainLayout.getRoot());
        applyStyles(scene);
        primaryStage.setScene(scene);
        mainLayout.navigateTo(initialView);
    }

    public static void navigateTo(String view) {
        if (mainLayout != null) {
            mainLayout.navigateTo(view);
        }
    }

    public static Stage getStage() { return primaryStage; }

    private static void applyStyles(Scene scene) {
        String css = App.class.getResource("/styles/main.css").toExternalForm();
        scene.getStylesheets().add(css);
    }
}

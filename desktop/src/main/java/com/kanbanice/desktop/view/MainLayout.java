package com.kanbanice.desktop.view;

import com.kanbanice.desktop.view.components.Sidebar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainLayout {

    private final BorderPane root;
    private final StackPane contentArea;
    private final Sidebar sidebar;

    public MainLayout() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #15554e;");

        sidebar = new Sidebar();
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #15554e;");

        root.setLeft(sidebar);
        root.setCenter(contentArea);
    }

    public BorderPane getRoot() { return root; }

    public void navigateTo(String view) {
        sidebar.setActive(view);
        contentArea.getChildren().clear();

        switch (view) {
            case "profile"  -> contentArea.getChildren().add(new ProfileView().getRoot());
            case "company"  -> contentArea.getChildren().add(new CompanyView().getRoot());
            case "projects" -> contentArea.getChildren().add(new ProjectsView().getRoot());
            case "board"    -> contentArea.getChildren().add(new ProjectBoardView().getRoot());
        }
    }
}

package com.kanbanice.desktop.view.components;

import com.kanbanice.desktop.App;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {

    private final Button profileBtn;
    private final Button companyBtn;
    private final Button projectsBtn;

    public Sidebar() {
        super(2);
        getStyleClass().add("sidebar");
        setPrefWidth(220);
        setMinWidth(220);
        setMaxWidth(220);

        Label title = new Label("KanbaNice");
        title.getStyleClass().add("sidebar-title");

        profileBtn = navBtn("👤  Профиль", "profile");
        companyBtn = navBtn("🏢  Компания", "company");
        projectsBtn = navBtn("📋  Проекты", "projects");

        VBox nav = new VBox(4, profileBtn, companyBtn, projectsBtn);
        nav.setPadding(new Insets(8, 12, 8, 12));
        VBox.setVgrow(nav, Priority.ALWAYS);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("🚪  Выйти");
        logoutBtn.getStyleClass().add("sidebar-nav-item");
        logoutBtn.setStyle("-fx-text-fill: #ef4444;");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> App.navigateToLogin());

        VBox footer = new VBox(logoutBtn);
        footer.setPadding(new Insets(8, 12, 24, 12));

        getChildren().addAll(title, nav, spacer, footer);
    }

    private Button navBtn(String text, String view) {
        Button b = new Button(text);
        b.getStyleClass().add("sidebar-nav-item");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> App.navigateTo(view));
        return b;
    }

    public void setActive(String view) {
        profileBtn.getStyleClass().remove("active");
        companyBtn.getStyleClass().remove("active");
        projectsBtn.getStyleClass().remove("active");

        switch (view) {
            case "profile"           -> profileBtn.getStyleClass().add("active");
            case "company"           -> companyBtn.getStyleClass().add("active");
            case "projects", "board" -> projectsBtn.getStyleClass().add("active");
        }
    }
}

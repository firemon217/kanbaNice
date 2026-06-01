package com.kanbanice.desktop.view.components;

import com.kanbanice.desktop.model.Project;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class ProjectCard extends VBox {

    public ProjectCard(Project project, Runnable onOpen, Runnable onDelete) {
        super(12);
        getStyleClass().add("project-card");
        setPadding(new Insets(20));

        // Name
        Label nameLbl = new Label(project.getName());
        nameLbl.getStyleClass().add("project-card-name");
        nameLbl.setWrapText(true);
        nameLbl.setMaxWidth(Double.MAX_VALUE);

        // Created date
        String dateStr = project.getCreatedAt() != null
                ? project.getCreatedAt().substring(0, Math.min(10, project.getCreatedAt().length()))
                : "—";
        Label infoLbl = new Label("Создан: " + dateStr);
        infoLbl.getStyleClass().add("project-card-info");

        // Actions
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_LEFT);

        Button openBtn = StyledButton.primary("Открыть");
        HBox.setHgrow(openBtn, Priority.ALWAYS);
        openBtn.setOnAction(e -> { if (onOpen != null) onOpen.run(); });

        Button delBtn = new Button("Удалить");
        delBtn.getStyleClass().add("btn-delete");
        delBtn.setMaxWidth(Region.USE_PREF_SIZE);
        delBtn.setOnAction(e -> { if (onDelete != null) onDelete.run(); });

        actions.getChildren().addAll(openBtn, delBtn);

        getChildren().addAll(nameLbl, infoLbl, actions);
    }
}

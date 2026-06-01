package com.kanbanice.desktop.view.components;

import com.kanbanice.desktop.App;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DialogHelper {

    public interface DialogContent {
        void build(VBox content, Stage dialog);
    }

    public static void show(String title, double width, DialogContent builder) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(App.getStage());
        dialog.setTitle(title);
        dialog.setResizable(false);

        VBox box = new VBox(16);
        box.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 32px;" +
            "-fx-background-radius: 16px;"
        );
        box.setPrefWidth(width);

        // Header
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("modal-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button closeBtn = new Button("✕");
        closeBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #6b7280;" +
            "-fx-font-size: 15px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 2 6 2 6;" +
            "-fx-border-radius: 6px;" +
            "-fx-background-radius: 6px;"
        );
        closeBtn.setOnAction(e -> dialog.close());
        header.getChildren().addAll(titleLbl, spacer, closeBtn);

        VBox content = new VBox(12);
        builder.build(content, dialog);

        box.getChildren().addAll(header, content);

        Scene scene = new Scene(box);
        scene.getStylesheets().add(
            DialogHelper.class.getResource("/styles/main.css").toExternalForm()
        );
        dialog.setScene(scene);
        dialog.sizeToScene();
        dialog.setMinWidth(width);
        dialog.showAndWait();
    }
}

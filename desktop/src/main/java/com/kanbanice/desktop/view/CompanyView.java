package com.kanbanice.desktop.view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kanbanice.desktop.api.ApiClient;
import com.kanbanice.desktop.model.Company;
import com.kanbanice.desktop.model.UserProfile;
import com.kanbanice.desktop.state.AppState;
import com.kanbanice.desktop.view.components.DialogHelper;
import com.kanbanice.desktop.view.components.StyledButton;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.Map;

public class CompanyView {

    private final ScrollPane root;
    private VBox container;

    public CompanyView() {
        container = new VBox(24);
        container.setPadding(new Insets(36));
        container.setStyle("-fx-background-color: #15554e;");
        root = new ScrollPane(container);
        root.setFitToWidth(true);
        root.setStyle("-fx-background-color: #15554e; -fx-background: #15554e; -fx-border-color: transparent;");
        loadCompany();
    }

    public ScrollPane getRoot() { return root; }

    private void loadCompany() {
        new Thread(() -> {
            Company company = null;
            try {
                company = ApiClient.get().get("/api/company", Company.class);
                AppState.getInstance().setCompany(company);
            } catch (Exception ignored) {
                AppState.getInstance().setCompany(null);
            }
            final Company c = company;
            Platform.runLater(() -> renderCompany(c));
        }).start();
    }

    private void renderCompany(Company company) {
        container.getChildren().clear();
        UserProfile user = AppState.getInstance().getUser();
        boolean isLeader = user != null && user.isLeader();

        // === HEADER ===
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = makeAvatar(company != null ? company.getName() : null);

        VBox nameBox = new VBox(4);
        String companyName = company != null ? company.getName()
                : (isLeader ? "Создайте компанию" : "Вы не принадлежите ни одной компании");
        Label nameLbl = new Label(companyName);
        nameLbl.getStyleClass().add("page-title");
        nameLbl.setWrapText(true);
        nameBox.getChildren().add(nameLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(avatar, nameBox, spacer);

        if (isLeader) {
            if (company == null) {
                Button createBtn = StyledButton.primary("+ Создать компанию");
                createBtn.setMaxWidth(Region.USE_PREF_SIZE);
                createBtn.setOnAction(e -> showCreateDialog());
                header.getChildren().add(createBtn);
            } else {
                Button editBtn = StyledButton.primary("✏  Редактировать");
                editBtn.setMaxWidth(Region.USE_PREF_SIZE);
                editBtn.setOnAction(e -> showEditDialog(company));

                Button deleteBtn = StyledButton.delete("Удалить компанию");
                deleteBtn.setMaxWidth(Region.USE_PREF_SIZE);
                deleteBtn.setOnAction(e -> showDeleteCompanyDialog());

                header.getChildren().addAll(editBtn, deleteBtn);
            }
        }

        container.getChildren().add(header);

        // === COWORKERS SECTION ===
        if (company != null && company.getUsers() != null) {
            VBox section = new VBox(16);
            section.getStyleClass().add("info-card");
            section.setPadding(new Insets(24));

            HBox sectionHeader = new HBox(12);
            sectionHeader.setAlignment(Pos.CENTER_LEFT);
            Label sectionTitle = new Label("Сотрудники (" + company.getUsers().size() + ")");
            sectionTitle.getStyleClass().add("section-title");
            Region sh = new Region(); HBox.setHgrow(sh, Priority.ALWAYS);
            sectionHeader.getChildren().addAll(sectionTitle, sh);

            if (isLeader) {
                Button addWorkerBtn = new Button("+ Добавить сотрудника");
                addWorkerBtn.getStyleClass().add("btn-ghost");
                addWorkerBtn.setOnAction(e -> showAddWorkerDialog(company));
                sectionHeader.getChildren().add(addWorkerBtn);
            }
            section.getChildren().add(sectionHeader);

            FlowPane grid = new FlowPane(12, 12);
            for (UserProfile w : company.getUsers()) {
                grid.getChildren().add(buildWorkerCard(w, company, isLeader, user));
            }
            section.getChildren().add(grid);
            container.getChildren().add(section);
        }
    }

    private VBox buildWorkerCard(UserProfile w, Company company, boolean isLeader, UserProfile currentUser) {
        VBox card = new VBox(8);
        card.getStyleClass().add("coworker-card");
        card.setPadding(new Insets(16));
        card.setMinWidth(220);

        StackPane av = makeAvatar(w.getName());
        av.setMinSize(44, 44); av.setMaxSize(44, 44);
        ((Label) av.getChildren().get(0)).setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0a332e;");
        av.setStyle("-fx-background-color: #6bfff3; -fx-background-radius: 22px;");

        Label nameLbl = new Label(w.getName() != null ? w.getName() : "—");
        nameLbl.setStyle("-fx-text-fill: #fafafa; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label emailLbl = new Label(w.getEmail() != null ? w.getEmail() : "—");
        emailLbl.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12px;");
        String roleStr = "LEADER".equalsIgnoreCase(w.getUserType()) ? "Организатор" : "Работник";
        Label roleLbl = new Label(roleStr);
        roleLbl.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #374151; " +
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 3 8 3 8;");

        card.getChildren().addAll(av, nameLbl, emailLbl, roleLbl);

        if (isLeader && !"LEADER".equalsIgnoreCase(w.getUserType())
                && (currentUser == null || !currentUser.getId().equals(w.getId()))) {
            Button removeBtn = new Button("Удалить");
            removeBtn.getStyleClass().add("btn-delete");
            removeBtn.setMaxWidth(Double.MAX_VALUE);
            removeBtn.setStyle(removeBtn.getStyle() + " -fx-font-size: 12px; -fx-padding: 5 10 5 10;");
            removeBtn.setOnAction(e -> showRemoveWorkerDialog(w));
            card.getChildren().add(removeBtn);
        }
        return card;
    }

    private StackPane makeAvatar(String name) {
        StackPane sp = new StackPane();
        sp.setMinSize(72, 72); sp.setMaxSize(72, 72);
        sp.setStyle("-fx-background-color: #6bfff3; -fx-background-radius: 36px;");
        String ch = (name != null && !name.isBlank()) ? name.substring(0, 1).toUpperCase() : "?";
        Label lbl = new Label(ch);
        lbl.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #0a332e;");
        sp.getChildren().add(lbl);
        return sp;
    }

    private void showCreateDialog() {
        DialogHelper.show("Создать компанию", 420, (content, dialog) -> {
            TextField nameField = new TextField();
            nameField.getStyleClass().add("form-input");
            nameField.setPromptText("Введите название компании");
            Label errorLbl = new Label(""); errorLbl.getStyleClass().add("error-text");
            Button createBtn = StyledButton.primary("Создать");
            createBtn.setOnAction(e -> {
                if (nameField.getText().isBlank()) { errorLbl.setText("Введите название"); return; }
                createBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        Company c = ApiClient.get().post("/api/company",
                                Map.of("name", nameField.getText()), Company.class);
                        AppState.getInstance().setCompany(c);
                        Platform.runLater(() -> { dialog.close(); renderCompany(c); });
                    } catch (Exception ex) {
                        Platform.runLater(() -> { errorLbl.setText("Ошибка: " + ex.getMessage()); createBtn.setDisable(false); });
                    }
                }).start();
            });
            Label lbl = new Label("Название компании"); lbl.getStyleClass().add("form-label-dark");
            content.getChildren().addAll(lbl, nameField, errorLbl, createBtn);
        });
    }

    private void showEditDialog(Company company) {
        DialogHelper.show("Редактировать компанию", 420, (content, dialog) -> {
            TextField nameField = new TextField(company.getName());
            nameField.getStyleClass().add("form-input");
            Label errorLbl = new Label(""); errorLbl.getStyleClass().add("error-text");
            Button saveBtn = StyledButton.primary("Сохранить");
            saveBtn.setOnAction(e -> {
                if (nameField.getText().isBlank()) { errorLbl.setText("Введите название"); return; }
                saveBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        Company c = ApiClient.get().put("/api/company",
                                Map.of("name", nameField.getText()), Company.class);
                        AppState.getInstance().setCompany(c);
                        Platform.runLater(() -> { dialog.close(); renderCompany(c); });
                    } catch (Exception ex) {
                        Platform.runLater(() -> { errorLbl.setText("Ошибка: " + ex.getMessage()); saveBtn.setDisable(false); });
                    }
                }).start();
            });
            Label lbl = new Label("Название компании"); lbl.getStyleClass().add("form-label-dark");
            content.getChildren().addAll(lbl, nameField, errorLbl, saveBtn);
        });
    }

    private void showDeleteCompanyDialog() {
        DialogHelper.show("Удалить компанию", 400, (content, dialog) -> {
            Label msg = new Label("Вы уверены? Все проекты и доски будут удалены.");
            msg.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px;"); msg.setWrapText(true);
            Button delBtn = StyledButton.delete("Да, удалить компанию");
            delBtn.setOnAction(e -> {
                delBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        ApiClient.get().delete("/api/company");
                        AppState.getInstance().setCompany(null);
                        Platform.runLater(() -> { dialog.close(); renderCompany(null); });
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

    private void showAddWorkerDialog(Company company) {
        DialogHelper.show("Добавить сотрудника", 420, (content, dialog) -> {
            TextField emailField = new TextField();
            emailField.getStyleClass().add("form-input");
            emailField.setPromptText("Email сотрудника");
            Label errorLbl = new Label(""); errorLbl.getStyleClass().add("error-text");
            Button addBtn = StyledButton.primary("Добавить");
            addBtn.setOnAction(e -> {
                if (emailField.getText().isBlank()) { errorLbl.setText("Введите email"); return; }
                addBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        ApiClient.get().post("/api/company/workers",
                                Map.of("email", emailField.getText()), Object.class);
                        Platform.runLater(() -> { dialog.close(); loadCompany(); });
                    } catch (Exception ex) {
                        Platform.runLater(() -> { errorLbl.setText("Ошибка: " + ex.getMessage()); addBtn.setDisable(false); });
                    }
                }).start();
            });
            Label lbl = new Label("Email сотрудника"); lbl.getStyleClass().add("form-label-dark");
            content.getChildren().addAll(lbl, emailField, errorLbl, addBtn);
        });
    }

    private void showRemoveWorkerDialog(UserProfile worker) {
        DialogHelper.show("Удалить сотрудника", 400, (content, dialog) -> {
            Label msg = new Label("Удалить " + worker.getName() + " из компании?");
            msg.setStyle("-fx-text-fill: #374151; -fx-font-size: 14px;"); msg.setWrapText(true);
            Button delBtn = StyledButton.delete("Удалить");
            delBtn.setOnAction(e -> {
                delBtn.setDisable(true);
                new Thread(() -> {
                    try {
                        ApiClient.get().delete("/api/company/workers/" + worker.getId());
                        Platform.runLater(() -> { dialog.close(); loadCompany(); });
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

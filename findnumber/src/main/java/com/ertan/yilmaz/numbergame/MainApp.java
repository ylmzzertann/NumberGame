package com.ertan.yilmaz.numbergame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Bu sınıf JavaFX uygulamasını başlatan ve ana pencereyi oluşturan sınıftır.
 */
public class MainApp extends Application {

    // Değişkenler bir kez atandığı için "final" yapıldı.
    private final GameModel gameModel = new GameModel();
    private final Label targetNumberLabel = new Label("?");
    private final HBox numbersBox = new HBox(10);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sayısal Zeka");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        HBox topPanel = new HBox(10);
        topPanel.setAlignment(Pos.CENTER);
        Label targetTextLabel = new Label("Hedef Sayı: ");
        targetTextLabel.setFont(new Font("Arial", 24));
        targetNumberLabel.setFont(new Font("Arial Bold", 36));
        topPanel.getChildren().addAll(targetTextLabel, targetNumberLabel);
        root.setTop(topPanel);
        BorderPane.setAlignment(topPanel, Pos.CENTER);

        numbersBox.setAlignment(Pos.CENTER);
        root.setCenter(numbersBox);

        HBox bottomPanel = new HBox(10);
        bottomPanel.setAlignment(Pos.CENTER);
        Button newGameButton = new Button("Yeni Oyun");
        bottomPanel.getChildren().add(newGameButton);
        root.setBottom(bottomPanel);
        BorderPane.setMargin(bottomPanel, new Insets(20, 0, 0, 0));

        newGameButton.setOnAction(event -> {
            gameModel.startNewGame();
            updateUI();
        });

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        newGameButton.fire();
    }

    private void updateUI() {
        targetNumberLabel.setText(String.valueOf(gameModel.getTargetNumber()));

        numbersBox.getChildren().clear();
        for (int number : gameModel.getAvailableNumbers()) {
            Label numberLabel = new Label(String.valueOf(number));
            numberLabel.setFont(new Font("Arial", 28));
            numberLabel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 15px; -fx-background-radius: 5px;");
            numbersBox.getChildren().add(numberLabel);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

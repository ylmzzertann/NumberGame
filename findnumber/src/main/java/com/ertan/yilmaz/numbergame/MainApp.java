package com.ertan.yilmaz.numbergame;

import java.util.Optional;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainApp extends Application {

    private final GameModel gameModel = new GameModel();
    private Stage mainStage;
    private Scene welcomeScene, gameScene;
    private Timeline timer;

    // Arayüz elemanları
    private final Label targetNumberLabel = new Label("?");
    private final Label messageLabel = new Label("Yeni bir oyuna başlayın veya bir sayı seçin.");
    private final Label totalScoreLabel = new Label("Toplam Puan: 0");
    private final Label timerLabel = new Label("Süre: -");
    private final Label difficultyLabel = new Label("Seviye: -");
    private final HBox numbersBox = new HBox(10);
    private final HBox operatorsBox = new HBox(10);
    private final VBox historyBox = new VBox(5);
    private Button submitGuessButton;

    // Seçim durumunu tutan değişkenler
    private Button selectedNumber1 = null;
    private String selectedOperator = null;
    private Button selectedOperatorButton = null;

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        mainStage.setTitle("Sayısal Zeka");

        // Sahneleri oluştur
        welcomeScene = createWelcomeScene();
        gameScene = createGameScene();

        mainStage.setScene(welcomeScene);
        mainStage.show();
    }

    private Scene createWelcomeScene() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Label welcomeLabel = new Label("Sayısal Zeka Oyununa Hoş Geldiniz!");
        welcomeLabel.setFont(new Font("Arial Bold", 28));

        Label promptLabel = new Label("Lütfen bir zorluk seviyesi seçin:");
        promptLabel.setFont(new Font("Arial", 18));

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button easyButton = new Button("Kolay (90sn, +5 Puan)");
        easyButton.setOnAction(e -> startGame(GameModel.Difficulty.EASY));

        Button mediumButton = new Button("Orta (60sn, +10 Puan)");
        mediumButton.setOnAction(e -> startGame(GameModel.Difficulty.MEDIUM));

        Button hardButton = new Button("Zor (30sn, +20 Puan)");
        hardButton.setOnAction(e -> startGame(GameModel.Difficulty.HARD));

        buttonBox.getChildren().addAll(easyButton, mediumButton, hardButton);
        layout.getChildren().addAll(welcomeLabel, promptLabel, buttonBox);

        return new Scene(layout, 800, 550);
    }

    private Scene createGameScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // Üst Kısım
        HBox statusBox = new HBox(50);
        statusBox.setAlignment(Pos.CENTER);
        totalScoreLabel.setFont(new Font("Arial Bold", 18));
        difficultyLabel.setFont(new Font("Arial Bold", 18));
        timerLabel.setFont(new Font("Arial Bold", 18));
        statusBox.getChildren().addAll(totalScoreLabel, difficultyLabel, timerLabel);

        VBox topPanel = new VBox(10);
        topPanel.setAlignment(Pos.CENTER);
        Label targetTextLabel = new Label("Hedef Sayı: ");
        targetTextLabel.setFont(new Font("Arial", 24));
        targetNumberLabel.setFont(new Font("Arial Bold", 36));
        messageLabel.setFont(new Font("Arial Italic", 14));
        topPanel.getChildren().addAll(statusBox, targetTextLabel, targetNumberLabel, messageLabel);
        root.setTop(topPanel);

        // Orta Kısım
        VBox centerPanel = new VBox(20);
        centerPanel.setAlignment(Pos.CENTER);
        numbersBox.setAlignment(Pos.CENTER);
        operatorsBox.setAlignment(Pos.CENTER);
        createOperatorButtons();
        centerPanel.getChildren().addAll(numbersBox, operatorsBox);
        root.setCenter(centerPanel);

        // Sağ Kısım
        ScrollPane historyScrollPane = new ScrollPane(historyBox);
        historyScrollPane.setPrefWidth(200);
        historyScrollPane.setFitToWidth(true);
        historyBox.setPadding(new Insets(10));
        historyBox.setStyle("-fx-background-color: #f4f4f4;");
        Label historyTitle = new Label("İşlem Geçmişi");
        historyTitle.setFont(new Font("Arial Bold", 16));
        VBox rightPanel = new VBox(10, historyTitle, historyScrollPane);
        rightPanel.setPadding(new Insets(0, 0, 0, 20));
        root.setRight(rightPanel);

        // Alt Kısım
        HBox bottomPanel = new HBox(10);
        bottomPanel.setAlignment(Pos.CENTER);
        Button newGameButton = new Button("Yeni Oyun (Ana Menü)");
        Button clearButton = new Button("Seçimi Temizle");
        submitGuessButton = new Button("Sonucu Bildir");
        submitGuessButton.setDisable(true);
        bottomPanel.getChildren().addAll(newGameButton, clearButton, submitGuessButton);
        root.setBottom(bottomPanel);
        BorderPane.setMargin(bottomPanel, new Insets(20, 0, 0, 0));

        // Olay Yönetimi
        newGameButton.setOnAction(event -> returnToWelcomeScreen());
        clearButton.setOnAction(event -> clearSelection());
        submitGuessButton.setOnAction(event -> submitGuess());

        return new Scene(root, 800, 550);
    }

    private void startGame(GameModel.Difficulty difficulty) {
        gameModel.setDifficulty(difficulty);
        difficultyLabel.setText("Seviye: " + difficulty.getDisplayName());
        mainStage.setScene(gameScene);
        startNewGameSession();
    }

    private void returnToWelcomeScreen() {
        if (timer != null) timer.stop();
        mainStage.setScene(welcomeScene);
    }

    private void startNewGameSession() {
        gameModel.resetTotalScore();
        totalScoreLabel.setText("Toplam Puan: 0");
        startNewRound();
    }

    private void startNewRound() {
        gameModel.startNewRound();
        historyBox.getChildren().clear();
        clearSelection();
        setGameControls(false);
        messageLabel.setText("Birinci sayıyı seçin.");
        setupTimer();
        timer.play();
        updateUI();
    }

    private void setupTimer() {
        if (timer != null) {
            timer.stop();
        }
        final int[] timeLeft = {gameModel.getCurrentDifficulty().getTimeInSeconds()};
        timerLabel.setText("Süre: " + timeLeft[0]);
        timerLabel.setTextFill(Color.BLACK);

        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLeft[0]--;
            timerLabel.setText("Süre: " + timeLeft[0]);
            if (timeLeft[0] <= 10) {
                timerLabel.setTextFill(Color.RED);
            }
            if (timeLeft[0] <= 0) {
                timer.stop();
                handleTimeUp();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
    }
    
    private void handleTimeUp() {
        setGameControls(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Süre Doldu!");
        alert.setHeaderText("Bu tur için süreniz bitti. Puan alamadınız.");
        alert.showAndWait();
        showPlayAgainDialog(0, 0);
    }

    private void createOperatorButtons() {
        operatorsBox.getChildren().clear();
        String[] ops = {"+", "-", "*", "/"};
        for (String op : ops) {
            Button opButton = new Button(op);
            opButton.setFont(new Font("Arial Bold", 24));
            opButton.setOnAction(event -> handleOperatorClick(opButton));
            operatorsBox.getChildren().add(opButton);
        }
    }

    private void handleNumberClick(Button clickedButton) {
        if (selectedOperator != null) {
            int num1 = Integer.parseInt(selectedNumber1.getText());
            int num2 = Integer.parseInt(clickedButton.getText());
            boolean success = gameModel.performOperation(num1, num2, selectedOperator);
            if (success) {
                int result = gameModel.getAvailableNumbers().get(gameModel.getAvailableNumbers().size() - 1);
                String historyEntry = String.format("%d %s %d = %d", num1, selectedOperator, num2, result);
                historyBox.getChildren().add(new Label(historyEntry));
                updateUI();
                messageLabel.setText("İşlem başarılı. Yeni bir sayı seçin.");
            } else {
                messageLabel.setText("Geçersiz işlem! Lütfen seçimi temizleyip tekrar deneyin.");
            }
            clearSelection();
        } else {
            clearSelection();
            selectedNumber1 = clickedButton;
            selectedNumber1.setStyle("-fx-background-color: #a0e0a0; -fx-padding: 15px; -fx-background-radius: 5px;");
            submitGuessButton.setDisable(false);
            messageLabel.setText("Bir operatör seçin veya sonucu bildirin.");
        }
    }

    private void handleOperatorClick(Button clickedOperatorButton) {
        if (selectedNumber1 != null) {
            selectedOperator = clickedOperatorButton.getText();
            selectedOperatorButton = clickedOperatorButton;
            selectedOperatorButton.setStyle("-fx-background-color: #a0a0e0;");
            submitGuessButton.setDisable(true);
            messageLabel.setText("İkinci sayıyı seçin.");
        } else {
            messageLabel.setText("Lütfen önce bir sayı seçin.");
        }
    }

    private void submitGuess() {
        if (selectedNumber1 == null) return;
        timer.stop();

        int finalGuess = Integer.parseInt(selectedNumber1.getText());
        int baseScore = gameModel.calculateBaseScore(finalGuess);
        int bonusPoints = (baseScore > 0) ? gameModel.getCurrentDifficulty().getBonusPoints() : 0;
        int roundScore = baseScore + bonusPoints;

        gameModel.addToTotalScore(roundScore);
        totalScoreLabel.setText("Toplam Puan: " + gameModel.getTotalScore());
        setGameControls(true);

        showPlayAgainDialog(baseScore, bonusPoints);
    }

    private void showPlayAgainDialog(int baseScore, int bonus) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Tur Bitti");
        
        int roundScore = baseScore + bonus;
        String headerText = "Tebrikler! Sayıya ulaştınız ve puanınız: " + roundScore;
        String contentText = String.format("Hesaplama Puanı: %d\nSeviye Bonusu: %d\n\nTekrar oynamak ister misiniz?", baseScore, bonus);

        if (roundScore == 0) {
            headerText = "Hedefe Yeterince Yaklaşamadınız";
            contentText = "Bu turdaki puanınız: 0\n\nTekrar oynamak ister misiniz?";
        }
        
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        ButtonType buttonTypeYes = new ButtonType("Evet");
        ButtonType buttonTypeNo = new ButtonType("Hayır, Bitir");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            startNewRound();
        } else {
            showFinalScore();
        }
    }
    
    private void showFinalScore() {
        Alert finalAlert = new Alert(Alert.AlertType.INFORMATION);
        finalAlert.setTitle("Oyun Bitti!");
        finalAlert.setHeaderText("Harika bir oyun çıkardınız!");
        finalAlert.setContentText("Toplam Puanınız: " + gameModel.getTotalScore());
        finalAlert.showAndWait();
        messageLabel.setText("Oyun bitti! Toplam Puan: " + gameModel.getTotalScore() + ". Yeni bir oyun için ana menüye dönün.");
    }

    private void clearSelection() {
        if (selectedNumber1 != null) {
            selectedNumber1.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 15px; -fx-background-radius: 5px;");
        }
        if (selectedOperatorButton != null) {
            selectedOperatorButton.setStyle(null);
        }
        selectedNumber1 = null;
        selectedOperator = null;
        selectedOperatorButton = null;
        submitGuessButton.setDisable(true);
    }
    
    private void setGameControls(boolean disabled) {
        numbersBox.setDisable(disabled);
        operatorsBox.setDisable(disabled);
        submitGuessButton.setDisable(disabled);
    }

    private void updateUI() {
        targetNumberLabel.setText(String.valueOf(gameModel.getTargetNumber()));
        numbersBox.getChildren().clear();
        for (int number : gameModel.getAvailableNumbers()) {
            Button numberButton = new Button(String.valueOf(number));
            numberButton.setFont(new Font("Arial", 28));
            numberButton.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 15px; -fx-background-radius: 5px;");
            numberButton.setOnAction(event -> handleNumberClick(numberButton));
            numbersBox.getChildren().add(numberButton);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

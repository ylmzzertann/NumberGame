package com.ertan.yilmaz.numbergame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameModel {

    // Zorluk seviyelerini, sürelerini ve bonus puanlarını tutan enum yapısı
    public enum Difficulty {
        EASY(90, 5, "Kolay"),
        MEDIUM(60, 10, "Orta"),
        HARD(30, 20, "Zor");

        private final int timeInSeconds;
        private final int bonusPoints;
        private final String displayName;

        Difficulty(int timeInSeconds, int bonusPoints, String displayName) {
            this.timeInSeconds = timeInSeconds;
            this.bonusPoints = bonusPoints;
            this.displayName = displayName;
        }

        public int getTimeInSeconds() { return timeInSeconds; }
        public int getBonusPoints() { return bonusPoints; }
        public String getDisplayName() { return displayName; }
    }

    private final List<Integer> availableNumbers;
    private int targetNumber;
    private int totalScore = 0;
    private Difficulty currentDifficulty;

    public GameModel() {
        this.availableNumbers = new ArrayList<>();
    }

    public void setDifficulty(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void startNewRound() {
        NumberGenerator generator = new NumberGenerator();
        generator.generateNumbers();
        this.targetNumber = generator.getTargetNumber();
        this.availableNumbers.clear();
        for (int number : generator.getNumbers()) {
            this.availableNumbers.add(number);
        }
    }

    public boolean performOperation(int num1, int num2, String operator) {
        if (!availableNumbers.contains(num1) || !availableNumbers.contains(num2)) {
            return false;
        }
        int result;
        switch (operator) {
            case "+" -> result = num1 + num2;
            case "-" -> {
                if (num1 < num2) return false;
                result = num1 - num2;
            }
            case "*" -> result = num1 * num2;
            case "/" -> {
                if (num2 == 0 || num1 % num2 != 0) return false;
                result = num1 / num2;
            }
            default -> {
                return false;
            }
        }
        availableNumbers.remove(Integer.valueOf(num1));
        availableNumbers.remove(Integer.valueOf(num2));
        availableNumbers.add(result);
        return true;
    }

    public int calculateBaseScore(int finalGuess) {
        int diff = Math.abs(finalGuess - targetNumber);
        return switch (diff) {
            case 0 -> 10;
            case 1 -> 7;
            case 2 -> 5;
            case 3 -> 3;
            default -> 0;
        };
    }

    public void addToTotalScore(int roundScore) {
        this.totalScore += roundScore;
    }

    public void resetTotalScore() {
        this.totalScore = 0;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public List<Integer> getAvailableNumbers() {
        return Collections.unmodifiableList(availableNumbers);
    }

    public int getTargetNumber() {
        return targetNumber;
    }
}
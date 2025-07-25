package com.ertan.yilmaz.numbergame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bu sınıf oyunun tüm durumunu ve çekirdek mantığını yönetir.
 * Arayüzden tamamen bağımsızdır.
 */
public class GameModel {

    private final List<Integer> startingNumbers;
    private final List<Integer> availableNumbers;
    private int targetNumber;

    public GameModel() {
        this.startingNumbers = new ArrayList<>();
        this.availableNumbers = new ArrayList<>();
    }

    /**
     * Yeni bir oyun turu başlatır. Sayıları üretir ve durumu sıfırlar.
     */
    public void startNewGame() {
        NumberGenerator generator = new NumberGenerator();
        generator.generateNumbers();

        this.targetNumber = generator.getTargetNumber();

        this.startingNumbers.clear();
        this.availableNumbers.clear();
        for (int number : generator.getNumbers()) {
            this.startingNumbers.add(number);
            this.availableNumbers.add(number);
        }
    }

    /**
     * Verilen iki sayı ve bir operatör ile matematiksel işlem yapar.
     * @param num1 İlk sayı
     * @param num2 İkinci sayı
     * @param operator İşlem operatörü (+, -, *, /)
     * @return İşlem başarılıysa true, değilse false döner.
     */
    public boolean performOperation(int num1, int num2, String operator) {
        if (!availableNumbers.contains(num1) || !availableNumbers.contains(num2)) {
            return false; // Sayılar mevcut değil
        }

        int result;
        // Daha modern "rule switch" yapısına geçildi.
        switch (operator) {
            case "+" -> result = num1 + num2;
            case "-" -> {
                if (num1 < num2) return false; // Negatif sonuçlara izin verme
                result = num1 - num2;
            }
            case "*" -> result = num1 * num2;
            case "/" -> {
                if (num2 == 0 || num1 % num2 != 0) return false; // Geçersiz bölme
                result = num1 / num2;
            }
            default -> {
                return false; // Geçersiz operatör
            }
        }

        availableNumbers.remove(Integer.valueOf(num1));
        availableNumbers.remove(Integer.valueOf(num2));
        availableNumbers.add(result);

        return true;
    }

    public List<Integer> getAvailableNumbers() {
        return Collections.unmodifiableList(availableNumbers);
    }

    public int getTargetNumber() {
        return targetNumber;
    }
}

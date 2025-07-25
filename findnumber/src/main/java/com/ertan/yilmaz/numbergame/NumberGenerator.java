package com.ertan.yilmaz.numbergame;

import java.util.Random;

public class NumberGenerator {
    // Değişkenler bir kez atandığı için "final" yapıldı.
    private final Random random = new Random();
    private final int[] numbers = new int[6];
    private int targetNumber;

    public void generateNumbers() {
        for (int i = 0; i < 5; i++) {
            numbers[i] = 1 + random.nextInt(9);
        }
        int[] specialNumbers = {25, 50, 75};
        numbers[5] = specialNumbers[random.nextInt(3)];
        targetNumber = 100 + random.nextInt(900);
    }

    public int[] getNumbers() {
        return numbers;
    }

    public int getTargetNumber() {
        return targetNumber;
    }
}

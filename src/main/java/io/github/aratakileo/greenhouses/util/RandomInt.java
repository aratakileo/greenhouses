package io.github.aratakileo.greenhouses.util;

import java.util.Random;

public final class RandomInt {
    private final static Random random = new Random();

    public static int get(int min, int max) {
        return random.nextInt(max - min) + min;
    }
}

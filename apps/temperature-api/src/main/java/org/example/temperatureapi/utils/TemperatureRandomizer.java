package org.example.temperatureapi.utils;

import java.util.Random;

public class TemperatureRandomizer {
    private static final Random RANDOM = new Random();

    private TemperatureRandomizer() {
    }

    public static double getRandomTemperature() {
        var temp = 15 + RANDOM.nextDouble() * 10;
        return Math.round(temp * 10.0) / 10.0;
    }
}

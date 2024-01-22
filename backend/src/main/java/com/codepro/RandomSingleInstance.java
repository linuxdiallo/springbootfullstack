package com.codepro;

import java.util.Random;

public class RandomSingleInstance {
    private static Random INSTANCE;
    public static Random getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Random();
        }
        return INSTANCE;
    }
}

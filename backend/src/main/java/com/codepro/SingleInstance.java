package com.codepro;

import java.security.SecureRandom;
import java.util.Random;

/***
 * This use singleton pattern
 */
public class SingleInstance {
    private static SecureRandom INSTANCE;
    public static Random getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SecureRandom();
        }
        return INSTANCE;
    }
}

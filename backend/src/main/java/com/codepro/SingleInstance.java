package com.codepro;

import java.security.SecureRandom;
import java.util.Random;

/***
 * This use singleton pattern
 */
public class SingleInstance {
    // SecureRandom is more secure than Random
    // this is a sonar report
    private static SecureRandom uniqueInstance;
    public static Random getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new SecureRandom();
        }
        return uniqueInstance;
    }
}

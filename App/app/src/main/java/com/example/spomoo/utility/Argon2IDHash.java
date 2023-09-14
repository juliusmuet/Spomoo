package com.example.spomoo.utility;

/*
 * Argon2IDHash of Spomoo Application
 * Author: Julius Müther
 * License: MIT License
 */

import com.lambdapioneer.argon2kt.Argon2Kt;
import com.lambdapioneer.argon2kt.Argon2KtResult;
import com.lambdapioneer.argon2kt.Argon2Mode;

import java.security.SecureRandom;

/*
 * NOT USED, password is hashed on server
 * Contains methods to hash a String using Argon2ID algorithm and to verify a string against the hashed String
 */
public class Argon2IDHash {

    //parameters of hash algorithm
    private static final int ITERATIONS = 2;
    private static final int MEMORY = 66536;
    private static final int PARALLELISM = 1;
    private static final int OUTPUT_LENGTH = 32;

    //returns the hashed String of the inputted String
    public static String hash(String password){
        Argon2Kt encoder = new Argon2Kt();
        Argon2KtResult result = encoder.hash(Argon2Mode.ARGON2_ID, password.getBytes(), generateSalt16Byte(), ITERATIONS, MEMORY, PARALLELISM, OUTPUT_LENGTH);
        return result.encodedOutputAsString();
    }

    //verifies the inputted String against a hashed String
    public static boolean verify(String clearPassword, String hashedPassword){
        Argon2Kt encoder = new Argon2Kt();
        return encoder.verify(Argon2Mode.ARGON2_ID, hashedPassword, clearPassword.getBytes());
    }

    //generates a random salt
    private static byte[] generateSalt16Byte() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }
}

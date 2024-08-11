package com.alexaytov.ai_hub.utils;

import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AES256EncryptionTest {

    AES256Encryption classUnderTest = new AES256Encryption(generateSecret());

    @Test
    void givenValue_whenEncryption_thenValueIsEncrypted() {
        String encrypted = classUnderTest.encrypt("test");
        assertNotEquals("test", encrypted);
        assertNotNull(encrypted);
    }

    @Test
    void givenEncryptedValue_whenDecrypting_thenCorrectValueIsDecrypted() {
        String encrypted = classUnderTest.encrypt("test");

        String decrypted = classUnderTest.decrypt(encrypted);

        assertEquals("test", decrypted);
    }

    @Test
    void givenExceptionIsThrown_whenEncrypting_thenNullIsReturned() {
        String encrypted = classUnderTest.encrypt(null);

        assertNull(encrypted);
    }

    @Test
    void givenExceptionIsThrown_whenDecrypting_thenNullIsReturned() {
        String decrypted = classUnderTest.decrypt(null);

        assertNull(decrypted);
    }

    // Generate symmetric AES key
    private static String generateSecret() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // for AES-256
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }

}
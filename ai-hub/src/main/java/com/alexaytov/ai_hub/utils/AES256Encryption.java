package com.alexaytov.ai_hub.utils;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AES256Encryption {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final Logger LOGGER = LoggerFactory.getLogger(AES256Encryption.class);

    private final String secret;

    public AES256Encryption(@Value("${api-key.secret}") String secret) {
        this.secret = secret;
    }

    public String encrypt(String strToEncrypt) {
        try {
            byte[] decodedSecret = Base64.getDecoder().decode(this.secret);
            SecretKeySpec secretKey = new SecretKeySpec(decodedSecret, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
        } catch (Exception e) {
            LOGGER.error("Error while encrypting", e);
            return null;
        }
    }

    public String decrypt(String strToDecrypt) {
        try {
            byte[] decodedSecret = Base64.getDecoder().decode(this.secret);
            SecretKeySpec secretKey = new SecretKeySpec(decodedSecret, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            LOGGER.error("Error while decrypting", e);
            return null;
        }
    }
}
package com.udea.bancodigital.shared.event;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility component for AES-GCM encryption/decryption of event payloads.
 * Centralizes crypto logic to avoid duplication across EventPublisher and
 * OutboxProcessor.
 */
@Component
public class AesGcmCryptoUtil {

    // FIX Sonar S2119: SecureRandom reutilizado como campo, no instanciado por
    // llamada
    private final SecureRandom secureRandom = new SecureRandom();

    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    /**
     * Encrypts plaintext using AES-GCM with the provided Base64-encoded key.
     */
    public String encrypt(String plaintext, String encryptionKeyBase64) throws Exception {
        validateKey(encryptionKeyBase64);
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKeyBase64);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Decrypts a Base64-encoded AES-GCM ciphertext using the provided key.
     */
    public String decrypt(String ciphertext, String encryptionKeyBase64) throws Exception {
        validateKey(encryptionKeyBase64);
        byte[] combined = Base64.getDecoder().decode(ciphertext);
        byte[] iv = new byte[IV_LENGTH];
        byte[] encrypted = new byte[combined.length - IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
        System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKeyBase64);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private void validateKey(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalStateException("Encryption key not configured");
        }
    }

}

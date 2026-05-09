package com.udea.bancodigital.shared.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AesGcmCryptoUtilTest {
    private AesGcmCryptoUtil cryptoUtil;
    private String validKey;

    @BeforeEach
    void setUp() {
        cryptoUtil = new AesGcmCryptoUtil();
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        validKey = Base64.getEncoder().encodeToString(key);
    }

    @Test
    void encrypt_ThenDecrypt_ReturnsOriginal() throws Exception {
        String original = "Hello, World!";
        String encrypted = cryptoUtil.encrypt(original, validKey);
        String decrypted = cryptoUtil.decrypt(encrypted, validKey);
        assertEquals(original, decrypted);
    }

    @Test
    void encrypt_NullKey_ThrowsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> cryptoUtil.encrypt("test", null));
    }

    @Test
    void encrypt_EmptyKey_ThrowsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> cryptoUtil.encrypt("test", ""));
    }

    @Test
    void decrypt_NullKey_ThrowsIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> cryptoUtil.decrypt("test", null));
    }

    @Test
    void encrypt_ProducesDifferentCiphertextEachTime() throws Exception {
        String plaintext = "same text";
        String enc1 = cryptoUtil.encrypt(plaintext, validKey);
        String enc2 = cryptoUtil.encrypt(plaintext, validKey);
        // AES-GCM with random IV should produce different ciphertexts
        assertNotEquals(enc1, enc2);
    }

    @Test
    void encrypt_JsonPayload_SuccessfulRoundTrip() throws Exception {
        String json = "{\"eventType\":\"TEST\",\"aggregateId\":\"123\"}";
        String encrypted = cryptoUtil.encrypt(json, validKey);
        String decrypted = cryptoUtil.decrypt(encrypted, validKey);
        assertEquals(json, decrypted);
    }

}

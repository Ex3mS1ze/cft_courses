package com.cft.util;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class CryptoUtilsTest {
    private static final String SECRET_KEY = "EW6zQ87WrwGL1ekm";
    private static final String NORMAL_TEXT_TXT = "normal_text.for_test";
    private static final String ENCRYPTED_TEXT_TXT = "encrypted_text.for_test";

    private byte[] encryptedBytesFromFile;
    private byte[] normalBytesFromFile;

    {
        try {
            encryptedBytesFromFile = Files.readAllBytes(Paths.get(ENCRYPTED_TEXT_TXT));
            normalBytesFromFile = Files.readAllBytes(Paths.get(NORMAL_TEXT_TXT));
        } catch (IOException ignore) {
        }
    }

    @Test
    public void encrypt_returnEncryptedBytesOfText_whenPassValidKeyAndNotEmptyStringWithText() {
        Optional<byte[]> encryptedBytes = CryptoUtils.encrypt(normalBytesFromFile, SECRET_KEY);

        assertTrue(encryptedBytes.isPresent());
        assertArrayEquals(encryptedBytesFromFile, encryptedBytes.get());
    }

    @Test
    public void decrypt_returnDecryptedBytesOfText_whenPassValidKeyAndStringWithEncryptedText() {
        Optional<byte[]> decryptedBytes = CryptoUtils.decrypt(encryptedBytesFromFile, SECRET_KEY);

        assertTrue(decryptedBytes.isPresent());
        assertArrayEquals(normalBytesFromFile, decryptedBytes.get());
    }
}
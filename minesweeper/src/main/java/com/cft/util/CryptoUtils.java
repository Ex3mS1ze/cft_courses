package com.cft.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CryptoUtils {
    private static final String ALGORITHM = "AES";

    public static Optional<byte[]> encrypt(byte[] text, String key) {
        return Optional.ofNullable(doCryptoJob(Cipher.ENCRYPT_MODE, text, key));
    }

    public static Optional<byte[]> decrypt(byte[] text, String key) {
        return Optional.ofNullable(doCryptoJob(Cipher.DECRYPT_MODE, text, key));
    }

    private static byte[] doCryptoJob(int cipherMode, byte[] text, String key) {
        byte[] result = null;

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            cipher.init(cipherMode, secretKey);
            result = cipher.doFinal(text);
        } catch (NoSuchAlgorithmException ignore) {
            log.error("Алгоритм шифрования не найден");
        } catch (InvalidKeyException ignore) {
            log.error("Не валидный ключ");
        } catch (NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Не удалось выполнить работу. Cipher.MODE = '{}'", cipherMode, e);
        }

        return result;
    }
}

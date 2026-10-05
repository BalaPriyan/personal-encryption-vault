package vault.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * Military-grade Cryptographic Engine.
 * Implements AES-256-GCM authenticated encryption and PBKDF2WithHmacSHA256 key derivation.
 */
public class CryptoEngine {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String KDF_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_LENGTH_BITS = 256;
    private static final int GCM_IV_LENGTH_BYTES = 12; // 96-bit recommended for GCM
    private static final int GCM_TAG_LENGTH_BITS = 128; // 128-bit authentication tag
    private static final int DEFAULT_ITERATIONS = 65536;

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a cryptographically secure random salt of specified byte length (default 16 bytes).
     */
    public static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    /**
     * Derives an AES-256 SecretKey from master password and salt using PBKDF2WithHmacSHA256.
     */
    public static SecretKey deriveKey(char[] password, byte[] salt, int iterations) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KDF_ALGORITHM);
            KeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_LENGTH_BITS);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Cryptographic failure during key derivation: " + e.getMessage(), e);
        }
    }

    /**
     * Encrypts plaintext bytes using AES-256-GCM.
     * Returns an EncryptedResult with ciphertext and IV.
     */
    public static EncryptedPayload encrypt(byte[] plaintext, SecretKey secretKey) {
        try {
            byte[] iv = generateRandomBytes(GCM_IV_LENGTH_BYTES);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] ciphertext = cipher.doFinal(plaintext);

            return new EncryptedPayload(
                Base64.getEncoder().encodeToString(ciphertext),
                Base64.getEncoder().encodeToString(iv)
            );
        } catch (Exception e) {
            throw new RuntimeException("Encryption error: " + e.getMessage(), e);
        }
    }

    /**
     * Encrypts a UTF-8 string.
     */
    public static EncryptedPayload encryptText(String text, SecretKey secretKey) {
        return encrypt(text.getBytes(StandardCharsets.UTF_8), secretKey);
    }

    /**
     * Decrypts ciphertext bytes using AES-256-GCM.
     */
    public static byte[] decrypt(String ciphertextBase64, String ivBase64, SecretKey secretKey) {
        try {
            byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);
            byte[] iv = Base64.getDecoder().decode(ivBase64);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            return cipher.doFinal(ciphertext);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed. Invalid key or tampered ciphertext: " + e.getMessage(), e);
        }
    }

    /**
     * Decrypts to a UTF-8 string.
     */
    public static String decryptText(String ciphertextBase64, String ivBase64, SecretKey secretKey) {
        byte[] decryptedBytes = decrypt(ciphertextBase64, ivBase64, secretKey);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    /**
     * Computes salted SHA-256 hash for password verification (zero-knowledge verification).
     */
    public static String hashPassword(char[] password, byte[] salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] pwdBytes = new String(password).getBytes(StandardCharsets.UTF_8);
            byte[] hash = md.digest(pwdBytes);
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constant-time comparison to prevent timing attack side-channels.
     */
    public static boolean verifyHash(String providedHash, String storedHash) {
        if (providedHash == null || storedHash == null) return false;
        byte[] a = providedHash.getBytes(StandardCharsets.UTF_8);
        byte[] b = storedHash.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }

    /**
     * Calculates Shannon Entropy of data string to determine cryptographic randomness or secret density.
     */
    public static double calculateShannonEntropy(String input) {
        if (input == null || input.isEmpty()) return 0.0;
        int[] charCounts = new int[256];
        for (char c : input.toCharArray()) {
            if (c < 256) {
                charCounts[c]++;
            }
        }
        double entropy = 0.0;
        int len = input.length();
        for (int count : charCounts) {
            if (count > 0) {
                double p = (double) count / len;
                entropy -= p * (Math.log(p) / Math.log(2));
            }
        }
        return Math.round(entropy * 100.0) / 100.0;
    }

    /**
     * Helper holder for encrypted payload.
     */
    public static class EncryptedPayload {
        private final String ciphertextBase64;
        private final String ivBase64;

        public EncryptedPayload(String ciphertextBase64, String ivBase64) {
            this.ciphertextBase64 = ciphertextBase64;
            this.ivBase64 = ivBase64;
        }

        public String getCiphertextBase64() { return ciphertextBase64; }
        public String getIvBase64() { return ivBase64; }
    }
}

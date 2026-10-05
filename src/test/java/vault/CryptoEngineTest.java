package vault;

import org.junit.jupiter.api.Test;
import vault.ai.AiSecurityAdvisor;
import vault.ai.DecoyGenerator;
import vault.crypto.CryptoEngine;
import vault.model.ScanResult;
import vault.model.VaultItem;

import javax.crypto.SecretKey;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CryptoEngineTest {

    @Test
    public void testAes256GcmRoundTrip() {
        String secretMessage = "Vault Confidential Secret 2026";
        char[] password = "MasterStrongPassword#99!".toCharArray();
        byte[] salt = CryptoEngine.generateRandomBytes(16);

        SecretKey key = CryptoEngine.deriveKey(password, salt, 65536);
        CryptoEngine.EncryptedPayload payload = CryptoEngine.encryptText(secretMessage, key);

        assertNotNull(payload.getCiphertextBase64());
        assertNotNull(payload.getIvBase64());
        assertNotEquals(secretMessage, payload.getCiphertextBase64());

        // Decrypt
        String decrypted = CryptoEngine.decryptText(payload.getCiphertextBase64(), payload.getIvBase64(), key);
        assertEquals(secretMessage, decrypted);
    }

    @Test
    public void testDecryptionFailsOnTampering() {
        String secretMessage = "Original unaltered data";
        char[] password = "Password123!".toCharArray();
        byte[] salt = CryptoEngine.generateRandomBytes(16);
        SecretKey key = CryptoEngine.deriveKey(password, salt, 10000);

        CryptoEngine.EncryptedPayload payload = CryptoEngine.encryptText(secretMessage, key);

        // Tamper with ciphertext
        String tamperedCiphertext = "X" + payload.getCiphertextBase64().substring(1);

        assertThrows(Exception.class, () -> {
            CryptoEngine.decryptText(tamperedCiphertext, payload.getIvBase64(), key);
        });
    }

    @Test
    public void testPasswordHashingAndVerification() {
        char[] password = "SecretPassphrase2026!".toCharArray();
        byte[] salt = CryptoEngine.generateRandomBytes(16);

        String hash = CryptoEngine.hashPassword(password, salt);
        assertNotNull(hash);

        // Verify correct password
        String correctAttempt = CryptoEngine.hashPassword("SecretPassphrase2026!".toCharArray(), salt);
        assertTrue(CryptoEngine.verifyHash(correctAttempt, hash));

        // Verify wrong password
        String wrongAttempt = CryptoEngine.hashPassword("WrongPassword".toCharArray(), salt);
        assertFalse(CryptoEngine.verifyHash(wrongAttempt, hash));
    }

    @Test
    public void testAiSecurityAdvisorDetectsCriticalSecrets() {
        AiSecurityAdvisor advisor = new AiSecurityAdvisor();

        // Test AWS Key Detection
        String sampleAws = "Production config: AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE and secret credentials.";
        ScanResult scan = advisor.analyze(sampleAws);

        assertEquals("CRITICAL", scan.getRiskLevel());
        assertTrue(scan.getFindings().stream().anyMatch(f -> "AWS_ACCESS_KEY".equals(f.getType())));
        assertTrue(scan.getSuggestedTags().contains("#AWS"));

        // Test Safe Content
        String safeText = "Remember to buy milk and sourdough bread from the bakery on Sunday morning.";
        ScanResult safeScan = advisor.analyze(safeText);
        assertEquals("SAFE", safeScan.getRiskLevel());
    }

    @Test
    public void testDecoyGeneratorPlausibleDeniability() {
        char[] duressPin = "9999".toCharArray();
        byte[] salt = CryptoEngine.generateRandomBytes(16);
        SecretKey decoyKey = CryptoEngine.deriveKey(duressPin, salt, 10000);

        List<VaultItem> decoys = DecoyGenerator.generateDefaultDecoyItems(decoyKey);
        assertFalse(decoys.isEmpty());

        for (VaultItem item : decoys) {
            assertEquals("SAFE", item.getRiskLevel());
            assertNotNull(item.getCiphertextBase64());
            // Decrypt verify
            String plain = CryptoEngine.decryptText(item.getCiphertextBase64(), item.getIvBase64(), decoyKey);
            assertNotNull(plain);
            assertTrue(plain.length() > 5);
        }
    }
}

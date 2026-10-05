package vault.crypto;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vault.ai.AiSecurityAdvisor;
import vault.ai.DecoyGenerator;
import vault.ai.VaultSentinel;
import vault.model.ScanResult;
import vault.model.VaultItem;
import vault.model.VaultPackage;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * High-level Vault Manager controlling state, cryptographic keys, storage, and AI routines.
 */
public class VaultManager {
    private static final String DEFAULT_STORAGE_PATH = "data/vault.enc.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final VaultSentinel sentinel;
    private final AiSecurityAdvisor aiAdvisor;

    private VaultPackage currentPackage;
    private SecretKey activeSecretKey;
    private boolean isUnlocked = false;
    private boolean isDecoyMode = false;

    // Default demo passwords for convenient testing
    public static final String DEMO_MASTER_PASSWORD = "CyberVault2026!SecureKey#99";
    public static final String DEMO_DURESS_PIN = "9999";

    public VaultManager(VaultSentinel sentinel, AiSecurityAdvisor aiAdvisor) {
        this.sentinel = sentinel;
        this.aiAdvisor = aiAdvisor;
        initializeVault();
    }

    /**
     * Initializes the vault from disk or generates demo dataset if missing.
     */
    public synchronized void initializeVault() {
        File file = new File(DEFAULT_STORAGE_PATH);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                this.currentPackage = gson.fromJson(reader, VaultPackage.class);
            } catch (Exception e) {
                System.err.println("Failed to load existing vault file: " + e.getMessage() + ". Rebuilding demo vault.");
                bootstrapDemoVault();
            }
        } else {
            bootstrapDemoVault();
        }
    }

    /**
     * Bootstraps a pre-populated showcase vault with encrypted items, risk ratings, and decoy data.
     */
    private void bootstrapDemoVault() {
        this.currentPackage = new VaultPackage();
        byte[] salt = CryptoEngine.generateRandomBytes(16);
        currentPackage.setSaltBase64(Base64.getEncoder().encodeToString(salt));
        currentPackage.setCreatedAt(System.currentTimeMillis());
        currentPackage.setLastModifiedAt(System.currentTimeMillis());

        // Set master password and duress PIN verifier hashes
        currentPackage.setPasswordVerifierHash(CryptoEngine.hashPassword(DEMO_MASTER_PASSWORD.toCharArray(), salt));
        currentPackage.setDuressPinHash(CryptoEngine.hashPassword(DEMO_DURESS_PIN.toCharArray(), salt));

        // Derive key for bootstrapping initial items
        SecretKey masterKey = CryptoEngine.deriveKey(DEMO_MASTER_PASSWORD.toCharArray(), salt, currentPackage.getKdfIterations());
        SecretKey decoyKey = CryptoEngine.deriveKey(DEMO_DURESS_PIN.toCharArray(), salt, currentPackage.getKdfIterations());

        // Populate realistic demo real items
        List<VaultItem> items = new ArrayList<>();

        // Demo Item 1: AWS Production Credentials
        items.add(createItem(
            "AWS Production Cloud Infrastructure Keys",
            "API_KEY",
            "AKIAIOSFODNN7EXAMPLE\nwJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY\nRegion: us-east-1\nCluster: k8s-prod-primary-us-east",
            masterKey
        ));

        // Demo Item 2: OpenAI API Secret & Project Config
        items.add(createItem(
            "OpenAI LLM Production Agent Key",
            "API_KEY",
            "sk-proj-aB99XyZ1029384756QWERTYUIOPasdfghjklzxcvbnm123456\nModel: gpt-4o\nRate Limit: 10,000 RPM\nAccount: Antigravity-Enterprise-Tier",
            masterKey
        ));

        // Demo Item 3: Confidential Corporate Strategy Note
        items.add(createItem(
            "Q4 Strategic M&A Valuation & Executive Summary",
            "NOTE",
            "Target Acquisition: Project Horizon Cyber Security Ltd.\n" +
            "Valuation Cap: $45,000,000 USD\n" +
            "Lead Partner: sarah.connor@antigravity-holdings.corp\n" +
            "Key escrow shares to be vested across 36 calendar months. Strict non-disclosure binding under Delaware jurisdiction.",
            masterKey
        ));

        // Demo Item 4: Critical Database Admin Root Credential
        items.add(createItem(
            "Primary PostgreSQL Multi-Region DB Connection",
            "CREDENTIAL",
            "postgres://vault_superadmin:SuperVaultPassw0rd_9876543210!@db-cluster.us-east-2.internal.net:5432/core_production?sslmode=verify-full",
            masterKey
        ));

        // Demo Item 5: Encrypted File / Document
        items.add(createItem(
            "Identity_Passport_Digital_Backup.pdf",
            "FILE",
            "JVBERi0xLjQKJcTl8uXrp/Og0MTGCjQgMCBvYmoKPDwKL0ZpbHRlciAvRmxhdGVEZWNvZGUKL0xlbmd0aCAxOTgKPj4Kc3RyZWFtCnic... [Simulated Encrypted Binary PDF Document Payload]",
            masterKey
        ));

        currentPackage.setItems(items);

        // Populate Decoy Items for Duress Mode
        List<VaultItem> decoys = DecoyGenerator.generateDefaultDecoyItems(decoyKey);
        currentPackage.setDecoyItems(decoys);

        saveVaultToDisk();
    }

    private VaultItem createItem(String title, String category, String plaintext, SecretKey key) {
        ScanResult scan = aiAdvisor.analyze(plaintext);
        CryptoEngine.EncryptedPayload payload = CryptoEngine.encryptText(plaintext, key);

        VaultItem item = new VaultItem(
            UUID.randomUUID().toString(),
            title,
            category != null ? category : scan.getSuggestedCategory(),
            payload.getCiphertextBase64(),
            payload.getIvBase64(),
            System.currentTimeMillis(),
            scan.getRiskLevel(),
            scan.getEntropyScore(),
            scan.getSuggestedTags(),
            generatePreview(plaintext),
            category.equals("FILE") ? title : null,
            plaintext.getBytes(StandardCharsets.UTF_8).length
        );
        return item;
    }

    private String generatePreview(String plaintext) {
        if (plaintext.length() <= 80) return plaintext;
        return plaintext.substring(0, 77) + "...";
    }

    public synchronized void saveVaultToDisk() {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdirs();
            currentPackage.setLastModifiedAt(System.currentTimeMillis());
            try (FileWriter writer = new FileWriter(DEFAULT_STORAGE_PATH, StandardCharsets.UTF_8)) {
                gson.toJson(currentPackage, writer);
            }
        } catch (Exception e) {
            System.err.println("Error saving vault to disk: " + e.getMessage());
        }
    }

    /**
     * Attempts to unlock the vault.
     * Supports both Master Password and Duress PIN.
     */
    public synchronized boolean unlock(char[] passwordOrPin, String clientIp) {
        byte[] salt = Base64.getDecoder().decode(currentPackage.getSaltBase64());
        String attemptHash = CryptoEngine.hashPassword(passwordOrPin, salt);

        // Check if matching master password
        if (CryptoEngine.verifyHash(attemptHash, currentPackage.getPasswordVerifierHash())) {
            this.activeSecretKey = CryptoEngine.deriveKey(passwordOrPin, salt, currentPackage.getKdfIterations());
            this.isUnlocked = true;
            this.isDecoyMode = false;
            sentinel.recordEvent("VAULT_UNLOCK_SUCCESS", "Master Vault successfully authenticated with AES-256 key.", "SUCCESS", clientIp);
            return true;
        }

        // Check if matching duress PIN
        if (CryptoEngine.verifyHash(attemptHash, currentPackage.getDuressPinHash())) {
            this.activeSecretKey = CryptoEngine.deriveKey(passwordOrPin, salt, currentPackage.getKdfIterations());
            this.isUnlocked = true;
            this.isDecoyMode = true;
            sentinel.recordEvent("DURESS_TRIGGERED", "Duress emergency authentication used. Decoy vault displayed.", "ANOMALY_WARNING", clientIp);
            return true;
        }

        sentinel.recordEvent("VAULT_UNLOCK_FAILED", "Failed authentication attempt.", "DENIED", clientIp);
        return false;
    }

    public synchronized void lock(String clientIp) {
        this.activeSecretKey = null;
        this.isUnlocked = false;
        this.isDecoyMode = false;
        sentinel.recordEvent("VAULT_LOCKED", "Session locked. In-memory AES keys wiped.", "SUCCESS", clientIp);
    }

    public synchronized List<VaultItem> getItems() {
        if (!isUnlocked) {
            return Collections.emptyList();
        }
        if (isDecoyMode) {
            return currentPackage.getDecoyItems();
        }
        return currentPackage.getItems();
    }

    public synchronized VaultItem addItem(String title, String category, String plaintext, String clientIp) {
        if (!isUnlocked || activeSecretKey == null) {
            throw new IllegalStateException("Vault is locked");
        }
        VaultItem item = createItem(title, category, plaintext, activeSecretKey);
        if (isDecoyMode) {
            currentPackage.getDecoyItems().add(item);
        } else {
            currentPackage.getItems().add(item);
        }
        saveVaultToDisk();
        sentinel.recordEvent("ITEM_ENCRYPTED", "New item encrypted & committed: " + title + " (Category: " + item.getCategory() + ")", "SUCCESS", clientIp);
        return item;
    }

    public synchronized String decryptItem(String itemId, String clientIp) {
        if (!isUnlocked || activeSecretKey == null) {
            throw new IllegalStateException("Vault is locked");
        }
        List<VaultItem> list = isDecoyMode ? currentPackage.getDecoyItems() : currentPackage.getItems();
        for (VaultItem item : list) {
            if (item.getId().equals(itemId)) {
                String plain = CryptoEngine.decryptText(item.getCiphertextBase64(), item.getIvBase64(), activeSecretKey);
                sentinel.recordEvent("ITEM_DECRYPTED", "Decrypted item ID: " + itemId + " (" + item.getTitle() + ")", "SUCCESS", clientIp);
                return plain;
            }
        }
        throw new NoSuchElementException("Item not found");
    }

    public synchronized boolean deleteItem(String itemId, String clientIp) {
        if (!isUnlocked) throw new IllegalStateException("Vault is locked");
        List<VaultItem> list = isDecoyMode ? currentPackage.getDecoyItems() : currentPackage.getItems();
        boolean removed = list.removeIf(i -> i.getId().equals(itemId));
        if (removed) {
            saveVaultToDisk();
            sentinel.recordEvent("ITEM_DELETED", "Item removed from vault: " + itemId, "SUCCESS", clientIp);
        }
        return removed;
    }

    public synchronized Map<String, Object> getMetrics() {
        Map<String, Object> map = new HashMap<>();
        map.put("isUnlocked", isUnlocked);
        map.put("isDecoyMode", isDecoyMode);
        map.put("algorithm", currentPackage != null ? currentPackage.getAlgorithm() : "AES/GCM/NoPadding");
        map.put("kdfIterations", currentPackage != null ? currentPackage.getKdfIterations() : 65536);
        map.put("itemCount", isUnlocked ? (isDecoyMode ? currentPackage.getDecoyItems().size() : currentPackage.getItems().size()) : 0);
        map.put("anomaliesDetected", sentinel.getAnomalyCount());
        
        // Compute vault health score (0 - 100)
        int health = 100;
        if (sentinel.getAnomalyCount() > 0) health -= Math.min(30, sentinel.getAnomalyCount() * 10);
        if (sentinel.getFailedLogins() > 0) health -= Math.min(20, sentinel.getFailedLogins() * 10);
        map.put("vaultHealthScore", Math.max(20, health));
        return map;
    }

    public synchronized String exportVaultJson() {
        return gson.toJson(currentPackage);
    }

    public synchronized void importVaultJson(String json, String clientIp) {
        VaultPackage imported = gson.fromJson(json, VaultPackage.class);
        if (imported == null || imported.getSaltBase64() == null) {
            throw new IllegalArgumentException("Invalid vault package format");
        }
        this.currentPackage = imported;
        this.isUnlocked = false;
        this.activeSecretKey = null;
        saveVaultToDisk();
        sentinel.recordEvent("VAULT_IMPORTED", "Vault package imported from external backup", "SUCCESS", clientIp);
    }

    public boolean isUnlocked() { return isUnlocked; }
    public boolean isDecoyMode() { return isDecoyMode; }
    public VaultPackage getCurrentPackage() { return currentPackage; }
}

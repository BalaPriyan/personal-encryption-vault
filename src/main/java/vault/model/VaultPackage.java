package vault.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Vault container structure for persistence and file export/import.
 */
public class VaultPackage {
    private String version = "2.0-AI-GCM";
    private String saltBase64;
    private String passwordVerifierHash; // SHA-256 with salt to verify password correctness without storing password
    private String duressPinHash; // SHA-256 with salt for decoy vault activation
    private int kdfIterations = 65536;
    private String algorithm = "AES/GCM/NoPadding";
    private long createdAt;
    private long lastModifiedAt;
    private List<VaultItem> items = new ArrayList<>();
    private List<VaultItem> decoyItems = new ArrayList<>();

    public VaultPackage() {}

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getSaltBase64() { return saltBase64; }
    public void setSaltBase64(String saltBase64) { this.saltBase64 = saltBase64; }

    public String getPasswordVerifierHash() { return passwordVerifierHash; }
    public void setPasswordVerifierHash(String passwordVerifierHash) { this.passwordVerifierHash = passwordVerifierHash; }

    public String getDuressPinHash() { return duressPinHash; }
    public void setDuressPinHash(String duressPinHash) { this.duressPinHash = duressPinHash; }

    public int getKdfIterations() { return kdfIterations; }
    public void setKdfIterations(int kdfIterations) { this.kdfIterations = kdfIterations; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(long lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    public List<VaultItem> getItems() { return items; }
    public void setItems(List<VaultItem> items) { this.items = items; }

    public List<VaultItem> getDecoyItems() { return decoyItems; }
    public void setDecoyItems(List<VaultItem> decoyItems) { this.decoyItems = decoyItems; }
}

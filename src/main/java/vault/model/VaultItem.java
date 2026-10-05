package vault.model;

import java.util.List;
import java.util.Map;

/**
 * Model representing an item stored in the encryption vault.
 * Sensitive data is encrypted using AES-256-GCM.
 */
public class VaultItem {
    private String id;
    private String title;
    private String category; // NOTE, CREDENTIAL, API_KEY, FILE, SECURE_DOCUMENT
    private String ciphertextBase64;
    private String ivBase64;
    private String authTagBase64; // GCM auth tag or embedded in ciphertext
    private long createdAt;
    private long updatedAt;
    
    // AI Metadata (unencrypted tags and risk indicators)
    private String riskLevel; // SAFE, LOW, MEDIUM, HIGH, CRITICAL
    private int entropyScore;
    private List<String> aiTags;
    private String previewDescription;
    private String originalFileName; // for files
    private long fileSize; // in bytes

    public VaultItem() {}

    public VaultItem(String id, String title, String category, String ciphertextBase64, String ivBase64, 
                     long createdAt, String riskLevel, int entropyScore, List<String> aiTags, 
                     String previewDescription, String originalFileName, long fileSize) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.ciphertextBase64 = ciphertextBase64;
        this.ivBase64 = ivBase64;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.riskLevel = riskLevel;
        this.entropyScore = entropyScore;
        this.aiTags = aiTags;
        this.previewDescription = previewDescription;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCiphertextBase64() { return ciphertextBase64; }
    public void setCiphertextBase64(String ciphertextBase64) { this.ciphertextBase64 = ciphertextBase64; }

    public String getIvBase64() { return ivBase64; }
    public void setIvBase64(String ivBase64) { this.ivBase64 = ivBase64; }

    public String getAuthTagBase64() { return authTagBase64; }
    public void setAuthTagBase64(String authTagBase64) { this.authTagBase64 = authTagBase64; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public int getEntropyScore() { return entropyScore; }
    public void setEntropyScore(int entropyScore) { this.entropyScore = entropyScore; }

    public List<String> getAiTags() { return aiTags; }
    public void setAiTags(List<String> aiTags) { this.aiTags = aiTags; }

    public String getPreviewDescription() { return previewDescription; }
    public void setPreviewDescription(String previewDescription) { this.previewDescription = previewDescription; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
}

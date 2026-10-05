package vault.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of an AI Privacy, PII, and Security Scan.
 */
public class ScanResult {
    private String riskLevel; // SAFE, LOW, MEDIUM, HIGH, CRITICAL
    private int entropyScore; // 0 - 100
    private double shannonEntropy;
    private List<Finding> findings = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();
    private List<String> suggestedTags = new ArrayList<>();
    private String suggestedCategory;

    public static class Finding {
        private String type; // e.g. "AWS_KEY", "OPENAI_KEY", "PRIVATE_KEY", "CREDIT_CARD", "PASSWORD", "EMAIL", "JWT"
        private String description;
        private String snippet;
        private String severity; // CRITICAL, HIGH, MEDIUM, LOW

        public Finding(String type, String description, String snippet, String severity) {
            this.type = type;
            this.description = description;
            this.snippet = snippet;
            this.severity = severity;
        }

        public String getType() { return type; }
        public String getDescription() { return description; }
        public String getSnippet() { return snippet; }
        public String getSeverity() { return severity; }
    }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public int getEntropyScore() { return entropyScore; }
    public void setEntropyScore(int entropyScore) { this.entropyScore = entropyScore; }

    public double getShannonEntropy() { return shannonEntropy; }
    public void setShannonEntropy(double shannonEntropy) { this.shannonEntropy = shannonEntropy; }

    public List<Finding> getFindings() { return findings; }
    public void setFindings(List<Finding> findings) { this.findings = findings; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

    public List<String> getSuggestedTags() { return suggestedTags; }
    public void setSuggestedTags(List<String> suggestedTags) { this.suggestedTags = suggestedTags; }

    public String getSuggestedCategory() { return suggestedCategory; }
    public void setSuggestedCategory(String suggestedCategory) { this.suggestedCategory = suggestedCategory; }
}

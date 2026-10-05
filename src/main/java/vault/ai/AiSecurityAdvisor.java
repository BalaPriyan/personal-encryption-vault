package vault.ai;

import vault.crypto.CryptoEngine;
import vault.model.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI-Assisted Security, PII, and Threat Intelligence Analyzer.
 * Performs deep inspection of candidate vault data before encryption.
 */
public class AiSecurityAdvisor {

    // Regex patterns for secret detection
    private static final Pattern AWS_KEY_PATTERN = Pattern.compile("AKIA[0-9A-Z]{16}");
    private static final Pattern OPENAI_KEY_PATTERN = Pattern.compile("sk-[a-zA-Z0-9_-]{20,}");
    private static final Pattern GITHUB_TOKEN_PATTERN = Pattern.compile("gh[pousr]_[A-Za-z0-9_]{36,}");
    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile("-----BEGIN (?:RSA |EC |OPENSSH |DSA )?PRIVATE KEY-----");
    private static final Pattern JWT_PATTERN = Pattern.compile("eyJ[A-Za-z0-9_-]{10,}\\.eyJ[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}");
    private static final Pattern CREDIT_CARD_PATTERN = Pattern.compile("\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11})\\b");
    private static final Pattern SSN_PATTERN = Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern GENERIC_SECRET_PATTERN = Pattern.compile("(?i)(password|secret|apikey|token|passwd|pwd)\\s*[:=]\\s*['\"]?([^'\"\\s]{6,})['\"]?");
    private static final Pattern DB_CONN_PATTERN = Pattern.compile("(?i)(mongodb|postgres|mysql|redis)://[^\\s]+");

    public ScanResult analyze(String text) {
        ScanResult result = new ScanResult();
        if (text == null || text.trim().isEmpty()) {
            result.setRiskLevel("SAFE");
            result.setEntropyScore(0);
            result.setShannonEntropy(0.0);
            result.setSuggestedCategory("NOTE");
            return result;
        }

        double shannon = CryptoEngine.calculateShannonEntropy(text);
        result.setShannonEntropy(shannon);

        List<ScanResult.Finding> findings = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        boolean hasCritical = false;
        boolean hasHigh = false;
        boolean hasMedium = false;

        // 1. Private Key Detection
        if (PRIVATE_KEY_PATTERN.matcher(text).find()) {
            findings.add(new ScanResult.Finding("PRIVATE_KEY", "Unencrypted Cryptographic Private Key detected (PEM/SSH)", "PRIVATE KEY block found", "CRITICAL"));
            recommendations.add("Critical: Ensure this private key is never committed to code repositories or sent over unencrypted channels.");
            tags.add("#PrivateKey");
            tags.add("#Infrastructure");
            hasCritical = true;
        }

        // 2. Cloud Provider / AWS Key
        Matcher awsMatcher = AWS_KEY_PATTERN.matcher(text);
        if (awsMatcher.find()) {
            String masked = maskSecret(awsMatcher.group());
            findings.add(new ScanResult.Finding("AWS_ACCESS_KEY", "AWS IAM Access Key ID detected", masked, "CRITICAL"));
            recommendations.add("Ensure accompanying AWS Secret Access Key is also safeguarded and rotated regularly via AWS IAM.");
            tags.add("#Cloud");
            tags.add("#AWS");
            hasCritical = true;
        }

        // 3. OpenAI / AI API Key
        Matcher openaiMatcher = OPENAI_KEY_PATTERN.matcher(text);
        if (openaiMatcher.find()) {
            String masked = maskSecret(openaiMatcher.group());
            findings.add(new ScanResult.Finding("AI_API_TOKEN", "OpenAI / LLM API Key detected", masked, "HIGH"));
            recommendations.add("Consider setting monthly usage limits on your LLM API account to mitigate leak exposure.");
            tags.add("#AI-Service");
            tags.add("#APIKey");
            hasHigh = true;
        }

        // 4. GitHub Token
        Matcher ghMatcher = GITHUB_TOKEN_PATTERN.matcher(text);
        if (ghMatcher.find()) {
            String masked = maskSecret(ghMatcher.group());
            findings.add(new ScanResult.Finding("GITHUB_TOKEN", "GitHub Personal Access Token detected", masked, "HIGH"));
            recommendations.add("Restrict fine-grained permissions for this GitHub token to minimal necessary repository scopes.");
            tags.add("#GitHub");
            tags.add("#Developer");
            hasHigh = true;
        }

        // 5. Database Connection String
        Matcher dbMatcher = DB_CONN_PATTERN.matcher(text);
        if (dbMatcher.find()) {
            findings.add(new ScanResult.Finding("DATABASE_URI", "Database Connection URI containing credentials detected", maskSecret(dbMatcher.group()), "CRITICAL"));
            recommendations.add("Database URIs should be restricted by network IP firewalls or AWS VPC peering.");
            tags.add("#Database");
            hasCritical = true;
        }

        // 6. JWT Token
        if (JWT_PATTERN.matcher(text).find()) {
            findings.add(new ScanResult.Finding("JWT_BEARER", "JSON Web Token (JWT) detected", "eyJ...", "HIGH"));
            recommendations.add("Verify token expiration time (exp claim); short-lived tokens reduce breach surface area.");
            tags.add("#SessionToken");
            hasHigh = true;
        }

        // 7. Credit Card Numbers
        Matcher ccMatcher = CREDIT_CARD_PATTERN.matcher(text);
        if (ccMatcher.find()) {
            String cc = ccMatcher.group();
            findings.add(new ScanResult.Finding("CREDIT_CARD", "Payment Card Industry (PCI) primary account number candidate", maskSecret(cc), "CRITICAL"));
            recommendations.add("PCI-DSS standard mandates strong encryption. Never share CVV security codes.");
            tags.add("#PCI-DSS");
            tags.add("#Financial");
            hasCritical = true;
        }

        // 8. SSN
        Matcher ssnMatcher = SSN_PATTERN.matcher(text);
        if (ssnMatcher.find()) {
            findings.add(new ScanResult.Finding("PII_SSN", "Social Security / National Identity Number format detected", "***-**-****", "HIGH"));
            recommendations.add("PII data requires zero-knowledge client-side encryption. Stored locally with AES-256.");
            tags.add("#PII");
            tags.add("#Identity");
            hasHigh = true;
        }

        // 9. Generic password/secret key-value
        Matcher genericMatcher = GENERIC_SECRET_PATTERN.matcher(text);
        if (genericMatcher.find()) {
            findings.add(new ScanResult.Finding("CREDENTIAL_PAIR", "Hardcoded credential assignment detected ('" + genericMatcher.group(1) + "')", "Secret value detected", "HIGH"));
            tags.add("#Credentials");
            hasHigh = true;
        }

        // 10. Email addresses
        Matcher emailMatcher = EMAIL_PATTERN.matcher(text);
        int emailCount = 0;
        while (emailMatcher.find()) {
            emailCount++;
        }
        if (emailCount > 0) {
            findings.add(new ScanResult.Finding("EMAIL_ADDRESS", emailCount + " Email address(es) identified", "", "LOW"));
            tags.add("#Contact");
            hasMedium = true;
        }

        // Determine Risk Level & Normalized Entropy Score
        String risk;
        int entropyScoreNormalized;

        if (hasCritical) {
            risk = "CRITICAL";
            entropyScoreNormalized = Math.min(100, (int) (shannon * 14 + 30));
        } else if (hasHigh) {
            risk = "HIGH";
            entropyScoreNormalized = Math.min(100, (int) (shannon * 14 + 15));
        } else if (hasMedium) {
            risk = "MEDIUM";
            entropyScoreNormalized = Math.min(100, (int) (shannon * 14));
        } else if (shannon > 4.5) {
            risk = "LOW";
            findings.add(new ScanResult.Finding("HIGH_ENTROPY", "High Shannon entropy (" + shannon + ") indicates encrypted data or random tokens", "", "LOW"));
            entropyScoreNormalized = (int) (shannon * 14);
        } else {
            risk = "SAFE";
            entropyScoreNormalized = Math.max(10, (int) (shannon * 10));
        }

        // Suggest category
        String suggestedCategory = "NOTE";
        if (tags.contains("#AWS") || tags.contains("#AI-Service") || tags.contains("#GitHub") || tags.contains("#APIKey")) {
            suggestedCategory = "API_KEY";
        } else if (tags.contains("#Credentials") || tags.contains("#SessionToken")) {
            suggestedCategory = "CREDENTIAL";
        } else if (tags.contains("#Financial") || tags.contains("#PII") || tags.contains("#PrivateKey")) {
            suggestedCategory = "SECURE_DOCUMENT";
        }

        if (tags.isEmpty()) {
            tags.add("#EncryptedVault");
            tags.add("#Personal");
        }

        result.setRiskLevel(risk);
        result.setEntropyScore(Math.min(100, entropyScoreNormalized));
        result.setFindings(findings);
        result.setRecommendations(recommendations);
        result.setSuggestedTags(tags);
        result.setSuggestedCategory(suggestedCategory);

        return result;
    }

    private String maskSecret(String secret) {
        if (secret.length() <= 8) return "••••••••";
        return secret.substring(0, 4) + "••••••••" + secret.substring(secret.length() - 4);
    }
}

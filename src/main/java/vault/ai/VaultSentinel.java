package vault.ai;

import vault.model.AuditEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * AI Behavioral Sentinel & Intrusion Detector.
 * Monitors vault operational velocity, brute force patterns, and security anomalies.
 */
public class VaultSentinel {
    private final ConcurrentLinkedDeque<AuditEvent> auditHistory = new ConcurrentLinkedDeque<>();
    private final int MAX_AUDIT_LOGS = 100;
    
    // Anomaly tracking metrics
    private int consecutiveFailedLogins = 0;
    private long lastDecryptTimestamp = 0;
    private int decryptBurstCounter = 0;
    private int anomalyCounter = 0;

    public synchronized void recordEvent(String action, String details, String status, String clientIp) {
        long now = System.currentTimeMillis();
        AuditEvent event = new AuditEvent(now, action, details, status, clientIp);
        auditHistory.addFirst(event);
        while (auditHistory.size() > MAX_AUDIT_LOGS) {
            auditHistory.removeLast();
        }

        // Anomaly Evaluation Rule 1: Brute-force detection
        if ("VAULT_UNLOCK_FAILED".equals(action)) {
            consecutiveFailedLogins++;
            if (consecutiveFailedLogins >= 3) {
                recordAnomaly("POTENTIAL_BRUTE_FORCE", "Repeated failed authentication attempts detected (" + consecutiveFailedLogins + " attempts). Initiating adaptive key derivation delay.");
            }
        } else if ("VAULT_UNLOCK_SUCCESS".equals(action)) {
            consecutiveFailedLogins = 0;
        }

        // Anomaly Evaluation Rule 2: Decryption Burst Velocity
        if ("ITEM_DECRYPTED".equals(action)) {
            if (now - lastDecryptTimestamp < 1500) {
                decryptBurstCounter++;
                if (decryptBurstCounter >= 5) {
                    recordAnomaly("RAPID_DECRYPT_EXFILTRATION", "Rapid bulk decryption velocity detected (" + decryptBurstCounter + " items in <5s). Flagging possible exfiltration pattern.");
                }
            } else {
                decryptBurstCounter = 1;
            }
            lastDecryptTimestamp = now;
        }

        // Anomaly Evaluation Rule 3: Duress Mode Triggered
        if ("DURESS_TRIGGERED".equals(action)) {
            recordAnomaly("DURESS_EMERGENCY_DECOY", "Duress emergency protocol engaged. Real vault sealed. Safe decoy profile presented.");
        }
    }

    private void recordAnomaly(String alertType, String description) {
        anomalyCounter++;
        auditHistory.addFirst(new AuditEvent(
            System.currentTimeMillis(),
            "AI_SENTINEL_ALERT: " + alertType,
            description,
            "ANOMALY_WARNING",
            "127.0.0.1"
        ));
    }

    public List<AuditEvent> getRecentEvents(int limit) {
        List<AuditEvent> list = new ArrayList<>(auditHistory);
        if (list.size() > limit) {
            return list.subList(0, limit);
        }
        return list;
    }

    public int getAnomalyCount() {
        return anomalyCounter;
    }

    public int getFailedLogins() {
        return consecutiveFailedLogins;
    }
}

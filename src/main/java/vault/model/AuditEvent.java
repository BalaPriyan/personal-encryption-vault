package vault.model;

/**
 * Security audit event for real-time monitoring and anomaly detection.
 */
public class AuditEvent {
    private long timestamp;
    private String action; // e.g. "VAULT_UNLOCK", "ITEM_ENCRYPTED", "ITEM_DECRYPTED", "DURESS_TRIGGERED", "INTEGRITY_CHECK"
    private String details;
    private String status; // "SUCCESS", "ANOMALY_WARNING", "DENIED"
    private String ipAddress;

    public AuditEvent() {}

    public AuditEvent(long timestamp, String action, String details, String status, String ipAddress) {
        this.timestamp = timestamp;
        this.action = action;
        this.details = details;
        this.status = status;
        this.ipAddress = ipAddress;
    }

    public long getTimestamp() { return timestamp; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public String getStatus() { return status; }
    public String getIpAddress() { return ipAddress; }
}

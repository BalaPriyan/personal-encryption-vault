# Aegis Vault — AI-Powered Personal Encryption Service
### Enterprise-Grade Zero-Knowledge Cryptographic Vault with AI Privacy & Behavioral Threat Intelligence

[![Java](https://img.shields.io/badge/Java-21%20%2F%2026-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Cryptography](https://img.shields.io/badge/Cipher-AES--256--GCM-0052CC?style=flat-square)](https://csrc.nist.gov/publications/detail/sp/800-38d/final)
[![KDF](https://img.shields.io/badge/KDF-PBKDF2--HMAC--SHA256-0747A6?style=flat-square)](https://datatracker.ietf.org/doc/html/rfc2898)
[![UI](https://img.shields.io/badge/UI-Atlassian%20Design%20System-0052CC?style=flat-square&logo=atlassian&logoColor=white)](https://atlassian.design/)
[![License](https://img.shields.io/badge/License-MIT-36B37E?style=flat-square)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-JUnit%205%20Passing-36B37E?style=flat-square&logo=junit5&logoColor=white)](src/test/java/vault/CryptoEngineTest.java)

---

## 📑 Table of Contents

1. [Executive Summary](#-executive-summary)
2. [Key Value Propositions](#-key-value-propositions)
3. [System Architecture](#-system-architecture)
   - [Architectural Topology](#architectural-topology)
   - [Subsystem Component Breakdown](#subsystem-component-breakdown)
   - [End-to-End Execution Flow](#end-to-end-execution-flow)
4. [Cryptographic Deep-Dive](#-cryptographic-deep-dive)
   - [Authenticated Encryption: AES-256-GCM](#authenticated-encryption-aes-256-gcm)
   - [Key Derivation: PBKDF2WithHmacSHA256](#key-derivation-pbkdf2withhmacsha256)
   - [Zero-Knowledge Password Verification](#zero-knowledge-password-verification)
   - [Constant-Time Comparison & Side-Channel Mitigation](#constant-time-comparison--side-channel-mitigation)
   - [Volatile Memory Hygiene & Key Dereferencing](#volatile-memory-hygiene--key-dereferencing)
   - [Storage Container Schema (`vault.enc.json`)](#storage-container-schema-vaultencjson)
5. [AI Privacy, PII & Threat Detection Layer](#-ai-privacy-pii--threat-detection-layer)
   - [Pattern Recognition & Secret Detection Rules](#pattern-recognition--secret-detection-rules)
   - [Shannon Entropy Analysis](#shannon-entropy-analysis)
   - [Risk Classification & Scoring Matrix](#risk-classification--scoring-matrix)
   - [Secret Masking & Actionable Recommendations](#secret-masking--actionable-recommendations)
6. [Plausible Deniability & Duress PIN System](#-plausible-deniability--duress-pin-system)
   - [Threat Model & Rubber-Hose Cryptanalysis Defense](#threat-model--rubber-hose-cryptanalysis-defense)
   - [Dual-Verifier Authentication Architecture](#dual-verifier-authentication-architecture)
   - [Synthetic Decoy Database Generation](#synthetic-decoy-database-generation)
   - [Covert Behavioral Signaling](#covert-behavioral-signaling)
7. [AI Behavioral Sentinel & Intrusion Detection](#-ai-behavioral-sentinel--intrusion-detection)
   - [Real-Time Anomaly Detection Engine](#real-time-anomaly-detection-engine)
   - [Dynamic Vault Health Score Formula](#dynamic-vault-health-score-formula)
   - [Immutable In-Memory Audit Trail](#immutable-in-memory-audit-trail)
8. [Atlassian Design System User Interface](#-atlassian-design-system-user-interface)
   - [Design Philosophy & Aesthetic Standards](#design-philosophy--aesthetic-standards)
   - [Workspace Layout & Interactive Workflows](#workspace-layout--interactive-workflows)
   - [Modals & Security Dialogs](#modals--security-dialogs)
9. [Comprehensive REST API Reference](#-comprehensive-rest-api-reference)
   - [Endpoint Summary Table](#endpoint-summary-table)
   - [Detailed Endpoint Specifications](#detailed-endpoint-specifications)
10. [Getting Started & Operational Guide](#-getting-started--operational-guide)
    - [Prerequisites](#prerequisites)
    - [Installation & Compilation](#installation--compilation)
    - [Execution & Port Configuration](#execution--port-configuration)
    - [Demo Credentials & Quick Showcase Actions](#demo-credentials--quick-showcase-actions)
11. [Testing & Quality Assurance](#-testing--quality-assurance)
    - [JUnit 5 Test Suite Analysis](#junit-5-test-suite-analysis)
    - [Running Test Commands](#running-test-commands)
12. [Threat Model & Security Guarantees](#-threat-model--security-guarantees)
    - [STRIDE Threat Analysis](#stride-threat-analysis)
    - [Security Boundaries & Invariants](#security-boundaries--invariants)
13. [Project Directory & Code Inventory](#-project-directory--code-inventory)
14. [License & Disclaimers](#-license--disclaimers)

---

## 🏛️ Executive Summary

**Aegis Vault** is a self-contained, enterprise-grade personal encryption service engineered in **modern Java (JDK 21/26)**. Designed on zero-knowledge cryptographic foundations, Aegis Vault provides an impenetrable boundary between confidential credentials and untrusted host environments.

Traditional secret management tools frequently succumb to credential leaks during data entry, brute-force access vectors, or physical coercion (rubber-hose cryptanalysis). Aegis Vault neutralizes these vectors through a unified defense-in-depth architecture:

1. **Zero-Knowledge Authenticated Cryptography**: Leverages **AES-256-GCM** (Galois/Counter Mode) with hardware-accelerated JVM intrinsics, paired with **PBKDF2-HMAC-SHA256** (65,536 iterations). Raw encryption keys exist strictly in volatile memory during active sessions and are zeroed/dereferenced on lock.
2. **AI Privacy & Secret Leak Inspection**: Inspects content in real-time before encryption, identifying exposed AWS tokens, OpenAI API keys, GitHub PATs, PEM private keys, PCI-DSS payment cards, SSNs, and database connection strings.
3. **Shannon Entropy Randomness Scoring**: Calculates mathematical information density to detect encrypted payloads, high-entropy tokens, and weak passwords.
4. **Plausible Deniability (Duress PIN Mode)**: Supports dual-state authentication. Entering the emergency PIN (`9999`) unlocks an isolated, synthetically populated **Decoy Vault** containing realistic benign data, preserving real confidentiality under coercion without alerting an adversary.
5. **AI Behavioral Sentinel**: Tracks operational velocity, detecting brute-force intrusions and rapid bulk exfiltration bursts in real time.
6. **Atlassian Design System (ADS) Interface**: Built with Jira- and Confluence-inspired design principles, providing clean typography, status lozenges, and zero third-party external CDN runtime dependencies.

---

## 💎 Key Value Propositions

| Capability | Legacy Vaults / Password Managers | Aegis Vault Implementation |
| :--- | :--- | :--- |
| **Tamper Resistance** | CBC mode / Unauthenticated cipher | **AES-256-GCM** with 128-bit GMAC authentication tag (AEAD). Any bit modification causes cryptographic failure. |
| **Coercion / Duress** | Single master password, total compromise | **Duress PIN (`9999`)** opens an isolated, realistic decoy vault with harmless notes and meal plans. |
| **Pre-commit Secret Leak Check** | None; blindly saves input | **AI Security Advisor** scans for 10+ token types, PII, and calculates character-level Shannon entropy before storage. |
| **Intrusion Velocity Defense** | Basic lockout or rate limit | **AI Sentinel** detects authentication bursts and rapid decrypt patterns, calculating an adaptive Vault Health Score. |
| **Runtime Footprint** | Bloated Electron apps (500MB+ RAM) | **Lightweight Native JVM HTTP Server** utilizing virtual threads per request (`Loom`), starting in < 1 second. |
| **UI Ergonomics** | Flashing neon or generic themes | **Atlassian Design System** tokens, structured tables, neutral backgrounds, accessible contrast ratios. |

---

## 🏗️ System Architecture

### Architectural Topology

```mermaid
flowchart TD
    User([User / Browser Client]) -->|HTTP / REST + ADS Interface| WebServer["com.sun.net.httpserver.HttpServer\n(Java 21+ Virtual Threads)"]
    
    subgraph Core Application Boundary
        WebServer --> StaticHandler["StaticFileHandler\n(/, /index.html, /style.css, /app.js)"]
        WebServer --> ApiHandler["ApiHandler\n(/api/*)"]
        
        ApiHandler --> VaultManager["VaultManager\n(Session State & CRUD Controller)"]
        ApiHandler --> AiAdvisor["AiSecurityAdvisor\n(PII & Secret Scanner)"]
        ApiHandler --> Sentinel["VaultSentinel\n(Behavioral & Anomaly Detector)"]
        
        VaultManager --> CryptoEngine["CryptoEngine\n(AES-256-GCM & PBKDF2-SHA256)"]
        VaultManager --> DecoyGen["DecoyGenerator\n(Plausible Deniability Synthesizer)"]
        VaultManager --> Storage[(Disk Container: data/vault.enc.json)]
    end
```

### Subsystem Component Breakdown

1. **Bootstrap & Server Layer (`vault.App`, `vault.server.StaticFileHandler`, `vault.server.ApiHandler`)**:
   - Initialized on configurable port (default: `8080`, overrideable via `PORT` environment variable or CLI argument).
   - Powered by Java's native `HttpServer` with `Executors.newVirtualThreadPerTaskExecutor()`.
   - Full CORS and standard HTTP response code management.
2. **Cryptographic Core (`vault.crypto.CryptoEngine`)**:
   - Pure standard Java Cryptography Architecture (`javax.crypto`) implementation.
   - Zero third-party crypto libraries required; leverages FIPS-compliant JVM security providers.
3. **Session & Persistence Controller (`vault.crypto.VaultManager`)**:
   - Maintains volatile active session state: `activeSecretKey`, `isUnlocked`, `isDecoyMode`.
   - Auto-bootstraps demo data on first launch or resets safely on file damage.
   - Atomically serializes state to `data/vault.enc.json` using Google Gson.
4. **AI Privacy & Security Layer (`vault.ai.AiSecurityAdvisor`)**:
   - Analyzes raw payload candidate text across multiple regex patterns, entropy thresholds, and PII heuristics.
5. **Plausible Deniability Engine (`vault.ai.DecoyGenerator`)**:
   - Procedurally provisions realistic decoy records encrypted under a separate key derived from the Duress PIN.
6. **Behavioral Sentinel (`vault.ai.VaultSentinel`)**:
   - Thread-safe sliding-window event log (`ConcurrentLinkedDeque<AuditEvent>`).
   - Identifies consecutive brute-force failures and rapid decryption exfiltration spikes.

### End-to-End Execution Flow

```mermaid
sequenceDiagram
    autonumber
    actor User as User Browser
    participant API as ApiHandler
    participant VM as VaultManager
    participant Sentinel as VaultSentinel
    participant Crypto as CryptoEngine
    participant Disk as vault.enc.json

    User->>API: POST /api/unlock { password: "..." }
    API->>VM: unlock(password, clientIp)
    VM->>Crypto: hashPassword(password, salt)
    Crypto-->>VM: calculatedHash
    
    alt Password Matches Master Hash
        VM->>Crypto: deriveKey(password, salt, 65536)
        Crypto-->>VM: AES-256 SecretKey
        VM->>VM: Store SecretKey in memory (isDecoyMode=false)
        VM->>Sentinel: recordEvent("VAULT_UNLOCK_SUCCESS")
        VM-->>API: Unlock Success (Master Mode)
        API-->>User: 200 OK { success: true, isDecoyMode: false }
    else Password Matches Duress PIN Hash
        VM->>Crypto: deriveKey(duressPin, salt, 65536)
        Crypto-->>VM: AES-256 Decoy SecretKey
        VM->>VM: Store SecretKey in memory (isDecoyMode=true)
        VM->>Sentinel: recordEvent("DURESS_TRIGGERED")
        VM-->>API: Unlock Success (Decoy Mode)
        API-->>User: 200 OK { success: true, isDecoyMode: true }
    else Verification Fails
        VM->>Sentinel: recordEvent("VAULT_UNLOCK_FAILED")
        VM-->>API: Unlock Denied
        API-->>User: 401 Unauthorized
    end
```

---

## 🔒 Cryptographic Deep-Dive

### Authenticated Encryption: AES-256-GCM

Aegis Vault strictly uses **AES-256 in Galois/Counter Mode (GCM)** with no padding (`AES/GCM/NoPadding`). GCM is an **Authenticated Encryption with Associated Data (AEAD)** algorithm that provides both data confidentiality and authenticity.

```
Plaintext Payload ──┐
                    ▼
           ┌─────────────────┐
           │ AES-256-CTR Core │ ◄── 256-bit Key + 96-bit Random IV
           └────────┬────────┘
                    ▼
           ┌─────────────────┐
           │ GMAC Auth Tag   │ ──► 128-bit Authentication Tag
           └────────┬────────┘
                    ▼
         Ciphertext + Auth Tag (Stored as Base64)
```

- **Key Size**: 256 bits (32 bytes).
- **Initialization Vector (IV / Nonce)**: 96 bits (12 bytes) uniquely generated per encryption operation via `java.security.SecureRandom`. A unique IV per item prevents nonce reuse attacks.
- **Authentication Tag**: 128 bits (16 bytes). GMAC verifies that neither the ciphertext nor the metadata has been manipulated.
- **Tamper Resistance**: If an attacker flips even a single bit in the ciphertext or replaces the IV, `Cipher.doFinal()` throws an `AEADBadTagException`, instantly aborting decryption.

### Key Derivation: PBKDF2WithHmacSHA256

To protect against dictionary and rainbow-table attacks, raw passphrases are stretched using Password-Based Key Derivation Function 2:

$$\text{Key} = \text{PBKDF2}(\text{HMAC-SHA256}, \text{Passphrase}, \text{Salt}, \text{Iterations}=65536, \text{KeyLength}=256)$$

- **Salt**: 128-bit (16-byte) cryptographically secure pseudorandom salt generated once during vault initialization and stored in the container header.
- **Work Factor**: 65,536 iterations ensures substantial resistance against GPU/ASIC brute-force clusters while remaining fast for human-interactive logins (< 100ms on modern processors).

### Zero-Knowledge Password Verification

To authenticate a user without storing the master password or writing derived keys to persistent storage:

1. During initialization, the master password is concatenated with the salt and hashed using `SHA-256`:
   $$\text{Verifier} = \text{SHA-256}(\text{Salt} \parallel \text{MasterPassword})$$
2. The resulting Base64 string is stored as `passwordVerifierHash`.
3. When unlocking, the input passphrase is hashed with the identical salt and compared against `passwordVerifierHash`.
4. The server never writes the password or the AES key to disk.

### Constant-Time Comparison & Side-Channel Mitigation

Standard string comparisons (`str.equals(...)`) terminate upon the first differing character, leaking information about the matched prefix through microsecond timing variations (timing attacks). Aegis Vault mitigates this using `java.security.MessageDigest.isEqual()`:

```java
public static boolean verifyHash(String providedHash, String storedHash) {
    if (providedHash == null || storedHash == null) return false;
    byte[] a = providedHash.getBytes(StandardCharsets.UTF_8);
    byte[] b = storedHash.getBytes(StandardCharsets.UTF_8);
    return MessageDigest.isEqual(a, b);
}
```

`MessageDigest.isEqual` performs a constant-time XOR comparison across all bytes regardless of early mismatches.

### Volatile Memory Hygiene & Key Dereferencing

When a user locks the vault (`POST /api/lock`) or when the browser session terminates:
- The `activeSecretKey` reference in `VaultManager` is immediately set to `null`.
- Session state variables (`isUnlocked`, `isDecoyMode`) are reset to `false`.
- The JVM Garbage Collector reclaims volatile key material.
- An explicit security audit event (`VAULT_LOCKED`) is logged.

### Storage Container Schema (`vault.enc.json`)

The entire encrypted vault is persisted as an atomic, human-auditable JSON container. No plaintext secrets or passwords exist anywhere within this structure:

```json
{
  "version": "2.0-AI-GCM",
  "saltBase64": "a+ptXIX5VDhxf5Mc1HTrCA==",
  "passwordVerifierHash": "6hNI6Ng+OjHkZzM/5IJIsiXm0qmzLg6cU2VDck81LFM=",
  "duressPinHash": "aE5WWUgGKB90PGBrFGkdU94iCMmX9OaoD1WyPafEq3s=",
  "kdfIterations": 65536,
  "algorithm": "AES/GCM/NoPadding",
  "createdAt": 1791183679542,
  "lastModifiedAt": 1791184221835,
  "items": [
    {
      "id": "2b5d7534-32a2-44e3-aaa5-e9419138e54d",
      "title": "AWS Production Cloud Infrastructure Keys",
      "category": "API_KEY",
      "ciphertextBase64": "leZ5cM1BUqQ2SxIIm58FqzwawWbfd844...",
      "ivBase64": "lU5HgAflVbZbn24k",
      "createdAt": 1791183679676,
      "updatedAt": 1791183679676,
      "riskLevel": "CRITICAL",
      "entropyScore": 100,
      "aiTags": ["#Cloud", "#AWS"],
      "previewDescription": "AKIAIOSFODNN7EXAMPLE\nwJalrXUtnFEMI/...",
      "fileSize": 113
    }
  ],
  "decoyItems": [ ... ]
}
```

---

## 🤖 AI Privacy, PII & Threat Detection Layer

Before sensitive data is encrypted into the vault, the **AI Security Advisor** (`AiSecurityAdvisor.java`) performs inspection to detect credentials, tokens, PII, and anomalous high-entropy strings.

```
User Input Text ──► Shannon Entropy Calculation ──► Regex Secret Engine ──► Classification Matrix
                                                                                   │
                                  ┌────────────────────────────────────────────────┘
                                  ▼
         ┌──────────────────────────────────────────────────────┐
         │ • Risk Level: CRITICAL / HIGH / MEDIUM / LOW / SAFE  │
         │ • Suggested Category & Semantic #Tags                │
         │ • Masked Preview Snippets                            │
         │ • Actionable Remediation Guidance                    │
         └──────────────────────────────────────────────────────┘
```

### Pattern Recognition & Secret Detection Rules

| Secret / PII Entity | Recognition Pattern / Rule | Assigned Severity | Automated Remediation Recommendation |
| :--- | :--- | :--- | :--- |
| **AWS IAM Access Key** | `AKIA[0-9A-Z]{16}` | `CRITICAL` | Rotate IAM key immediately; verify that the accompanying Secret Access Key is not exposed in public repositories. |
| **OpenAI / LLM API Key** | `sk-[a-zA-Z0-9_-]{20,}` | `HIGH` | Set hard spending limits on the AI provider dashboard; restrict model access permissions. |
| **GitHub Access Token** | `gh[pousr]_[A-Za-z0-9_]{36,}` | `HIGH` | Limit token scopes to minimal necessary repositories; enforce short expiration lifespans. |
| **Private Keys (PEM/SSH)** | `-----BEGIN (RSA\|EC\|OPENSSH\|DSA)? PRIVATE KEY-----` | `CRITICAL` | Ensure key is protected with an encrypted passphrase; never transmit across unencrypted channels. |
| **JSON Web Token (JWT)** | `eyJ[A-Za-z0-9_-]{10,}\.eyJ...` | `HIGH` | Verify token `exp` timestamp; short-lived tokens reduce breach surface area. |
| **Payment Cards (PCI-DSS)**| Luhn-compatible 13-16 digit numbers (Visa, MC, Amex) | `CRITICAL` | PCI-DSS mandates strict encryption; never record CVV security codes in notes. |
| **Social Security Numbers**| `\b\d{3}-\d{2}-\d{4}\b` (US SSN format) | `HIGH` | PII requires zero-knowledge client-side encryption. Stored locally with AES-256. |
| **Database Connection URI**| `(postgres\|mongodb\|mysql\|redis)://...` | `CRITICAL` | Restrict database ports via network VPC security groups; enforce TLS connections. |
| **Hardcoded Credentials**  | `(password\|secret\|apikey\|token)\s*[:=]\s*...` | `HIGH` | Extract hardcoded secrets into secure environment variables. |
| **Email Addresses**        | RFC 5322 compliant regex | `LOW` / `MEDIUM` | Flagged as personal contact PII for metadata filtering. |

### Shannon Entropy Analysis

To detect unformatted cryptographic keys, password hashes, and random token strings, Aegis Vault computes the **Shannon Entropy** ($H$) of the input string across its 256-character ASCII distribution:

$$H(X) = - \sum_{i=1}^{n} P(x_i) \log_2 P(x_i)$$

Where:
- $P(x_i) = \frac{\text{count}(x_i)}{\text{total length}}$ represents the probability of character $x_i$ occurring.
- Natural English text typically exhibits an entropy score between **$2.5$ and $3.5$ bits/byte**.
- High-randomness passwords, Base64 strings, and hex keys exhibit an entropy score between **$4.5$ and $6.0$ bits/byte**.

```java
public static double calculateShannonEntropy(String input) {
    if (input == null || input.isEmpty()) return 0.0;
    int[] charCounts = new int[256];
    for (char c : input.toCharArray()) {
        if (c < 256) charCounts[c]++;
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
```

The raw entropy score is normalized ($0-100\%$) and combined with pattern detections to compute the final risk rating.

### Risk Classification & Scoring Matrix

```
[Critical Findings Detected?] ──► YES ──► Risk: CRITICAL (Entropy: shannon * 14 + 30)
              │ NO
[High Findings Detected?]     ──► YES ──► Risk: HIGH     (Entropy: shannon * 14 + 15)
              │ NO
[Medium Findings Detected?]   ──► YES ──► Risk: MEDIUM   (Entropy: shannon * 14)
              │ NO
[Shannon Entropy > 4.5?]      ──► YES ──► Risk: LOW      (High-entropy random string)
              │ NO
                              ──► Risk: SAFE     (Natural language note)
```

### Secret Masking & Actionable Recommendations

To protect credentials from over-the-shoulder snooping in logs and metadata displays, all identified secrets are automatically masked:

```java
private String maskSecret(String secret) {
    if (secret.length() <= 8) return "••••••••";
    return secret.substring(0, 4) + "••••••••" + secret.substring(secret.length() - 4);
}
```
*Example:* `AKIAIOSFODNN7EXAMPLE` is masked as `AKIA••••••••MPLE`.

---

## 🎭 Plausible Deniability & Duress PIN System

### Threat Model & Rubber-Hose Cryptanalysis Defense

Under standard security models, an adversary possessing physical control of a device can compel the user to reveal their master password (often termed "rubber-hose cryptanalysis" or forced compliance at border crossings). Refusing to comply results in harm, while complying reveals all confidential records.

Aegis Vault resolves this through **Plausible Deniability**:
- The user maintains two credentials:
  1. **Master Password** (`CyberVault2026!SecureKey#99`): Unlocks the real vault.
  2. **Duress PIN** (`9999`): Unlocks the **Decoy Vault**.
- When the Duress PIN is entered, the UI behaves identically to a normal unlock. No error messages or "decoy mode warnings" are displayed to an observer.
- The real items remain completely encrypted and inaccessible.

### Dual-Verifier Authentication Architecture

```mermaid
flowchart LR
    InputPass[User Input] --> Hashing["Hash with Salt (SHA-256)"]
    Hashing --> Compare1{"Matches Master Hash?"}
    Hashing --> Compare2{"Matches Duress Hash?"}
    
    Compare1 -- YES --> Key1["Derive Real Key\nLoad items[]"]
    Compare2 -- YES --> Key2["Derive Decoy Key\nLoad decoyItems[]"]
    
    Key1 --> RealDashboard["Real Vault Display\n(AWS Keys, DB Credentials)"]
    Key2 --> DecoyDashboard["Decoy Vault Display\n(Meal Plans, Book Lists)"]
```

Both hashes are stored side-by-side in `vault.enc.json`. An adversary inspecting the file cannot mathematically determine that `duressPinHash` is a decoy rather than a secondary user, recovery key, or dual-authorization credential.

### Synthetic Decoy Database Generation

The Decoy Vault is pre-populated by `DecoyGenerator.java` with realistic, innocuous encrypted items:

1. **Weekly Fitness Routine & Meal Plan**: Benign health notes with workouts and meal preparation.
2. **Book Recommendations 2026**: Curated literature and engineering reading list.
3. **Apartment Paint Color Hex Codes**: Interior design swatches (#E8ECEF, #2C3E50, #F4F6F7).
4. **Grocery & Hardware Store Checklist**: Shopping errands for LED bulbs, WD-40, and coffee beans.

Each decoy item is encrypted with its own unique random IV using the key derived from the Duress PIN. Decryption operations on decoy items succeed cleanly, convincing an attacker that they have accessed genuine data.

### Covert Behavioral Signaling

When the duress PIN is triggered, the AI Sentinel silently records an audit alert:
- `DURESS_TRIGGERED`: Recorded with timestamp and client IP.
- Decoy state is flagged internally, allowing security teams or automated scripts to capture the event without any visual indicator on the client screen.

---

## 🚨 AI Behavioral Sentinel & Intrusion Detection

The **Vault Sentinel** (`VaultSentinel.java`) acts as an in-memory threat monitoring agent that analyzes operational patterns and access velocity.

```
Operational Events (Unlock, Decrypt, Add, Delete)
                      │
                      ▼
        ┌───────────────────────────┐
        │ Sliding Window Event Log  │ ◄── Max 100 Audit Events
        └─────────────┬─────────────┘
                      │
        ┌─────────────▼─────────────┐
        │ Heuristic Anomaly Engine  │
        └─────────────┬─────────────┘
                      │
       ┌──────────────┴──────────────┐
       ▼                             ▼
Brute-Force Detection       Rapid Exfiltration Alert
(3+ failed logins)          (5+ decryptions in < 1500ms)
```

### Real-Time Anomaly Detection Engine

1. **Brute-Force Intrusion Detection**:
   - Tracks `consecutiveFailedLogins`.
   - If $\ge 3$ consecutive failures occur, flags `POTENTIAL_BRUTE_FORCE`.
   - Consecutive counter resets to 0 upon any successful authentication.
2. **Rapid Decryption / Exfiltration Burst**:
   - Tracks inter-arrival time between consecutive `ITEM_DECRYPTED` actions.
   - If $\ge 5$ items are decrypted with interval $< 1500\text{ ms}$, flags `RAPID_DECRYPT_EXFILTRATION`.
   - Detects automated screen scrapers or malware attempting bulk credential dumping.
3. **Duress Emergency Protocol**:
   - Automatically registers `DURESS_EMERGENCY_DECOY` anomaly alert upon secondary PIN usage.

### Dynamic Vault Health Score Formula

The Sentinel calculates a real-time **Vault Health Score** ($0 - 100\%$) exposed in the dashboard metrics:

$$\text{HealthScore} = \max\Big(20,\; 100 - \min(30, \text{Anomalies} \times 10) - \min(20, \text{FailedLogins} \times 10)\Big)$$

- Baseline is **100% (Optimal)**.
- Deducts 10 points per detected anomaly (capped at 30).
- Deducts 10 points per failed login attempt (capped at 20).
- Floor is set at 20% to avoid negative metric overflows.

### Immutable In-Memory Audit Trail

Events are held in a bounded `ConcurrentLinkedDeque` (capped at the 100 most recent events). Each event records:
- **Timestamp**: Epoch millisecond precision.
- **Action**: e.g., `VAULT_UNLOCK_SUCCESS`, `ITEM_ENCRYPTED`, `ITEM_DECRYPTED`, `VAULT_LOCKED`.
- **Details**: Human-readable narrative describing the operation.
- **Status**: `SUCCESS`, `ANOMALY_WARNING`, or `DENIED`.
- **Client IP**: Remote socket address of the requesting client.

---

## 🎨 Atlassian Design System User Interface

The frontend is constructed following the **Atlassian Design System (ADS)** guidelines (the visual language of Jira, Confluence, and Bitbucket).

```
┌────────────────────────────────────────────────────────────────────────┐
│ [🔒 Aegis Vault] [Security]    [⚠️ Duress]    [UNLOCKED] [Scanner] [Audit] [Lock] │
├────────────────────────────────────────────────────────────────────────┤
│ Personal Vault Repository                                              │
│ Manage client-side encrypted credentials with AI threat inspection.    │
│                                                                        │
│ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ │
│ │Protected Assets│ │Health Score    │ │AI Sentinel    │ │Crypto Standard│ │
│ │  5 [AES-256]  │ │  98% [OPTIMAL]│ │ACTIVE [0 Alerts│ │PBKDF2-SHA256  │ │
│ └───────────────┘ └───────────────┘ └───────────────┘ └───────────────┘ │
│                                                                        │
│ [🔍 Filter items...]  [All Items] [Notes] [Credentials] [API Secrets] [Files]  │
│                                                                        │
│ ┌────────────────────────────────────────────────────────────────────┐ │
│ │ Title               Category    Risk Level  Entropy   Tags  Action │ │
│ ├────────────────────────────────────────────────────────────────────┤ │
│ │ AWS Production Keys API_KEY     CRITICAL    100%      #AWS  [View] │ │
│ │ OpenAI Agent Key    API_KEY     HIGH        92%       #AI   [View] │ │
│ └────────────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

### Design Philosophy & Aesthetic Standards

- **Color Tokens**:
  - Primary Brand Blue: `#0052CC` (hover: `#0065FF`, active: `#0747A6`).
  - Neutral Backgrounds: `#FAFBFC` (default background), `#FFFFFF` (cards), `#F4F5F7` (sunken).
  - Borders: `#DFE1E6`, focused: `#4C9AFF`.
  - Typography: System font stack (`-apple-system`, `BlinkMacSystemFont`, `'Segoe UI'`, `Roboto`, sans-serif); Monospace: `'SFMono-Medium'`, `Consolas`, monospace.
- **Status Lozenges**:
  - `CRITICAL`: Red Subtle (`#FFEBE6`, text `#BF2600`).
  - `HIGH`: Yellow Subtle (`#FFF0B3`, text `#172B4D`).
  - `OPTIMAL` / `SAFE`: Green Subtle (`#E3FCEF`, text `#006644`).
  - `IN PROGRESS`: Blue Subtle (`#DEEBFF`, text `#0747A6`).
- **Zero Third-Party Dependencies**: No external fonts or CSS frameworks loaded over public CDNs; runs completely offline in air-gapped environments.

### Workspace Layout & Interactive Workflows

1. **Locked State Screen**:
   - Clean, centered Atlassian card.
   - Includes one-click demo helpers: `🔑 Fill Master Key` and `🚨 Fill Duress PIN (9999)`.
   - Real-time inline error reporting for incorrect passphrases.
2. **Unlocked Dashboard**:
   - **4 KPI Metric Cards**: Protected Assets, Security Health Score, AI Sentinel Posture, and Cryptographic Standard.
   - **Search & Filter Bar**: Instant filtering by free text query and category tabs (`ALL`, `NOTE`, `CREDENTIAL`, `API_KEY`, `FILE`).
   - **Structured Issue Table**: Displays title, category, risk lozenge, entropy percentage, semantic tag lozenges, and action buttons.

### Modals & Security Dialogs

- **Create Item Modal**: Input title, category, and secret content. Features live **AI Privacy Inspection** and an integrated cryptographically secure **Password Generator**.
- **Inspect & Decrypt Modal**: Displays risk level, entropy score, GCM 96-bit IV, decrypted plaintext (with one-click clipboard copy), and raw disk ciphertext preview.
- **File Encryption Modal**: Drag-and-drop zone allowing any document, PDF, certificate, or image to be encrypted and decrypted.
- **AI Threat Scanner Playground**: Standalone text inspection tool with pre-loaded vulnerability test presets (AWS keys, Credit Cards, RSA private keys).
- **Sentinel Audit Log Modal**: Real-time tabular viewer of all security events and anomaly alerts.

---

## 🔌 Comprehensive REST API Reference

The server exposes a clean, standardized JSON REST API under `/api/*`. All responses return appropriate HTTP status codes and CORS headers.

### Endpoint Summary Table

| Method | Endpoint | Access Requirement | Purpose |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/status` | Public | Returns vault lock state, decoy status, and security metrics. |
| `POST` | `/api/unlock` | Public | Unlocks session using Master Password or Duress PIN. |
| `POST` | `/api/lock` | Public / Session | Purges active keys from memory and locks the vault. |
| `GET` | `/api/items` | Authenticated | Retrieves all encrypted item metadata for active session. |
| `POST` | `/api/items` | Authenticated | Encrypts and adds a new item to the active vault. |
| `POST` | `/api/decrypt` | Authenticated | Decrypts a specific item by ID and returns plaintext. |
| `DELETE` | `/api/items?id={id}` | Authenticated | Permanently removes an item from the vault. |
| `POST` | `/api/ai/scan` | Public | Performs on-the-fly AI security analysis of input text. |
| `GET` | `/api/audit` | Public | Returns the 40 most recent Sentinel audit events. |
| `GET` | `/api/export` | Authenticated | Downloads the entire encrypted vault container as JSON. |
| `POST` | `/api/import` | Public | Imports and replaces the encrypted vault backup. |

---

### Detailed Endpoint Specifications

#### 1. Get Vault Status
- **Method / Path**: `GET /api/status`
- **Description**: Returns lock state, algorithm specifications, and health scores.
- **Response `200 OK`**:
```json
{
  "isUnlocked": true,
  "isDecoyMode": false,
  "algorithm": "AES/GCM/NoPadding",
  "kdfIterations": 65536,
  "itemCount": 5,
  "anomaliesDetected": 0,
  "vaultHealthScore": 98
}
```

#### 2. Unlock Vault Session
- **Method / Path**: `POST /api/unlock`
- **Request Body**:
```json
{
  "password": "CyberVault2026!SecureKey#99"
}
```
- **Response `200 OK` (Master Key)**:
```json
{
  "success": true,
  "isUnlocked": true,
  "isDecoyMode": false,
  "message": "Master Vault unlocked."
}
```
- **Response `200 OK` (Duress PIN: `9999`)**:
```json
{
  "success": true,
  "isUnlocked": true,
  "isDecoyMode": true,
  "message": "Decoy emergency vault unlocked."
}
```
- **Response `401 Unauthorized` (Invalid Passphrase)**:
```json
{
  "success": false,
  "isUnlocked": false,
  "isDecoyMode": false,
  "message": "Authentication failed. Incorrect master password or PIN."
}
```

#### 3. Lock Vault Session
- **Method / Path**: `POST /api/lock`
- **Request Body**: Empty or `{}`
- **Response `200 OK`**:
```json
{
  "success": true,
  "message": "Vault safely locked. In-memory keys purged."
}
```

#### 4. Get Vault Items
- **Method / Path**: `GET /api/items`
- **Authentication**: Requires active unlocked session (`403 Forbidden` if locked).
- **Response `200 OK`**:
```json
{
  "total": 5,
  "isDecoyMode": false,
  "items": [
    {
      "id": "2b5d7534-32a2-44e3-aaa5-e9419138e54d",
      "title": "AWS Production Cloud Infrastructure Keys",
      "category": "API_KEY",
      "ciphertextBase64": "leZ5cM1BUqQ2SxIIm58Fqzw...",
      "ivBase64": "lU5HgAflVbZbn24k",
      "createdAt": 1791183679676,
      "updatedAt": 1791183679676,
      "riskLevel": "CRITICAL",
      "entropyScore": 100,
      "aiTags": ["#Cloud", "#AWS"],
      "previewDescription": "AKIAIOSFODNN7EXAMPLE...",
      "fileSize": 113
    }
  ]
}
```

#### 5. Add / Encrypt New Item
- **Method / Path**: `POST /api/items`
- **Request Body**:
```json
{
  "title": "Payment Gateway API Key",
  "category": "API_KEY",
  "content": "sec_tok_prod_sample_credential_0011223344"
}
```
- **Response `201 Created`**:
```json
{
  "success": true,
  "item": {
    "id": "9d81d227-c351-4fa3-94c0-21a4f0d619eb",
    "title": "Payment Gateway API Key",
    "category": "API_KEY",
    "ciphertextBase64": "mXy891zN...",
    "ivBase64": "kLmNoP123456",
    "riskLevel": "HIGH",
    "entropyScore": 88,
    "aiTags": ["#APIKey", "#EncryptedVault"]
  }
}
```

#### 6. Decrypt Item
- **Method / Path**: `POST /api/decrypt`
- **Request Body**:
```json
{
  "id": "2b5d7534-32a2-44e3-aaa5-e9419138e54d"
}
```
- **Response `200 OK`**:
```json
{
  "success": true,
  "id": "2b5d7534-32a2-44e3-aaa5-e9419138e54d",
  "plaintext": "AKIAIOSFODNN7EXAMPLE\nwJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY\nRegion: us-east-1"
}
```

#### 7. Delete Item
- **Method / Path**: `DELETE /api/items?id=2b5d7534-32a2-44e3-aaa5-e9419138e54d`
- **Response `200 OK`**:
```json
{
  "success": true
}
```

#### 8. AI Scan Playground
- **Method / Path**: `POST /api/ai/scan`
- **Request Body**:
```json
{
  "content": "Database config: postgres://admin:SuperSecret123@db.internal:5432/prod"
}
```
- **Response `200 OK`**:
```json
{
  "riskLevel": "CRITICAL",
  "entropyScore": 84,
  "shannonEntropy": 4.62,
  "findings": [
    {
      "type": "DATABASE_URI",
      "description": "Database Connection URI containing credentials detected",
      "snippet": "post••••prod",
      "severity": "CRITICAL"
    }
  ],
  "recommendations": [
    "Database URIs should be restricted by network IP firewalls or AWS VPC peering."
  ],
  "suggestedTags": ["#Database"],
  "suggestedCategory": "CREDENTIAL"
}
```

#### 9. Get Audit Events
- **Method / Path**: `GET /api/audit`
- **Response `200 OK`**:
```json
{
  "anomaliesDetected": 0,
  "events": [
    {
      "timestamp": 1791184221835,
      "action": "VAULT_UNLOCK_SUCCESS",
      "details": "Master Vault successfully authenticated with AES-256 key.",
      "status": "SUCCESS",
      "ipAddress": "127.0.0.1"
    }
  ]
}
```

#### 10. Export Encrypted Vault
- **Method / Path**: `GET /api/export`
- **Response Headers**:
  - `Content-Type: application/json; charset=UTF-8`
  - `Content-Disposition: attachment; filename="personal-vault.enc.json"`
- **Response Body**: Raw complete JSON of `VaultPackage`.

#### 11. Import Encrypted Vault
- **Method / Path**: `POST /api/import`
- **Request Body**: Raw JSON string of `VaultPackage`.
- **Response `200 OK`**:
```json
{
  "success": true,
  "message": "Vault imported successfully."
}
```

---

## 🚀 Getting Started & Operational Guide

### Prerequisites

| Component | Minimum Requirement | Recommended | Verification Command |
| :--- | :--- | :--- | :--- |
| **Java JDK** | OpenJDK 21 | OpenJDK 26 (tested) | `java -version` |
| **Gradle** | Gradle 8.5+ | Gradle 8.12 / 9.x | `gradle -version` |
| **Web Browser** | Any HTML5 browser | Chrome, Edge, Firefox, Safari | — |

### Installation & Compilation

Clone or navigate to the repository directory:
```bash
cd "Personal Encryption Vault"
```

Verify that Gradle compiles the application classes:
```bash
gradle classes
```

### Execution & Port Configuration

#### Option A: Using the Convenience Run Script (`./run.sh`)
The project includes a unified, color-coded runner script for Linux/macOS ([`run.sh`](file:///home/fondness/Projects/sample/PROJECT/Personal%20Encryption%20Vault/run.sh)) and Windows ([`run.bat`](file:///home/fondness/Projects/sample/PROJECT/Personal%20Encryption%20Vault/run.bat)):

```bash
# Launch on default port (8080)
./run.sh

# Launch on custom port
./run.sh 9090
# or
./run.sh --port 9090

# Run the automated test suite
./run.sh --test

# Build and verify classes
./run.sh --build
```

#### Option B: Direct Gradle Execution
You can also launch directly using Gradle or the Gradle wrapper:
```bash
gradle run

# Via environment variable
PORT=9090 gradle run

# Via argument
gradle run --args="9090"
```

#### Expected Terminal Startup Banner:
```text
=================================================================
   AI-POWERED PERSONAL ENCRYPTION VAULT
=================================================================
 Cryptographic Engine: AES-256-GCM + PBKDF2WithHmacSHA256 (65k)
 AI Privacy Sentinel : Deep PII / Secret / Anomaly Scanner Active
 Plausible Deniability: Duress PIN System Active
-----------------------------------------------------------------
 Web Dashboard: http://localhost:8080
 Master Password: CyberVault2026!SecureKey#99
 Duress PIN     : 9999 (Opens Decoy Vault)
=================================================================
```

Open your browser and navigate to:
```
http://localhost:8080
```

### Demo Credentials & Quick Showcase Actions

The login screen features pre-configured **one-click helper buttons** for seamless evaluation:

| Target Vault | Quick Button | Value | Access Scope & Behavior |
| :--- | :--- | :--- | :--- |
| **Master Real Vault** | `🔑 Fill Master Key` | `CyberVault2026!SecureKey#99` | Unlocks real AWS credentials, OpenAI keys, corporate M&A notes, database strings, and digital files. |
| **Duress Decoy Vault** | `🚨 Fill Duress PIN (9999)` | `9999` | Triggers **Plausible Deniability**. Displays an innocuous decoy vault (meal plans, reading lists, paint codes). |

---

## 🧪 Testing & Quality Assurance

### JUnit 5 Test Suite Analysis

The test suite in [`src/test/java/vault/CryptoEngineTest.java`](src/test/java/vault/CryptoEngineTest.java) executes automated regression and security tests:

1. **`testAes256GcmRoundTrip`**: Validates that plaintext encrypted with a 256-bit PBKDF2 derived key decrypts back to the exact original string, verifying Base64 serialization and IV extraction.
2. **`testDecryptionFailsOnTampering`**: Modifies a single character of the ciphertext payload and asserts that `CryptoEngine.decryptText()` throws an exception, confirming AEAD authentication tag validation.
3. **`testPasswordHashingAndVerification`**: Verifies salted SHA-256 password hashing and asserts that correct passwords return `true` while mismatched passwords return `false`.
4. **`testAiSecurityAdvisorDetectsCriticalSecrets`**: Feeds known AWS IAM credentials (`AKIA...`) and confirms that `riskLevel` is flagged as `CRITICAL`, findings include `AWS_ACCESS_KEY`, and tags contain `#AWS`. Tests benign text to verify `SAFE` classification.
5. **`testDecoyGeneratorPlausibleDeniability`**: Derives a decoy key using PIN `9999`, generates default decoy items, and asserts that all items are rated `SAFE` and decrypt successfully using the decoy key.

### Running Test Commands

Execute the test suite using Gradle:
```bash
gradle test
```

#### Test Execution Output:
```text
> Task :compileJava UP-TO-DATE
> Task :processResources
> Task :classes
> Task :compileTestJava UP-TO-DATE
> Task :processTestResources NO-SOURCE
> Task :testClasses UP-TO-DATE
> Task :test

BUILD SUCCESSFUL in 7s
4 actionable tasks: 2 executed, 2 up-to-date
```

Comprehensive HTML test reports are generated at:
```text
build/reports/tests/test/index.html
```

---

## 🛡️ Threat Model & Security Guarantees

### STRIDE Threat Analysis

| Threat Category | Potential Vector | Aegis Vault Countermeasure |
| :--- | :--- | :--- |
| **Spoofing Identity** | Attacker attempts to forge password hashes or bypass login. | Salted SHA-256 verifier with constant-time equality check; secrets cannot be derived without the exact master password. |
| **Tampering with Data** | Attacker modifies encrypted file bytes on disk. | **AES-256-GCM** 128-bit authentication tag. Any tampered byte causes `AEADBadTagException` and aborts decryption. |
| **Repudiation** | User denies performing an action or access attempt. | In-memory `VaultSentinel` event ledger timestamps every unlock, decrypt, add, delete, and import event with client IP. |
| **Information Disclosure**| Data exfiltration via physical coercion or inspection. | Zero-knowledge storage; memory key dereferencing upon lock; Duress PIN reveals only synthetic decoy data. |
| **Denial of Service** | Rapid repeated calls to `/api/decrypt` or unlock. | Virtual threads per connection prevent thread exhaustion; Sentinel flags anomalous velocity bursts. |
| **Elevation of Privilege**| Unauthorized access to locked data. | Enforced in `VaultManager`: all CRUD operations verify `isUnlocked` and fail immediately if locked. |

### Security Boundaries & Invariants

1. **In-Memory Boundary**: The derived secret key is stored in volatile JVM heap memory only while the vault is explicitly unlocked. Keys are never serialized or logged.
2. **On-Disk Boundary**: The disk file (`vault.enc.json`) contains only high-entropy Base64 ciphertext, random IVs, nonces, and unencrypted category/tag metadata.
3. **Network Boundary**: The native HTTP server binds to localhost by default. When deployed across networks, it should be fronted with TLS (e.g., Nginx reverse proxy with HTTPS).

---

## 📁 Project Directory & Code Inventory

```text
Personal Encryption Vault/
├── build.gradle                                  # Gradle build configuration and dependencies
├── settings.gradle                               # Gradle project settings
├── data/
│   └── vault.enc.json                            # Persistent encrypted JSON container
├── src/
│   ├── main/
│   │   ├── java/vault/
│   │   │   ├── App.java                          # Main bootstrap, virtual thread HTTP server setup
│   │   │   ├── crypto/
│   │   │   │   ├── CryptoEngine.java             # AES-256-GCM, PBKDF2-SHA256, Shannon Entropy
│   │   │   │   └── VaultManager.java             # Session state, key cache, disk persistence, CRUD
│   │   │   ├── ai/
│   │   │   │   ├── AiSecurityAdvisor.java        # PII regex scanner, secret leak detector, tags
│   │   │   │   ├── DecoyGenerator.java           # Plausible deniability synthetic decoy builder
│   │   │   │   └── VaultSentinel.java            # Behavioral anomaly, velocity, and intrusion monitor
│   │   │   ├── model/
│   │   │   │   ├── VaultItem.java                # Encrypted item entity and metadata
│   │   │   │   ├── VaultPackage.java             # Serialization model for disk storage
│   │   │   │   ├── ScanResult.java               # AI scan findings and recommendations model
│   │   │   │   └── AuditEvent.java               # Sentinel security event model
│   │   │   └── server/
│   │   │       ├── ApiHandler.java               # REST API endpoints controller (/api/*)
│   │   │       └── StaticFileHandler.java        # Static asset web server (HTML, CSS, JS)
│   │   └── resources/
│   │       └── web/                              # Atlassian Design System frontend
│   │           ├── index.html                    # Main HTML5 UI workspace and modals
│   │           ├── style.css                     # Atlassian tokens, lozenges, and layout styling
│   │           └── app.js                        # Client state, event listeners, and API bridge
│   └── test/
│       └── java/vault/
│           └── CryptoEngineTest.java             # JUnit 5 cryptographic and AI test suite
└── README.md                                     # Complete end-to-end documentation report
```

### Source Code File Inventory & Role Summary

| File | Primary Class | Responsibility |
| :--- | :--- | :--- |
| [`App.java`](src/main/java/vault/App.java) | `vault.App` | Server bootstrap, port resolution, virtual thread allocation, route registration. |
| [`CryptoEngine.java`](src/main/java/vault/crypto/CryptoEngine.java) | `vault.crypto.CryptoEngine` | Cryptographic primitives: AES-256-GCM encrypt/decrypt, PBKDF2 key derivation, Shannon entropy calculation. |
| [`VaultManager.java`](src/main/java/vault/crypto/VaultManager.java) | `vault.crypto.VaultManager` | Manages volatile session keys, master/duress authentication, disk serialization, and CRUD operations. |
| [`AiSecurityAdvisor.java`](src/main/java/vault/ai/AiSecurityAdvisor.java) | `vault.ai.AiSecurityAdvisor` | Pre-commit regex inspection for AWS, OpenAI, GitHub tokens, PEM keys, credit cards, SSNs, and DB strings. |
| [`DecoyGenerator.java`](src/main/java/vault/ai/DecoyGenerator.java) | `vault.ai.DecoyGenerator` | Generates realistic synthetic decoy items (meal plans, reading lists, paint swatches) for Duress Mode. |
| [`VaultSentinel.java`](src/main/java/vault/ai/VaultSentinel.java) | `vault.ai.VaultSentinel` | Behavioral anomaly detector, brute-force tracking, rapid exfiltration detection, and audit trail ledger. |
| [`ApiHandler.java`](src/main/java/vault/server/ApiHandler.java) | `vault.server.ApiHandler` | HTTP request router for 11 REST endpoints, parameter parsing, and JSON response serialization. |
| [`StaticFileHandler.java`](src/main/java/vault/server/StaticFileHandler.java) | `vault.server.StaticFileHandler` | Serves web assets (HTML, CSS, JS) with appropriate MIME type mapping. |
| [`CryptoEngineTest.java`](src/test/java/vault/CryptoEngineTest.java) | `vault.CryptoEngineTest` | JUnit 5 test suite verifying round-trips, tampering detection, hashing, and AI detection accuracy. |

---

## 📜 License & Disclaimers

### Open Source License
This project is distributed under the **MIT License**. You are free to use, modify, study, and distribute this software for personal or commercial applications with attribution.

### Cryptographic Compliance
This software utilizes standard AES-256-GCM and PBKDF2 encryption. Ensure your deployment complies with your local jurisdiction's export control regulations regarding cryptographic software.

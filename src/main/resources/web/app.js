/**
 * AEGIS VAULT - Professional Atlassian-Style Client Logic
 */

document.addEventListener('DOMContentLoaded', () => {
    // --- Application State ---
    let vaultState = {
        isUnlocked: false,
        isDecoyMode: false,
        items: [],
        currentFilter: 'ALL',
        searchQuery: '',
        activeInspectItem: null
    };

    // --- DOM Elements ---
    const lockedView = document.getElementById('lockedStateView');
    const unlockedView = document.getElementById('unlockedStateView');
    const vaultStatusLozenge = document.getElementById('vaultStatusLozenge');
    const lockBtnText = document.getElementById('lockBtnText');
    const toggleLockBtn = document.getElementById('toggleLockBtn');
    const decoyBanner = document.getElementById('decoyBanner');

    const unlockForm = document.getElementById('unlockForm');
    const masterPasswordInput = document.getElementById('masterPasswordInput');
    const fillDemoMasterBtn = document.getElementById('fillDemoMasterBtn');
    const fillDemoDuressBtn = document.getElementById('fillDemoDuressBtn');
    const unlockErrorMsg = document.getElementById('unlockErrorMsg');

    const healthScoreValue = document.getElementById('healthScoreValue');
    const totalItemsValue = document.getElementById('totalItemsValue');
    const activeAnomaliesText = document.getElementById('activeAnomaliesText');
    const anomalyBadge = document.getElementById('anomalyBadge');

    const vaultSearchInput = document.getElementById('vaultSearchInput');
    const catTabs = document.querySelectorAll('.tab-btn');
    const itemsTableBody = document.getElementById('itemsTableBody');
    const emptyTableState = document.getElementById('emptyTableState');

    // Modals
    const itemModal = document.getElementById('itemModal');
    const newItemBtn = document.getElementById('newItemBtn');
    const itemForm = document.getElementById('itemForm');
    const itemTitleInput = document.getElementById('itemTitleInput');
    const itemCategorySelect = document.getElementById('itemCategorySelect');
    const itemContentInput = document.getElementById('itemContentInput');
    const triggerAiInspectBtn = document.getElementById('triggerAiInspectBtn');
    const generatePassBtn = document.getElementById('generatePassBtn');
    const inlineScanFeedback = document.getElementById('inlineScanFeedback');
    const scanRiskBadge = document.getElementById('scanRiskBadge');
    const scanFindingsList = document.getElementById('scanFindingsList');
    const scanRecommendations = document.getElementById('scanRecommendations');

    const inspectModal = document.getElementById('inspectModal');
    const inspectCategoryBadge = document.getElementById('inspectCategoryBadge');
    const inspectTitle = document.getElementById('inspectTitle');
    const inspectRiskLevel = document.getElementById('inspectRiskLevel');
    const inspectEntropy = document.getElementById('inspectEntropy');
    const inspectIv = document.getElementById('inspectIv');
    const inspectPlaintextContent = document.getElementById('inspectPlaintextContent');
    const inspectCiphertextPreview = document.getElementById('inspectCiphertextPreview');
    const copyDecryptedBtn = document.getElementById('copyDecryptedBtn');
    const deleteInspectItemBtn = document.getElementById('deleteInspectItemBtn');

    // Scanner Modal
    const scannerModal = document.getElementById('scannerModal');
    const aiScannerBtn = document.getElementById('aiScannerBtn');
    const scannerInput = document.getElementById('scannerInput');
    const scannerOutputArea = document.getElementById('scannerOutputArea');
    const encryptFromScannerBtn = document.getElementById('encryptFromScannerBtn');

    // File Modal
    const fileModal = document.getElementById('fileModal');
    const uploadFileBtn = document.getElementById('uploadFileBtn');
    const dropZone = document.getElementById('dropZone');
    const fileUploadInput = document.getElementById('fileUploadInput');
    const selectedFileInfo = document.getElementById('selectedFileInfo');
    const fileNameDisplay = document.getElementById('fileNameDisplay');
    const fileSizeDisplay = document.getElementById('fileSizeDisplay');
    const submitFileEncryptBtn = document.getElementById('submitFileEncryptBtn');
    let stagedFileData = null;

    // Audit Modal
    const auditModal = document.getElementById('auditModal');
    const auditBtn = document.getElementById('auditBtn');
    const auditTableBody = document.getElementById('auditTableBody');
    const auditAnomalyCount = document.getElementById('auditAnomalyCount');
    const refreshAuditBtn = document.getElementById('refreshAuditBtn');

    // Export & Toast
    const exportVaultBtn = document.getElementById('exportVaultBtn');
    const toast = document.getElementById('toastNotification');

    // --- Initial Status Check ---
    checkStatus();

    // --- Event Listeners with Safe Guards ---
    if (toggleLockBtn) {
        toggleLockBtn.addEventListener('click', () => {
            if (vaultState.isUnlocked) {
                lockVault();
            } else if (masterPasswordInput) {
                masterPasswordInput.focus();
            }
        });
    }

    if (unlockForm) {
        unlockForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const pwd = masterPasswordInput.value.trim();
            if (!pwd) return;
            await unlockVault(pwd);
        });
    }

    if (fillDemoMasterBtn) {
        fillDemoMasterBtn.addEventListener('click', () => {
            masterPasswordInput.value = "CyberVault2026!SecureKey#99";
            unlockVault(masterPasswordInput.value);
        });
    }

    if (fillDemoDuressBtn) {
        fillDemoDuressBtn.addEventListener('click', () => {
            masterPasswordInput.value = "9999";
            unlockVault(masterPasswordInput.value);
        });
    }

    // Search and Tabs
    if (vaultSearchInput) {
        vaultSearchInput.addEventListener('input', (e) => {
            vaultState.searchQuery = e.target.value.toLowerCase();
            renderItems();
        });
    }

    catTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            catTabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            vaultState.currentFilter = tab.dataset.category;
            renderItems();
        });
    });

    // Modals
    if (newItemBtn) newItemBtn.addEventListener('click', () => openModal(itemModal));
    if (aiScannerBtn) aiScannerBtn.addEventListener('click', () => openModal(scannerModal));
    if (uploadFileBtn) uploadFileBtn.addEventListener('click', () => openModal(fileModal));
    if (auditBtn) {
        auditBtn.addEventListener('click', () => {
            openModal(auditModal);
            loadAuditLogs();
        });
    }
    if (refreshAuditBtn) refreshAuditBtn.addEventListener('click', loadAuditLogs);

    // Modal Close buttons
    document.querySelectorAll('[data-close]').forEach(btn => {
        btn.addEventListener('click', () => {
            const targetId = btn.dataset.close;
            const targetModal = document.getElementById(targetId);
            if (targetModal) closeModal(targetModal);
        });
    });

    // New Item Submission
    if (itemForm) {
        itemForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const title = itemTitleInput.value.trim();
            const category = itemCategorySelect.value;
            const content = itemContentInput.value;

            try {
                const res = await fetch('/api/items', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ title, category, content })
                });
                const data = await res.json();
                if (data.success) {
                    showToast("Item successfully encrypted and stored.");
                    closeModal(itemModal);
                    itemForm.reset();
                    inlineScanFeedback?.classList.add('hidden');
                    await loadItems();
                    await checkStatus();
                } else {
                    showToast("Error: " + (data.error || "Failed to save item"));
                }
            } catch (err) {
                showToast("Network error: " + err.message);
            }
        });
    }

    // AI Scanner inside Item Modal
    if (triggerAiInspectBtn) {
        triggerAiInspectBtn.addEventListener('click', async () => {
            const content = itemContentInput.value;
            if (!content) {
                showToast("Please enter text first to analyze.");
                return;
            }
            await inspectContentWithAi(content);
        });
    }

    let scanDebounceTimer = null;
    if (itemContentInput) {
        itemContentInput.addEventListener('input', () => {
            clearTimeout(scanDebounceTimer);
            scanDebounceTimer = setTimeout(() => {
                const text = itemContentInput.value.trim();
                if (text.length > 5) {
                    inspectContentWithAi(text);
                } else if (inlineScanFeedback) {
                    inlineScanFeedback.classList.add('hidden');
                }
            }, 600);
        });
    }

    // Password Generator
    if (generatePassBtn) {
        generatePassBtn.addEventListener('click', () => {
            const pass = generateSecurePassphrase();
            itemContentInput.value = pass;
            itemCategorySelect.value = "CREDENTIAL";
            inspectContentWithAi(pass);
        });
    }

    // Copy Decrypted Plaintext
    if (copyDecryptedBtn) {
        copyDecryptedBtn.addEventListener('click', () => {
            const text = inspectPlaintextContent.innerText;
            navigator.clipboard.writeText(text);
            showToast("Copied to clipboard.");
        });
    }

    // Delete Item
    if (deleteInspectItemBtn) {
        deleteInspectItemBtn.addEventListener('click', async () => {
            if (!vaultState.activeInspectItem) return;
            if (confirm(`Delete '${vaultState.activeInspectItem.title}' from the vault?`)) {
                try {
                    const res = await fetch(`/api/items?id=${vaultState.activeInspectItem.id}`, { method: 'DELETE' });
                    const data = await res.json();
                    if (data.success) {
                        showToast("Item deleted.");
                        closeModal(inspectModal);
                        await loadItems();
                        await checkStatus();
                    }
                } catch (err) {
                    showToast("Failed to delete: " + err.message);
                }
            }
        });
    }

    // Export Vault
    if (exportVaultBtn) {
        exportVaultBtn.addEventListener('click', () => {
            window.location.href = '/api/export';
            showToast("Vault package exported.");
        });
    }

    // File Drag & Drop
    if (dropZone && fileUploadInput) {
        dropZone.addEventListener('click', () => fileUploadInput.click());
        dropZone.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropZone.style.borderColor = 'var(--ds-blue-primary)';
        });
        dropZone.addEventListener('dragleave', () => {
            dropZone.style.borderColor = 'var(--ds-border)';
        });
        dropZone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropZone.style.borderColor = 'var(--ds-border)';
            if (e.dataTransfer.files.length > 0) {
                handleSelectedFile(e.dataTransfer.files[0]);
            }
        });
        fileUploadInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                handleSelectedFile(e.target.files[0]);
            }
        });
    }

    if (submitFileEncryptBtn) {
        submitFileEncryptBtn.addEventListener('click', async () => {
            if (!stagedFileData) return;
            try {
                submitFileEncryptBtn.disabled = true;
                submitFileEncryptBtn.innerText = "Encrypting...";
                const res = await fetch('/api/items', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        title: stagedFileData.name,
                        category: "FILE",
                        content: stagedFileData.base64
                    })
                });
                const data = await res.json();
                if (data.success) {
                    showToast(`File '${stagedFileData.name}' encrypted.`);
                    closeModal(fileModal);
                    stagedFileData = null;
                    selectedFileInfo?.classList.add('hidden');
                    submitFileEncryptBtn.innerText = "Encrypt File";
                    await loadItems();
                    await checkStatus();
                }
            } catch (err) {
                showToast("Error: " + err.message);
            } finally {
                submitFileEncryptBtn.disabled = false;
            }
        });
    }

    // Scanner presets
    const sampleAws = document.getElementById('loadSampleAws');
    const samplePii = document.getElementById('loadSamplePii');
    const sampleSsh = document.getElementById('loadSampleSsh');
    const sampleSafe = document.getElementById('loadSampleSafe');

    if (sampleAws) {
        sampleAws.addEventListener('click', () => {
            scannerInput.value = "AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE\nAWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY\nPAYMENT_API_SECRET=sample_sec_token_9876543210_demo\nRegion: us-east-1";
            runDeepAiScanner(scannerInput.value);
        });
    }
    if (samplePii) {
        samplePii.addEventListener('click', () => {
            scannerInput.value = "Customer Patient Profile:\nName: Johnathan Doe\nSSN: 394-82-1924\nVisa Card: 4111 5222 6333 7444\nEmail: jdoe@medical-cloud.org";
            runDeepAiScanner(scannerInput.value);
        });
    }
    if (sampleSsh) {
        sampleSsh.addEventListener('click', () => {
            scannerInput.value = "-----BEGIN RSA PRIVATE KEY-----\nMIIEowIBAAKCAQEA0p9K34lskjdf9834kjshdf9834hkjsdf9834hkjsdf\n... [Production Key] ...\n-----END RSA PRIVATE KEY-----";
            runDeepAiScanner(scannerInput.value);
        });
    }
    if (sampleSafe) {
        sampleSafe.addEventListener('click', () => {
            scannerInput.value = "Morning reflection:\nReviewing distributed caching architecture and reading system design chapter.";
            runDeepAiScanner(scannerInput.value);
        });
    }

    if (scannerInput) {
        scannerInput.addEventListener('input', () => {
            const text = scannerInput.value.trim();
            if (text.length > 5) {
                runDeepAiScanner(text);
            } else if (scannerOutputArea) {
                scannerOutputArea.innerHTML = `<div style="color: var(--ds-text-subtlest); text-align: center; padding: 20px 0;">Type above or select a preset to trigger AI inspection.</div>`;
                if (encryptFromScannerBtn) encryptFromScannerBtn.disabled = true;
            }
        });
    }

    if (encryptFromScannerBtn) {
        encryptFromScannerBtn.addEventListener('click', () => {
            const text = scannerInput.value.trim();
            if (!text) return;
            closeModal(scannerModal);
            openModal(itemModal);
            itemTitleInput.value = "Analyzed Item (" + new Date().toLocaleTimeString() + ")";
            itemContentInput.value = text;
            inspectContentWithAi(text);
        });
    }

    // --- Core Operations ---

    async function checkStatus() {
        try {
            const res = await fetch('/api/status');
            const data = await res.json();
            vaultState.isUnlocked = !!data.isUnlocked;
            vaultState.isDecoyMode = !!data.isDecoyMode;

            updateStatusUi(data);

            if (vaultState.isUnlocked) {
                lockedView?.classList.add('hidden');
                unlockedView?.classList.remove('hidden');
                await loadItems();
            } else {
                lockedView?.classList.remove('hidden');
                unlockedView?.classList.add('hidden');
            }
        } catch (err) {
            console.error("Status check failed", err);
        }
    }

    function updateStatusUi(data) {
        if (vaultStatusLozenge) {
            if (data.isUnlocked) {
                if (data.isDecoyMode) {
                    vaultStatusLozenge.className = "lozenge lozenge-moved";
                    vaultStatusLozenge.innerText = "DECOY ACTIVE";
                    decoyBanner?.classList.remove('hidden');
                } else {
                    vaultStatusLozenge.className = "lozenge lozenge-success";
                    vaultStatusLozenge.innerText = "UNLOCKED";
                    decoyBanner?.classList.add('hidden');
                }
            } else {
                vaultStatusLozenge.className = "lozenge lozenge-removed";
                vaultStatusLozenge.innerText = "LOCKED";
                decoyBanner?.classList.add('hidden');
            }
        }

        if (lockBtnText) {
            lockBtnText.innerText = data.isUnlocked ? "Lock Vault" : "Unlock Vault";
        }

        if (healthScoreValue) {
            healthScoreValue.innerText = (data.vaultHealthScore || 98) + "%";
        }
        if (totalItemsValue) {
            totalItemsValue.innerText = data.itemCount || 0;
        }

        const anomalies = data.anomaliesDetected || 0;
        if (activeAnomaliesText) {
            activeAnomaliesText.innerText = anomalies === 0 ? "0 Alerts" : `${anomalies} Anomaly`;
        }
        if (anomalyBadge) {
            if (anomalies > 0) {
                anomalyBadge.innerText = anomalies;
                anomalyBadge.classList.remove('hidden');
            } else {
                anomalyBadge.classList.add('hidden');
            }
        }
    }

    async function unlockVault(password) {
        if (unlockErrorMsg) unlockErrorMsg.classList.add('hidden');
        try {
            const res = await fetch('/api/unlock', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ password })
            });
            const data = await res.json();
            if (data.success) {
                showToast(data.message || "Vault unlocked.");
                if (masterPasswordInput) masterPasswordInput.value = "";
                await checkStatus();
            } else {
                if (unlockErrorMsg) {
                    unlockErrorMsg.innerText = data.message || "Invalid password or PIN.";
                    unlockErrorMsg.classList.remove('hidden');
                }
                await checkStatus();
            }
        } catch (err) {
            if (unlockErrorMsg) {
                unlockErrorMsg.innerText = "Connection error: " + err.message;
                unlockErrorMsg.classList.remove('hidden');
            }
        }
    }

    async function lockVault() {
        try {
            const res = await fetch('/api/lock', { method: 'POST' });
            const data = await res.json();
            showToast(data.message || "Vault locked.");
            await checkStatus();
        } catch (err) {
            showToast("Failed to lock: " + err.message);
        }
    }

    async function loadItems() {
        try {
            const res = await fetch('/api/items');
            if (res.status === 403) {
                await checkStatus();
                return;
            }
            const data = await res.json();
            vaultState.items = data.items || [];
            if (totalItemsValue) totalItemsValue.innerText = vaultState.items.length;
            renderItems();
        } catch (err) {
            console.error("Failed loading items", err);
        }
    }

    function renderItems() {
        if (!itemsTableBody) return;
        itemsTableBody.innerHTML = '';

        const filtered = vaultState.items.filter(item => {
            const matchesCategory = vaultState.currentFilter === 'ALL' || item.category === vaultState.currentFilter;
            const searchLower = vaultState.searchQuery;
            const matchesSearch = !searchLower || 
                (item.title && item.title.toLowerCase().includes(searchLower)) ||
                (item.previewDescription && item.previewDescription.toLowerCase().includes(searchLower)) ||
                (item.aiTags && item.aiTags.some(t => t.toLowerCase().includes(searchLower)));
            return matchesCategory && matchesSearch;
        });

        if (filtered.length === 0) {
            emptyTableState?.classList.remove('hidden');
            return;
        } else {
            emptyTableState?.classList.add('hidden');
        }

        filtered.forEach(item => {
            const tr = document.createElement('tr');
            const lozengeClass = getAtlassianLozengeClass(item.riskLevel);
            const tagsHtml = (item.aiTags || []).map(t => `<span class="tag-badge">${escapeHtml(t)}</span>`).join('');

            tr.innerHTML = `
                <td>
                    <a href="javascript:void(0)" class="item-title-link inspect-link">${escapeHtml(item.title)}</a>
                    <div class="item-preview-text">${escapeHtml(item.previewDescription || 'Encrypted content')}</div>
                </td>
                <td>
                    <span class="lozenge lozenge-default">${escapeHtml(item.category || 'NOTE')}</span>
                </td>
                <td>
                    <span class="lozenge ${lozengeClass}">${escapeHtml(item.riskLevel || 'SAFE')}</span>
                </td>
                <td style="font-family: var(--font-mono); font-size: 12px; color: var(--ds-text-subtle);">
                    ${item.entropyScore || 0}%
                </td>
                <td>
                    <div class="tags-cell">${tagsHtml}</div>
                </td>
                <td style="text-align: right;">
                    <button class="btn btn-default btn-sm inspect-link">Decrypt</button>
                </td>
            `;

            tr.querySelectorAll('.inspect-link').forEach(el => {
                el.addEventListener('click', () => decryptAndInspect(item));
            });

            itemsTableBody.appendChild(tr);
        });
    }

    async function decryptAndInspect(item) {
        vaultState.activeInspectItem = item;
        if (inspectTitle) inspectTitle.innerText = item.title;
        if (inspectCategoryBadge) inspectCategoryBadge.innerText = item.category || 'NOTE';
        if (inspectRiskLevel) {
            inspectRiskLevel.className = `lozenge ${getAtlassianLozengeClass(item.riskLevel)}`;
            inspectRiskLevel.innerText = item.riskLevel || 'SAFE';
        }
        if (inspectEntropy) inspectEntropy.innerText = (item.entropyScore || 0) + '%';
        if (inspectIv) inspectIv.innerText = item.ivBase64 ? item.ivBase64.substring(0, 16) + '...' : 'AES-GCM-IV';
        if (inspectCiphertextPreview) inspectCiphertextPreview.innerText = item.ciphertextBase64 || 'Stored securely';
        if (inspectPlaintextContent) inspectPlaintextContent.innerText = "Decrypting via Java Cryptographic Engine...";

        openModal(inspectModal);

        try {
            const res = await fetch('/api/decrypt', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: item.id })
            });
            const data = await res.json();
            if (data.success) {
                if (item.category === 'FILE' && data.plaintext.startsWith('data:')) {
                    inspectPlaintextContent.innerHTML = `
                        <div style="padding: 10px 0;">
                            <p style="margin-bottom: 8px; color: var(--ds-green-text); font-weight: 600;">Decrypted binary file ready for download:</p>
                            <a href="${data.plaintext}" download="${escapeHtml(item.originalFileName || item.title)}" class="btn btn-primary btn-sm">
                                ⬇️ Download File (${formatBytes(item.fileSize)})
                            </a>
                        </div>
                    `;
                } else {
                    inspectPlaintextContent.innerText = data.plaintext;
                }
            } else {
                inspectPlaintextContent.innerText = "Error: " + (data.error || "Authentication failed");
            }
        } catch (err) {
            inspectPlaintextContent.innerText = "Decryption failed: " + err.message;
        }
    }

    async function inspectContentWithAi(text) {
        try {
            const res = await fetch('/api/ai/scan', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ content: text })
            });
            const scan = await res.json();

            if (inlineScanFeedback) inlineScanFeedback.classList.remove('hidden');
            if (scanRiskBadge) {
                scanRiskBadge.className = `lozenge ${getAtlassianLozengeClass(scan.riskLevel)}`;
                scanRiskBadge.innerText = scan.riskLevel;
            }

            if (scan.suggestedCategory && itemCategorySelect) {
                itemCategorySelect.value = scan.suggestedCategory;
            }

            if (scanFindingsList) {
                if (scan.findings && scan.findings.length > 0) {
                    scanFindingsList.innerHTML = scan.findings.map(f => `
                        <div class="scan-finding-row">
                            <span class="lozenge ${getAtlassianLozengeClass(f.severity)}">${f.severity}</span>
                            <span><strong>${escapeHtml(f.type)}:</strong> ${escapeHtml(f.description)}</span>
                        </div>
                    `).join('');
                } else {
                    scanFindingsList.innerHTML = `<span style="color: var(--ds-green-text); font-size: 12px; font-weight: 500;">✓ No sensitive leaks or unmasked credentials detected.</span>`;
                }
            }

            if (scanRecommendations) {
                if (scan.recommendations && scan.recommendations.length > 0) {
                    scanRecommendations.innerHTML = `<strong>Guidance:</strong> ` + scan.recommendations.map(escapeHtml).join(' | ');
                } else {
                    scanRecommendations.innerHTML = `<strong>Guidance:</strong> Content meets standard security hygiene.`;
                }
            }
        } catch (err) {
            console.error("AI Scan failed", err);
        }
    }

    async function runDeepAiScanner(text) {
        try {
            const res = await fetch('/api/ai/scan', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ content: text })
            });
            const scan = await res.json();

            let findingsHtml = '';
            if (scan.findings && scan.findings.length > 0) {
                findingsHtml = `
                    <div style="margin-bottom: 12px;">
                        <div style="font-size: 11px; font-weight: 700; color: var(--ds-text-subtle); margin-bottom: 6px; text-transform: uppercase;">Detected Vulnerabilities & Secrets:</div>
                        ${scan.findings.map(f => `
                            <div class="scan-finding-row">
                                <span class="lozenge ${getAtlassianLozengeClass(f.severity)}">${f.severity}</span>
                                <div><strong>${escapeHtml(f.type)}:</strong> ${escapeHtml(f.description)} ${f.snippet ? `<code style="font-size: 11px; color: var(--ds-text-subtle); margin-left: 4px;">(${escapeHtml(f.snippet)})</code>` : ''}</div>
                            </div>
                        `).join('')}
                    </div>
                `;
            } else {
                findingsHtml = `
                    <div style="padding: 8px 10px; background: var(--ds-green-subtle); border-radius: var(--radius-sm); margin-bottom: 12px;">
                        <span style="color: var(--ds-green-text); font-size: 13px; font-weight: 500;">✓ Content is clean. No unmasked credentials, private keys, or PII identified.</span>
                    </div>
                `;
            }

            const recHtml = (scan.recommendations || []).map(r => `<li>${escapeHtml(r)}</li>`).join('');
            const tagsHtml = (scan.suggestedTags || []).map(t => `<span class="tag-badge">${escapeHtml(t)}</span>`).join('');

            if (scannerOutputArea) {
                scannerOutputArea.innerHTML = `
                    <div style="display: flex; gap: 16px; margin-bottom: 14px; padding-bottom: 10px; border-bottom: 1px solid var(--ds-border);">
                        <div>
                            <span style="font-size: 11px; font-weight: 700; color: var(--ds-text-subtle);">ASSESSED THREAT</span>
                            <div style="margin-top: 2px;"><span class="lozenge ${getAtlassianLozengeClass(scan.riskLevel)}">${scan.riskLevel}</span></div>
                        </div>
                        <div style="border-left: 1px solid var(--ds-border); padding-left: 14px;">
                            <span style="font-size: 11px; font-weight: 700; color: var(--ds-text-subtle);">SHANNON ENTROPY</span>
                            <div style="margin-top: 2px; font-weight: 600; font-family: var(--font-mono);">${scan.shannonEntropy} bits</div>
                        </div>
                        <div style="border-left: 1px solid var(--ds-border); padding-left: 14px;">
                            <span style="font-size: 11px; font-weight: 700; color: var(--ds-text-subtle);">CATEGORY</span>
                            <div style="margin-top: 2px;"><span class="lozenge lozenge-default">${scan.suggestedCategory || 'NOTE'}</span></div>
                        </div>
                    </div>

                    ${findingsHtml}

                    <div style="margin-bottom: 10px;">
                        <span style="font-size: 11px; font-weight: 700; color: var(--ds-text-subtle); text-transform: uppercase;">Inferred Tags:</span>
                        <div style="display: flex; gap: 4px; margin-top: 4px;">${tagsHtml}</div>
                    </div>

                    ${recHtml ? `
                        <div style="font-size: 12px; color: var(--ds-text-subtle); border-top: 1px solid var(--ds-border); padding-top: 8px;">
                            <strong>Security Recommendations:</strong>
                            <ul style="padding-left: 18px; margin-top: 4px;">${recHtml}</ul>
                        </div>
                    ` : ''}
                `;
            }

            if (encryptFromScannerBtn) encryptFromScannerBtn.disabled = false;
        } catch (err) {
            if (scannerOutputArea) scannerOutputArea.innerHTML = `<div style="color: var(--ds-red-text);">Error: ${err.message}</div>`;
        }
    }

    function handleSelectedFile(file) {
        if (!file) return;
        const reader = new FileReader();
        reader.onload = (e) => {
            stagedFileData = {
                name: file.name,
                size: file.size,
                base64: e.target.result
            };
            if (fileNameDisplay) fileNameDisplay.innerText = file.name;
            if (fileSizeDisplay) fileSizeDisplay.innerText = formatBytes(file.size);
            selectedFileInfo?.classList.remove('hidden');
            if (submitFileEncryptBtn) submitFileEncryptBtn.disabled = false;
        };
        reader.readAsDataURL(file);
    }

    async function loadAuditLogs() {
        try {
            const res = await fetch('/api/audit');
            const data = await res.json();
            if (auditAnomalyCount) auditAnomalyCount.innerText = data.anomaliesDetected || 0;
            const events = data.events || [];
            
            if (auditTableBody) {
                auditTableBody.innerHTML = events.map(ev => {
                    const dateStr = new Date(ev.timestamp).toLocaleTimeString();
                    const lozengeClass = ev.status === 'SUCCESS' ? 'lozenge-success' : (ev.status === 'DENIED' ? 'lozenge-removed' : 'lozenge-moved');
                    return `
                        <tr>
                            <td style="color: var(--ds-text-subtle); font-family: var(--font-mono);">${dateStr}</td>
                            <td style="font-weight: 600; color: var(--ds-text);">${escapeHtml(ev.action)}</td>
                            <td>${escapeHtml(ev.details)}</td>
                            <td><span class="lozenge ${lozengeClass}">${escapeHtml(ev.status)}</span></td>
                            <td style="color: var(--ds-text-subtle); font-family: var(--font-mono);">${escapeHtml(ev.ipAddress || '127.0.0.1')}</td>
                        </tr>
                    `;
                }).join('');
            }
        } catch (err) {
            console.error("Failed to load audit logs", err);
        }
    }

    function generateSecurePassphrase() {
        const words = ["Shield", "Aegis", "Vortex", "Horizon", "Cipher", "Cascade", "Titan", "Solar", "Falcon", "Vertex", "Matrix", "Echo"];
        const chars = "!@#$%&*";
        const w1 = words[Math.floor(Math.random() * words.length)];
        const w2 = words[Math.floor(Math.random() * words.length)];
        const num = Math.floor(100 + Math.random() * 900);
        const sym = chars[Math.floor(Math.random() * chars.length)];
        return `${w1}-${w2}-${num}${sym}`;
    }

    function getAtlassianLozengeClass(level) {
        switch ((level || '').toUpperCase()) {
            case 'CRITICAL': return 'lozenge-bold-red';
            case 'HIGH': return 'lozenge-removed';
            case 'MEDIUM': return 'lozenge-moved';
            case 'LOW': return 'lozenge-inprogress';
            case 'SAFE':
            default: return 'lozenge-success';
        }
    }

    function openModal(modal) {
        if (modal) modal.classList.remove('hidden');
    }

    function closeModal(modal) {
        if (modal) modal.classList.add('hidden');
    }

    function showToast(message) {
        if (!toast) return;
        toast.innerText = message;
        toast.classList.remove('hidden');
        setTimeout(() => toast.classList.add('hidden'), 3500);
    }

    function escapeHtml(str) {
        if (!str) return '';
        return String(str).replace(/[&<>"']/g, m => ({
            '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
        }[m]));
    }

    function formatBytes(bytes) {
        if (!bytes || bytes === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
    }
});

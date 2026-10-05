package vault.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import vault.ai.AiSecurityAdvisor;
import vault.ai.VaultSentinel;
import vault.crypto.VaultManager;
import vault.model.ScanResult;
import vault.model.VaultItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Handler for AI Vault Operations.
 */
public class ApiHandler implements HttpHandler {
    private final VaultManager vaultManager;
    private final VaultSentinel sentinel;
    private final AiSecurityAdvisor aiAdvisor;
    private final Gson gson = new Gson();

    public ApiHandler(VaultManager vaultManager, VaultSentinel sentinel, AiSecurityAdvisor aiAdvisor) {
        this.vaultManager = vaultManager;
        this.sentinel = sentinel;
        this.aiAdvisor = aiAdvisor;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Enable CORS
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();
        String clientIp = exchange.getRemoteAddress().getAddress().getHostAddress();

        try {
            if ("/api/status".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleStatus(exchange);
            } else if ("/api/unlock".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleUnlock(exchange, clientIp);
            } else if ("/api/lock".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleLock(exchange, clientIp);
            } else if ("/api/items".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleGetItems(exchange);
            } else if ("/api/items".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleAddItem(exchange, clientIp);
            } else if ("/api/decrypt".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleDecryptItem(exchange, clientIp);
            } else if ("/api/items".equals(path) && "DELETE".equalsIgnoreCase(method)) {
                handleDeleteItem(exchange, clientIp);
            } else if ("/api/ai/scan".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleAiScan(exchange);
            } else if ("/api/audit".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleGetAudit(exchange);
            } else if ("/api/export".equals(path) && "GET".equalsIgnoreCase(method)) {
                handleExport(exchange);
            } else if ("/api/import".equals(path) && "POST".equalsIgnoreCase(method)) {
                handleImport(exchange, clientIp);
            } else {
                sendJsonResponse(exchange, 404, Map.of("error", "Endpoint not found: " + path));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, Map.of("error", e.getMessage() != null ? e.getMessage() : "Internal server error"));
        }
    }

    private void handleStatus(HttpExchange exchange) throws IOException {
        Map<String, Object> metrics = vaultManager.getMetrics();
        sendJsonResponse(exchange, 200, metrics);
    }

    private void handleUnlock(HttpExchange exchange, String clientIp) throws IOException {
        String body = readRequestBody(exchange);
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        String password = json.has("password") ? json.get("password").getAsString() : "";

        boolean success = vaultManager.unlock(password.toCharArray(), clientIp);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("isUnlocked", vaultManager.isUnlocked());
        response.put("isDecoyMode", vaultManager.isDecoyMode());
        if (!success) {
            response.put("message", "Authentication failed. Incorrect master password or PIN.");
            sendJsonResponse(exchange, 401, response);
        } else {
            response.put("message", vaultManager.isDecoyMode() ? "Decoy emergency vault unlocked." : "Master Vault unlocked.");
            sendJsonResponse(exchange, 200, response);
        }
    }

    private void handleLock(HttpExchange exchange, String clientIp) throws IOException {
        vaultManager.lock(clientIp);
        sendJsonResponse(exchange, 200, Map.of("success", true, "message", "Vault safely locked. In-memory keys purged."));
    }

    private void handleGetItems(HttpExchange exchange) throws IOException {
        if (!vaultManager.isUnlocked()) {
            sendJsonResponse(exchange, 403, Map.of("error", "Vault is locked. Authenticate first."));
            return;
        }
        List<VaultItem> items = vaultManager.getItems();
        sendJsonResponse(exchange, 200, Map.of(
            "items", items,
            "isDecoyMode", vaultManager.isDecoyMode(),
            "total", items.size()
        ));
    }

    private void handleAddItem(HttpExchange exchange, String clientIp) throws IOException {
        if (!vaultManager.isUnlocked()) {
            sendJsonResponse(exchange, 403, Map.of("error", "Vault is locked."));
            return;
        }
        String body = readRequestBody(exchange);
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();

        String title = json.has("title") ? json.get("title").getAsString() : "Untitled Item";
        String category = json.has("category") ? json.get("category").getAsString() : "NOTE";
        String content = json.has("content") ? json.get("content").getAsString() : "";

        VaultItem created = vaultManager.addItem(title, category, content, clientIp);
        sendJsonResponse(exchange, 201, Map.of(
            "success", true,
            "item", created
        ));
    }

    private void handleDecryptItem(HttpExchange exchange, String clientIp) throws IOException {
        if (!vaultManager.isUnlocked()) {
            sendJsonResponse(exchange, 403, Map.of("error", "Vault is locked."));
            return;
        }
        String body = readRequestBody(exchange);
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        String id = json.has("id") ? json.get("id").getAsString() : "";

        try {
            String plaintext = vaultManager.decryptItem(id, clientIp);
            sendJsonResponse(exchange, 200, Map.of(
                "success", true,
                "id", id,
                "plaintext", plaintext
            ));
        } catch (Exception e) {
            sendJsonResponse(exchange, 404, Map.of("error", e.getMessage()));
        }
    }

    private void handleDeleteItem(HttpExchange exchange, String clientIp) throws IOException {
        if (!vaultManager.isUnlocked()) {
            sendJsonResponse(exchange, 403, Map.of("error", "Vault is locked."));
            return;
        }
        String query = exchange.getRequestURI().getQuery();
        String id = null;
        if (query != null && query.startsWith("id=")) {
            id = query.substring(3);
        }
        if (id == null) {
            sendJsonResponse(exchange, 400, Map.of("error", "Missing id parameter"));
            return;
        }
        boolean removed = vaultManager.deleteItem(id, clientIp);
        sendJsonResponse(exchange, 200, Map.of("success", removed));
    }

    private void handleAiScan(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        JsonObject json = JsonParser.parseString(body).getAsJsonObject();
        String content = json.has("content") ? json.get("content").getAsString() : "";

        ScanResult scan = aiAdvisor.analyze(content);
        sendJsonResponse(exchange, 200, scan);
    }

    private void handleGetAudit(HttpExchange exchange) throws IOException {
        sendJsonResponse(exchange, 200, Map.of(
            "events", sentinel.getRecentEvents(40),
            "anomaliesDetected", sentinel.getAnomalyCount()
        ));
    }

    private void handleExport(HttpExchange exchange) throws IOException {
        String json = vaultManager.exportVaultJson();
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"personal-vault.enc.json\"");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void handleImport(HttpExchange exchange, String clientIp) throws IOException {
        String body = readRequestBody(exchange);
        try {
            vaultManager.importVaultJson(body, clientIp);
            sendJsonResponse(exchange, 200, Map.of("success", true, "message", "Vault imported successfully."));
        } catch (Exception e) {
            sendJsonResponse(exchange, 400, Map.of("error", "Import failed: " + e.getMessage()));
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String json = gson.toJson(data);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}

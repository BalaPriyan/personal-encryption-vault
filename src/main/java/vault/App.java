package vault;

import com.sun.net.httpserver.HttpServer;
import vault.ai.AiSecurityAdvisor;
import vault.ai.VaultSentinel;
import vault.crypto.VaultManager;
import vault.server.ApiHandler;
import vault.server.StaticFileHandler;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main application bootstrap for the AI-Based Personal Encryption Vault.
 */
public class App {
    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String portEnv = System.getenv("PORT");
        if (portEnv != null) {
            try {
                port = Integer.parseInt(portEnv);
            } catch (NumberFormatException ignored) {}
        }
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        try {
            VaultSentinel sentinel = new VaultSentinel();
            AiSecurityAdvisor aiAdvisor = new AiSecurityAdvisor();
            VaultManager vaultManager = new VaultManager(sentinel, aiAdvisor);

            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // Register API routes
            ApiHandler apiHandler = new ApiHandler(vaultManager, sentinel, aiAdvisor);
            server.createContext("/api", apiHandler);

            // Determine web directory (prefer src/main/resources/web or local web/)
            String webDir = "src/main/resources/web";
            if (!new File(webDir).exists()) {
                webDir = "web";
            }
            server.createContext("/", new StaticFileHandler(webDir));

            server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
            server.start();

            System.out.println("=================================================================");
            System.out.println("   AI-POWERED PERSONAL ENCRYPTION VAULT");
            System.out.println("=================================================================");
            System.out.println(" Cryptographic Engine: AES-256-GCM + PBKDF2WithHmacSHA256 (65k)");
            System.out.println(" AI Privacy Sentinel : Deep PII / Secret / Anomaly Scanner Active");
            System.out.println(" Plausible Deniability: Duress PIN System Active");
            System.out.println("-----------------------------------------------------------------");
            System.out.println(" Web Dashboard: http://localhost:" + port);
            System.out.println(" Master Password: " + VaultManager.DEMO_MASTER_PASSWORD);
            System.out.println(" Duress PIN     : " + VaultManager.DEMO_DURESS_PIN + " (Opens Decoy Vault)");
            System.out.println("=================================================================");

        } catch (Exception e) {
            System.err.println("Fatal: Server startup failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

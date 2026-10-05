package vault.ai;

import vault.crypto.CryptoEngine;
import vault.model.VaultItem;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Plausible Deniability & Decoy Vault Synthesizer.
 * Generates realistic decoy notes and harmless records for Duress Mode.
 */
public class DecoyGenerator {

    public static List<VaultItem> generateDefaultDecoyItems(SecretKey decoyKey) {
        List<VaultItem> items = new ArrayList<>();

        items.add(createEncryptedDecoyItem(
            "Weekly Fitness Routine & Meal Plan",
            "NOTE",
            "Monday: Upper body (bench press 4x8, dumbbell rows 4x10, overhead press 3x10).\n" +
            "Tuesday: 5km recovery jog in park.\n" +
            "Wednesday: Lower body (squats 4x8, Romanian deadlifts 3x10).\n" +
            "Meal Prep: Grilled chicken with brown rice and steamed broccoli.",
            "SAFE",
            24,
            Arrays.asList("#Fitness", "#Personal", "#Health"),
            "Weekly gym schedule and meal prep instructions",
            decoyKey
        ));

        items.add(createEncryptedDecoyItem(
            "Book Recommendations 2026",
            "NOTE",
            "1. 'Thinking, Fast and Slow' by Daniel Kahneman\n" +
            "2. 'The Pragmatic Programmer' 20th Anniversary Edition\n" +
            "3. 'Project Hail Mary' by Andy Weir\n" +
            "4. 'Klara and the Sun' by Kazuo Ishiguro",
            "SAFE",
            18,
            Arrays.asList("#Books", "#ReadingList", "#Personal"),
            "List of literature and engineering books to read",
            decoyKey
        ));

        items.add(createEncryptedDecoyItem(
            "Apartment Paint Color Hex Codes",
            "NOTE",
            "Living Room: #E8ECEF (Soft Cloud Grey)\n" +
            "Bedroom Accent Wall: #2C3E50 (Midnight Slate)\n" +
            "Kitchen Cabinets: #F4F6F7 (Warm Alabaster)",
            "SAFE",
            15,
            Arrays.asList("#Home", "#Decoration"),
            "Color palette swatches for home renovation",
            decoyKey
        ));

        items.add(createEncryptedDecoyItem(
            "Grocery & Hardware Store Checklist",
            "NOTE",
            "- 2x LED Warm Light Bulbs (E26 60W equivalent)\n" +
            "- WD-40 Lubricant Spray\n" +
            "- AA Batteries pack of 12\n" +
            "- Ground organic coffee beans (Medium roast)",
            "SAFE",
            12,
            Arrays.asList("#Shopping", "#Home"),
            "Hardware store errands and grocery list",
            decoyKey
        ));

        return items;
    }

    private static VaultItem createEncryptedDecoyItem(
            String title, String category, String plaintext, String riskLevel,
            int entropy, List<String> tags, String preview, SecretKey key) {
        
        CryptoEngine.EncryptedPayload payload = CryptoEngine.encryptText(plaintext, key);
        return new VaultItem(
            UUID.randomUUID().toString(),
            title,
            category,
            payload.getCiphertextBase64(),
            payload.getIvBase64(),
            System.currentTimeMillis(),
            riskLevel,
            entropy,
            tags,
            preview,
            null,
            plaintext.getBytes().length
        );
    }
}

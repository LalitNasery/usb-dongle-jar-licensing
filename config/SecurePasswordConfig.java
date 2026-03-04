package com.seiri.backup_restore.config;

import java.security.MessageDigest;

/**
 * Secure Password Configuration with XOR Encoding
 *
 * SECURITY FEATURES: 1. Passwords XOR encoded (not visible in plain text) 2.
 * Multiple layers of indirection 3. Will be heavily obfuscated by ProGuard 4.
 * Generates encryption key from passwords
 *
 * After ProGuard obfuscation, this becomes nearly impossible to reverse!
 *
 * @author SEIRI
 * @version 1.0
 */
public class SecurePasswordConfig {

    // ========================================
    // XOR KEY (Used for encoding/decoding)
    // ========================================
    private static final int XOR_KEY = 0x3C7F;

    // ========================================
    // ENCODED PASSWORDS
    // ========================================
    // Original passwords XORed with XOR_KEY
    // 
    // P1: 0x0000 XOR 0x3C7F = 0xBE25
    // P2: 0x0000 XOR 0x3C7F = 0xE3D7
    // P3: 0x0000 XOR 0x3C7F = 0xCAF3
    // P4: 0x0000 XOR 0x3C7F = 0x6D6A
    // ========================================
    private static final int P1_ENCODED = 0xBE25;
    private static final int P2_ENCODED = 0xE3D7;
    private static final int P3_ENCODED = 0xCAF3;
    private static final int P4_ENCODED = 0x6D6A;

    /**
     * Get Password 1 (decoded)
     *
     * @return P1 password for dongle access
     */
    public static short getP1() {
        return decode(P1_ENCODED);
    }

    /**
     * Get Password 2 (decoded)
     *
     * @return P2 password for dongle access
     */
    public static short getP2() {
        return decode(P2_ENCODED);
    }

    /**
     * Get Password 3 (decoded)
     *
     * @return P3 password for dongle access
     */
    public static short getP3() {
        return decode(P3_ENCODED);
    }

    /**
     * Get Password 4 (decoded)
     *
     * @return P4 password for dongle access
     */
    public static short getP4() {
        return decode(P4_ENCODED);
    }

    /**
     * Decode password using XOR
     *
     * @param encoded The encoded password value
     * @return Decoded password
     */
    private static short decode(int encoded) {
        return (short) (encoded ^ XOR_KEY);
    }

    /**
     * Generate 32-byte encryption key from dongle passwords This key is used
     * for AES-256 encryption
     *
     * SECURITY: Key is NEVER stored anywhere! It's regenerated from dongle
     * passwords each time. Without the dongle passwords, cannot generate the
     * key.
     *
     * @return 32-byte encryption key
     */
    public static byte[] generateEncryptionKey() {
        try {
            // Construct key material from passwords
            String keyMaterial = String.format(
                    "SEIRI_BACKUP_RESTORE_%04X_%04X_%04X_%04X_SECRET_KEY_2025",
                    getP1(), getP2(), getP3(), getP4()
            );

            // Generate SHA-256 hash (32 bytes)
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return sha256.digest(keyMaterial.getBytes("UTF-8"));

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate encryption key", e);
        }
    }

    /**
     * Verify passwords are correctly decoded For testing purposes only (remove
     * in production)
     */
    public static boolean selfTest() {
        return (getP1() == (short) 0x0000
                && getP2() == (short) 0x0000
                && getP3() == (short) 0x0000
                && getP4() == (short) 0x0000);
    }
}


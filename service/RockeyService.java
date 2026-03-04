package com.seiri.backup_restore.service;

import com.seiri.backup_restore.jni.RockeyLibrary;
import com.seiri.backup_restore.config.SecurePasswordConfig;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.boot.SpringApplication;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * Enhanced Rockey Service with Secure Password Management
 *
 * SECURITY FEATURES: - Passwords loaded from SecurePasswordConfig (XOR encoded)
 * - Encryption key generated from passwords (not stored) - Continuous dongle
 * monitoring - Application shutdown if dongle removed
 *
 * @author lalit
 */
@Service
public class RockeyService {

    @Autowired
    private ApplicationContext appContext;

    // Monitoring settings from application.properties
    @Value("${rockey.monitoring.enabled:true}")
    private boolean monitoringEnabled;

    @Value("${rockey.monitoring.interval:2}")
    private int monitoringInterval;

    private RockeyLibrary rockey;
    private boolean libraryLoaded = false;
    private boolean dongleAvailable = false;
    private ScheduledExecutorService monitoringScheduler;
    private AtomicBoolean shutdownInProgress = new AtomicBoolean(false);

    // Passwords (loaded from SecurePasswordConfig)
    private short P1, P2, P3, P4;

    // Cached encryption key
    private byte[] cachedEncryptionKey = null;

    @PostConstruct
    public void init() {
        try {
            System.out.println("========================================");
            System.out.println("  ROCKEY4 SMART Initialization");
            System.out.println("========================================");

            // Load passwords from SecurePasswordConfig (XOR decoded)
            P1 = SecurePasswordConfig.getP1();
            P2 = SecurePasswordConfig.getP2();
            P3 = SecurePasswordConfig.getP3();
            P4 = SecurePasswordConfig.getP4();

            System.out.println("✓ Secure passwords loaded (XOR decoded)");
            System.out.println("  P1: " + String.format("0x%04X", P1));
            System.out.println("  P2: " + String.format("0x%04X", P2));
            System.out.println("  P3: " + String.format("0x%04X", P3));
            System.out.println("  P4: " + String.format("0x%04X", P4));

            // Verify passwords decoded correctly
            if (!SecurePasswordConfig.selfTest()) {
                throw new Exception("Password decoding verification failed!");
            }
            System.out.println("✓ Password verification: PASSED");

            // Load JNA library
            rockey = RockeyLibrary.INSTANCE;
            libraryLoaded = true;
            System.out.println("✓ ROCKEY4 SMART library loaded via JNA");

            // Verify dongle
            if (verifyDongle()) {
                dongleAvailable = true;

                // Generate encryption key from passwords
                generateEncryptionKey();

                Map<String, Object> info = getDongleInfo();
                System.out.println("✓ Dongle detected and verified!");
                System.out.println("  Hardware ID: " + info.get("hardwareId"));
                System.out.println("  Status: " + info.get("message"));
                System.out.println("✓ Encryption key generated from dongle passwords");
                System.out.println("  Key length: 32 bytes (AES-256)");

                // Start monitoring
                if (monitoringEnabled) {
                    startDongleMonitoring();
                }

                System.out.println("========================================");
                System.out.println("✓ Initialization Complete!");
                System.out.println("========================================");

            } else {
                System.err.println("✗ ROCKEY4 SMART dongle NOT FOUND!");
                System.err.println("✗ Please insert dongle and restart application.");
                System.exit(SpringApplication.exit(appContext, () -> 1));
            }
        } catch (UnsatisfiedLinkError e) {
            System.err.println("✗ Failed to load ROCKEY4 SMART library: " + e.getMessage());
            System.err.println("  Make sure libRockey4Smart.so is in /usr/local/lib");
            System.exit(SpringApplication.exit(appContext, () -> 1));
        } catch (Exception e) {
            System.err.println("✗ Error initializing dongle: " + e.getMessage());
            e.printStackTrace();
            System.exit(SpringApplication.exit(appContext, () -> 1));
        }
    }

    /**
     * Generate encryption key from dongle passwords NO EXTERNAL STORAGE - Key
     * regenerated each time!
     */
    private void generateEncryptionKey() {
        try {
            // Generate key from SecurePasswordConfig
            cachedEncryptionKey = SecurePasswordConfig.generateEncryptionKey();

        } catch (Exception e) {
            System.err.println("⚠️  Error generating encryption key: " + e.getMessage());
            cachedEncryptionKey = new byte[32];
        }
    }

    /**
     * Get encryption key for AES encryption
     */
    public byte[] getEncryptionKey() {
        if (cachedEncryptionKey == null) {
            generateEncryptionKey();
        }
        return cachedEncryptionKey;
    }

    /**
     * Encrypt data using dongle-derived key
     */
    public byte[] encryptData(byte[] data) throws Exception {
        requireDongle();

        byte[] key = getEncryptionKey();
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(data);
    }

    /**
     * Decrypt data using dongle-derived key
     */
    public byte[] decryptData(byte[] encryptedData) throws Exception {
        requireDongle();

        byte[] key = getEncryptionKey();
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encryptedData);
    }

    /**
     * Encrypt String (convenience method)
     */
    public String encryptString(String plainText) throws Exception {
        byte[] encrypted = encryptData(plainText.getBytes("UTF-8"));
        return java.util.Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypt String (convenience method)
     */
    public String decryptString(String encryptedBase64) throws Exception {
        byte[] encrypted = java.util.Base64.getDecoder().decode(encryptedBase64);
        byte[] decrypted = decryptData(encrypted);
        return new String(decrypted, "UTF-8");
    }

    private void startDongleMonitoring() {
        monitoringScheduler = Executors.newScheduledThreadPool(1);

        System.out.println("✓ Dongle monitoring started");
        System.out.println("  Interval: " + monitoringInterval + " seconds");
        System.out.println("  Mode: Continuous (any valid dongle accepted)");

        monitoringScheduler.scheduleAtFixedRate(() -> {
            try {
                if (!checkDonglePresent()) {
                    if (shutdownInProgress.compareAndSet(false, true)) {
                        System.err.println("\n╔════════════════════════════════════════════════╗");
                        System.err.println("║   ⚠️  CRITICAL: DONGLE REMOVED!               ║");
                        System.err.println("╚════════════════════════════════════════════════╝");
                        System.err.println("✗ ROCKEY4 SMART dongle has been disconnected!");
                        System.err.println("✗ Application will terminate immediately.");
                        System.err.println("✗ Please insert dongle and restart.\n");
                        System.exit(1);
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️  Monitoring error: " + e.getMessage());
            }
        }, monitoringInterval, monitoringInterval, TimeUnit.SECONDS);
    }

    private boolean checkDonglePresent() {
        if (!libraryLoaded || rockey == null) {
            return false;
        }

        try {
            int[] lp1 = new int[1];
            int[] lp2 = new int[1];
            short[] handle = new short[1];
            short[] p1 = {P1};
            short[] p2 = {P2};
            short[] p3 = {P3};
            short[] p4 = {P4};

            short ret = rockey.Rockey(RockeyLibrary.RY_FIND, handle, lp1, lp2,
                    p1, p2, p3, p4, null);

            return (ret == 0);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyDongle() {
        if (!libraryLoaded || rockey == null) {
            return false;
        }

        try {
            int[] lp1 = new int[1];
            int[] lp2 = new int[1];
            short[] handle = new short[1];
            short[] p1 = {P1};
            short[] p2 = {P2};
            short[] p3 = {P3};
            short[] p4 = {P4};

            // Try to find dongle
            short ret = rockey.Rockey(RockeyLibrary.RY_FIND, handle, lp1, lp2,
                    p1, p2, p3, p4, null);
            if (ret != 0) {
                System.err.println("  RY_FIND failed with error: " + ret);
                return false;
            }

            int detectedHardwareId = lp1[0];
            System.out.println("  Detected Hardware ID: 0x"
                    + Integer.toHexString(detectedHardwareId).toUpperCase());

            // Try to open
            ret = rockey.Rockey(RockeyLibrary.RY_OPEN, handle, lp1, lp2,
                    p1, p2, p3, p4, null);
            if (ret != 0) {
                System.err.println("  RY_OPEN failed with error: " + ret);
                return false;
            }

            // Close
            rockey.Rockey(RockeyLibrary.RY_CLOSE, handle, lp1, lp2,
                    p1, p2, p3, p4, null);

            return true;
        } catch (Exception e) {
            System.err.println("  Exception during verification: " + e.getMessage());
            return false;
        }
    }

    public boolean isDongleAvailable() {
        return libraryLoaded && dongleAvailable;
    }

    public Map<String, Object> getDongleInfo() {
        Map<String, Object> result = new HashMap<>();

        if (!libraryLoaded || rockey == null) {
            result.put("available", false);
            result.put("message", "Library not loaded");
            return result;
        }

        try {
            int[] lp1 = new int[1];
            int[] lp2 = new int[1];
            short[] handle = new short[1];
            short[] p1 = {P1};
            short[] p2 = {P2};
            short[] p3 = {P3};
            short[] p4 = {P4};

            short ret = rockey.Rockey(RockeyLibrary.RY_FIND, handle, lp1, lp2,
                    p1, p2, p3, p4, null);

            if (ret != 0) {
                result.put("available", false);
                result.put("message", "Dongle not found");
                result.put("errorCode", (int) ret);
                return result;
            }

            int hardwareId = lp1[0];

            ret = rockey.Rockey(RockeyLibrary.RY_OPEN, handle, lp1, lp2,
                    p1, p2, p3, p4, null);

            if (ret != 0) {
                result.put("available", false);
                result.put("hardwareId", String.format("0x%08X", hardwareId));
                result.put("message", "Dongle found but cannot open");
                result.put("errorCode", (int) ret);
                return result;
            }

            // Test write
            p1[0] = 0;
            p2[0] = 6;
            byte[] testData = "TEST!".getBytes();
            ret = rockey.Rockey(RockeyLibrary.RY_WRITE, handle, lp1, lp2,
                    p1, p2, p3, p4, testData);

            boolean canWrite = (ret == 0);

            rockey.Rockey(RockeyLibrary.RY_CLOSE, handle, lp1, lp2,
                    p1, p2, p3, p4, null);

            result.put("available", true);
            result.put("hardwareId", String.format("0x%08X", hardwareId));
            result.put("canWrite", canWrite);
            result.put("message", "Dongle fully functional");

        } catch (Exception e) {
            result.put("available", false);
            result.put("message", "Error: " + e.getMessage());
        }

        return result;
    }

    public void requireDongle() throws Exception {
        if (!isDongleAvailable() || !checkDonglePresent()) {
            throw new Exception("ROCKEY4 SMART dongle is required for this operation");
        }
    }

    @PreDestroy
    public void cleanup() {
        if (monitoringScheduler != null && !monitoringScheduler.isShutdown()) {
            System.out.println("Stopping dongle monitoring...");
            monitoringScheduler.shutdown();
        }

        // Clear cached key
        if (cachedEncryptionKey != null) {
            Arrays.fill(cachedEncryptionKey, (byte) 0);
            cachedEncryptionKey = null;
        }
    }
}


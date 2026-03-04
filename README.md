# USB Dongle-Based JAR Licensing System

A hardware-based software licensing mechanism for locally deployed Java/Spring Boot applications using the ROCKEY4 Smart USB dongle.

## What It Does

This system binds a Java application's execution to a physical USB dongle.

- Application **starts only if the dongle is inserted**
- Application **shuts down immediately** if the dongle is removed during runtime
- Application **requires a restart** after reinserting the dongle
- Encryption key is **derived from dongle passwords** — never stored anywhere

## How It Works

1. On startup, `RockeyService` tries to find and open the dongle using the ROCKEY4 Smart library
2. If dongle is not found — application exits immediately
3. If dongle is found — a background thread starts monitoring every 2 seconds
4. If monitoring detects dongle removal — application shuts down instantly
5. An AES-256 encryption key is generated from dongle passwords using SHA-256 — this key exists only in memory and is cleared on shutdown

## Project Structure
```
src/
├── config/
│   ├── SecurePasswordConfig.java   # XOR-encoded password management & key generation
│   └── RockeyConfig.java           # Spring Boot config properties binding
├── jni/
│   ├── JRockey4Smart.java          # JNI wrapper for native ROCKEY4 library
│   └── RockeyLibrary.java          # JNA interface to libRockey4Smart.so
└── service/
    └── RockeyService.java          # Core dongle verification & monitoring service
```

## Tech Stack

- Java 11+
- Spring Boot
- JNA (Java Native Access)
- ROCKEY4 Smart USB Dongle (hardware)
- AES-256 Encryption
- SHA-256 Key Derivation

## Setup & Usage

### Prerequisites
- ROCKEY4 Smart USB dongle
- Native library installed: `libRockey4Smart.so` at `/usr/local/lib/`
- Java 11+, Maven

### Configuration
Replace placeholder passwords in `SecurePasswordConfig.java` with your actual XOR-encoded dongle credentials:
```java
private static final int P1_ENCODED = 0x0000; // Replace with your value
private static final int P2_ENCODED = 0x0000; // Replace with your value
private static final int P3_ENCODED = 0x0000; // Replace with your value
private static final int P4_ENCODED = 0x0000; // Replace with your value
```

### Enable Dongle Monitoring in `application.properties`
```properties
rockey.monitoring.enabled=true
rockey.monitoring.interval=2
```

### Activate in Your Service
The `init()` method is annotated with `@PostConstruct` — uncomment it to activate dongle checking on application startup:
```java
@PostConstruct
public void init() { ... }
```

### Run
```bash
mvn spring-boot:run
```

## Security Features

| Feature | Details |
|---|---|
| Password encoding | XOR obfuscation (ProGuard-ready) |
| Encryption key | AES-256, derived via SHA-256, never stored |
| Runtime monitoring | Polls dongle every 2 seconds |
| Shutdown on removal | Immediate `System.exit(1)` |
| Key cleanup | Zeroed from memory on shutdown |

## Use Case

Designed for software deployed **on-site at client locations** where internet-based license validation is not possible or reliable. The physical dongle acts as the license — no dongle, no access.

## Author

Lalit Kiran Nasery — [linkedin.com/in/lalit-nasery-a23b7b267](https://linkedin.com/in/lalit-nasery-a23b7b267)

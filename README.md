# usb-dongle-jar-licensing
Hardware-based licensing for a locally deployed Spring Boot app using ROCKEY4 Smart USB dongle. App validates dongle on startup, monitors every 2 seconds, and shuts down instantly if removed. AES-256 encryption key derived from dongle passwords via SHA-256 — never stored, cleared on shutdown.

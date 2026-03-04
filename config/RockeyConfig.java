/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seiri.backup_restore.config;

/**
 *
 * @author lalit
 */
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rockey")
public class RockeyConfig {

    private Password password = new Password();
    private Hardware hardware = new Hardware();
    private Monitoring monitoring = new Monitoring();

    public static class Password {

        private String p1;
        private String p2;
        private String p3;
        private String p4;

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }

        public String getP2() {
            return p2;
        }

        public void setP2(String p2) {
            this.p2 = p2;
        }

        public String getP3() {
            return p3;
        }

        public void setP3(String p3) {
            this.p3 = p3;
        }

        public String getP4() {
            return p4;
        }

        public void setP4(String p4) {
            this.p4 = p4;
        }

        public short getP1AsShort() {
            return (short) Long.parseLong(p1.replace("0x", ""), 16);
        }

        public short getP2AsShort() {
            return (short) Long.parseLong(p2.replace("0x", ""), 16);
        }

        public short getP3AsShort() {
            return (short) Long.parseLong(p3.replace("0x", ""), 16);
        }

        public short getP4AsShort() {
            return (short) Long.parseLong(p4.replace("0x", ""), 16);
        }
    }

    public static class Hardware {

        private Id id = new Id();

        public static class Id {

            private Check check = new Check();

            public static class Check {

                private boolean enabled = false;

                public boolean isEnabled() {
                    return enabled;
                }

                public void setEnabled(boolean enabled) {
                    this.enabled = enabled;
                }
            }

            public Check getCheck() {
                return check;
            }

            public void setCheck(Check check) {
                this.check = check;
            }
        }

        public Id getId() {
            return id;
        }

        public void setId(Id id) {
            this.id = id;
        }
    }

    public static class Monitoring {

        private boolean enabled;
        private int interval;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public Hardware getHardware() {
        return hardware;
    }

    public void setHardware(Hardware hardware) {
        this.hardware = hardware;
    }

    public Monitoring getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
    }
}


package rip.bolt.nerve.config;

import rip.bolt.nerve.NervePlugin;

public class AppData {

    public static class Redis {

        private static ConfigSection section = NervePlugin.getInstance().getAppConfig().getSection("redis");

        public static boolean isEnabled() {
            return section.getBoolean("enabled", false);
        }

        public static String getHost() {
            return section.getString("host");
        }

        public static String getPassword() {
            return section.getString("password");
        }

        public static int getPort() {
            return section.getInt("port", 6379);
        }

        public static int getReconnectSleep() {
            return section.getInt("reconnect-sleep", 15);
        }

    }

}

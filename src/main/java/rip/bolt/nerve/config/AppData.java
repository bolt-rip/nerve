package rip.bolt.nerve.config;

import rip.bolt.nerve.NervePlugin;

public class AppData {

    public static class API {

        private static ConfigSection section = NervePlugin.getInstance().getAppConfig().getSection("api");

        public static String getURL() {
            return section.getString("url");
        }
        
        public static String getCurrentlyRunningMatchesPath() {
            return section.getString("currently-running-matches");
        }

        public static String getKey() {
            return section.getString("key");
        }

    }

    public static class AutoMove {

        private static ConfigSection section = NervePlugin.getInstance().getAppConfig().getSection("automove");

        public static int getPollDuration() {
            return section.getInt("poll-duration");
        }

    }

    public static class Redis {

        private static ConfigSection section = NervePlugin.getInstance().getAppConfig().getSection("redis");

        public static boolean isEnabled() {
            return section.getBoolean("enabled");
        }

        public static String getHost() {
            return section.getString("host");
        }

        public static int getPort() {
            return section.getInt("port");
        }

    }

    public static class PrivateServerConfig {

        private static ConfigSection section = NervePlugin.getInstance().getAppConfig().getSection("private-server-config");

        public static String getLuckpermsPassword() {
            return section.getString("luckperms-password");
        }

        public static String getLuckpermsAddress() {
            return section.getString("luckperms-address");
        }

        public static String getHelmRepoUrl() {
            return section.getString("helm-repo-url");
        }

    }

}

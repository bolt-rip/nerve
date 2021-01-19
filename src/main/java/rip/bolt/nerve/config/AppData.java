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

}

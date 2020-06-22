package rip.bolt.nerve.config;

import rip.bolt.nerve.NervePlugin;

public class AppData {

    public static class Discord {

        private static ConfigSection section = NervePlugin.getInstance().getAppConfig().getSection("discord");

        public static String getToken() {
            return section.getString("token");
        }

        public static long getChannelId() {
            return section.getLong("channel-id");
        }

    }

    public static class API {

        private static ConfigSection section = NervePlugin.getInstance().getAppConfig().getSection("api");

        public static String getURL() {
            return section.getString("url");
        }

        public static String getKey() {
            return section.getString("key");
        }

    }

    public static String userPunishmentEndpoint = "users/{uuid}/punishments";
    public static String submitUserPunishmentEndpoint = "users/{uuid}/punishments/new";

}

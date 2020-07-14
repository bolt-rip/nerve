package rip.bolt.nerve.utils;

public class ServerUtils {

    public static boolean isPrivateServer(String name) {
        return !name.startsWith("ranked-") && !name.startsWith("lobby-") && !name.equals("fallback_lobby");
    }

    public static boolean isLobbyServer(String name) {
        return name.startsWith("lobby-") || name.equals("fallback_lobby");
    }

}

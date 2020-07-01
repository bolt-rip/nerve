package rip.bolt.nerve.utils;

public class ServerUtils {

    public static boolean isPrivateServer(String name) {
        return !name.startsWith("ranked-") && !name.startsWith("lobby-");
    }

}

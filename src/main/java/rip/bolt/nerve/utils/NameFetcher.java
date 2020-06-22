package rip.bolt.nerve.utils;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

import org.json.JSONArray;

public class NameFetcher implements Runnable {

    private static Map<UUID, String> cache = new HashMap<>();

    public static String getNameFromUUID(UUID uuid) {
        if (cache.containsKey(uuid))
            return cache.get(uuid);

        try {
            URLConnection connection = new URL("https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "").toLowerCase() + "/names").openConnection();

            Scanner scanner = new Scanner(connection.getInputStream());
            String data = scanner.useDelimiter("\\Z").next();
            scanner.close();

            JSONArray array = new JSONArray(data);
            cache.put(uuid, array.getJSONObject(0).getString("name"));

            return cache.get(uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        System.out.println("Clearing cached names...");
        cache.clear();
    }

}
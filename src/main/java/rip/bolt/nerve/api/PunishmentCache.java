package rip.bolt.nerve.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import rip.bolt.nerve.api.definitions.Punishment;

public class PunishmentCache {

    private Map<UUID, List<Punishment>> activePunishments = new HashMap<UUID, List<Punishment>>();
    private Map<UUID, List<Punishment>> allPunishments = new HashMap<UUID, List<Punishment>>();

    public void invalidateCache(UUID uuid) {
        activePunishments.remove(uuid);
        allPunishments.remove(uuid);
    }

    public List<Punishment> cacheActive(UUID uuid, List<Punishment> active) {
        activePunishments.put(uuid, active);

        return active;
    }

    public List<Punishment> cacheAll(UUID uuid, List<Punishment> all) {
        allPunishments.put(uuid, all);

        return all;
    }

    public List<Punishment> getCachedActivePunishments(UUID uuid) {
        return activePunishments.get(uuid);
    }
    
    public List<Punishment> getCachedAllPunishments(UUID uuid) {
        return allPunishments.get(uuid);
    }

}

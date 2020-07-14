package rip.bolt.nerve.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PrivateServerManager {

    private List<UUID> requested = new ArrayList<UUID>();

    public boolean hasRequested(ProxiedPlayer player) {
        return requested.contains(player.getUniqueId());
    }

    public void serverStartup(ProxiedPlayer player) {
        requested.remove(player.getUniqueId());
    }

    public void request(ProxiedPlayer player) {
        requested.add(player.getUniqueId());
    }

}

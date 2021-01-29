package rip.bolt.nerve.listener;

import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.event.RedisMessageEvent;
import rip.bolt.nerve.utils.Messages;

public class QueueListener implements Listener {

    @EventHandler
    public void onPlayerJoinQueue(RedisMessageEvent event) {
        if (!event.getChannel().equals("queue"))
            return;

        JSONObject data = new JSONObject(event.getMessage());
        boolean joined = data.getString("action").equals("JOIN");

        JSONObject user = data.getJSONObject("user");
        if (user == null)
            return;

        UUID userUUID = UUID.fromString(user.getString("uuid"));
        String userUsername = user.getString("username");

        JSONArray players = data.getJSONArray("players");
        if (players == null)
            return;

        int inQueue = players.length();
        int queueSize = data.getInt("limit");

        for (int i = 0; i < inQueue; i++) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(UUID.fromString(players.getString(i)));
            if (target == null)
                continue;

            target.sendMessage(Messages.playerJoinLeaveQueue(userUsername, inQueue, queueSize, joined, target.getUniqueId().equals(userUUID)));
        }
    }

}

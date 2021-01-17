package rip.bolt.nerve.listener;

import java.util.UUID;

import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.event.RedisMessageEvent;

public class RequeueResponseListener implements Listener {

    @EventHandler
    public void onRedisMessage(RedisMessageEvent event) {
        if (!event.getChannel().equals("requeue-response"))
            return;

        JSONObject response = new JSONObject(event.getMessage());
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(response.getString("user")));
        if (player == null)
            return;

        player.sendMessage(TextComponent.fromLegacyText((response.getBoolean("success") ? ChatColor.GREEN : ChatColor.RED) + response.getString("message")));
    }

}

package rip.bolt.nerve.listener;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.api.definitions.QueueUpdate;
import rip.bolt.nerve.api.definitions.QueueUpdate.Action;
import rip.bolt.nerve.event.RedisMessageEvent;
import rip.bolt.nerve.managers.MatchRegistry;
import rip.bolt.nerve.utils.Messages;

public class QueueListener implements Listener {

    private MatchRegistry registry;
    private ObjectMapper objectMapper;

    public QueueListener(MatchRegistry registry, ObjectMapper objectMapper) {
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    @EventHandler
    public void onPlayerJoinQueue(RedisMessageEvent event) {
        if (!event.getChannel().equals("queue"))
            return;

        try {
            QueueUpdate update = objectMapper.readValue(event.getMessage(), QueueUpdate.class);
            ProxiedPlayer proxiedUser = ProxyServer.getInstance().getPlayer(update.getUser().getUniqueId());
            String username = proxiedUser == null ? update.getUser().getUsername() : proxiedUser.getName();

            if (update.getAction() == Action.LEAVE) { // when the match starts & players are moved to their teams, this event may be (incorrectly) fired
                if (registry.getPlayerMatch(update.getUser().getUniqueId()) != null)
                    return;
            }

            int inQueue = update.getPlayers().size();
            int queueSize = update.getLimit();
            BaseComponent[] playerJoinLeaveQueue = Messages.playerJoinLeaveQueue(username, inQueue, queueSize, update.getAction(), false);

            for (UUID uuid : update.getPlayers()) {
                if (uuid == null || uuid.equals(update.getUser().getUniqueId()))
                    continue;

                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);
                if (target != null)
                    target.sendMessage(playerJoinLeaveQueue);
            }

            if (proxiedUser != null)
                proxiedUser.sendMessage(Messages.playerJoinLeaveQueue(username, inQueue, queueSize, update.getAction(), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

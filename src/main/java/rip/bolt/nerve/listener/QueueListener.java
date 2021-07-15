package rip.bolt.nerve.listener;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import net.kyori.adventure.text.TextComponent;
import rip.bolt.nerve.api.definitions.QueueUpdate;
import rip.bolt.nerve.api.definitions.QueueUpdate.Action;
import rip.bolt.nerve.event.RedisMessageEvent;
import rip.bolt.nerve.inject.listener.Listener;
import rip.bolt.nerve.match.MatchRegistry;
import rip.bolt.nerve.utils.Messages;

public class QueueListener implements Listener {

    private ProxyServer server;
    private MatchRegistry registry;
    private ObjectMapper objectMapper;

    @Inject
    public QueueListener(ProxyServer server, MatchRegistry registry, ObjectMapper objectMapper) {
        this.server = server;
        this.registry = registry;
        this.objectMapper = objectMapper;
    }

    @Subscribe
    public void onPlayerJoinQueue(RedisMessageEvent event) {
        if (!event.getChannel().equals("queue"))
            return;

        try {
            QueueUpdate update = objectMapper.readValue(event.getMessage(), QueueUpdate.class);
            Optional<Player> proxiedUser = server.getPlayer(update.getUser().getUniqueId());
            String username = !proxiedUser.isPresent() ? update.getUser().getUsername() : proxiedUser.get().getUsername();

            if (update.getAction() == Action.LEAVE) { // when the match starts & players are moved to their teams, this event may be (incorrectly) fired
                if (registry.getPlayerMatch(update.getUser().getUniqueId()) != null)
                    return;
            }

            int inQueue = update.getPlayers().size();
            int queueSize = update.getLimit();
            TextComponent playerJoinLeaveQueue = Messages.playerJoinLeaveQueue(username, inQueue, queueSize, update.getAction(), false);

            for (UUID uuid : update.getPlayers()) {
                if (uuid == null || uuid.equals(update.getUser().getUniqueId()))
                    continue;

                Optional<Player> target = server.getPlayer(uuid);
                if (target.isPresent())
                    target.get().sendMessage(playerJoinLeaveQueue);
            }

            if (proxiedUser.isPresent())
                proxiedUser.get().sendMessage(Messages.playerJoinLeaveQueue(username, inQueue, queueSize, update.getAction(), true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

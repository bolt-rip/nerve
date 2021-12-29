package rip.bolt.nerve.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent.ServerResult;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.MatchStatus;
import rip.bolt.nerve.inject.listener.Listener;

public class JoinListener implements Listener {

    private ProxyServer server;
    private MatchRegistry registry;
    private List<Player> connectingPlayers;

    @Inject
    public JoinListener(ProxyServer server, MatchRegistry registry) {
        this.server = server;
        this.registry = registry;
        this.connectingPlayers = new ArrayList<Player>();
    }

    @Subscribe
    public void onPlayerLogin(PostLoginEvent event) {
        connectingPlayers.add(event.getPlayer());
    }

    @Subscribe
    public void onSwitchServer(ServerPreConnectEvent event) {
        if (!connectingPlayers.remove(event.getPlayer()))
            return;

        // they just joined
        Match match = registry.getPlayerMatch(event.getPlayer().getUniqueId());
        if (match == null)
            return;

        registry.onPlayerJoin(event.getPlayer(), match);
        if (match.getStatus() == MatchStatus.LOADED || match.getStatus() == MatchStatus.STARTED) {
            Optional<RegisteredServer> assignedServer = server.getServer(match.getServer());
            if (!assignedServer.isPresent())
                return;

            event.setResult(ServerResult.allowed(assignedServer.get()));
        }
    }

}

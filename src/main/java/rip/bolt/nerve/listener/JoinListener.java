package rip.bolt.nerve.listener;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.managers.AutomoveManager;

public class JoinListener implements Listener {

    private AutomoveManager automoveManager;
    private List<ProxiedPlayer> connectingPlayers;

    public JoinListener() {
        this.automoveManager = NervePlugin.getInstance().getAutomoveManager();
        connectingPlayers = new ArrayList<ProxiedPlayer>();
    }

    @EventHandler
    public void onPlayerLogin(PostLoginEvent event) {
        connectingPlayers.add(event.getPlayer());
    }

    @EventHandler
    public void onSwitchServer(ServerConnectEvent event) {
        if (!connectingPlayers.remove(event.getPlayer()))
            return;

        // they just joined
        Match match = automoveManager.getPlayerMatch(event.getPlayer().getUniqueId());
        if (match == null)
            return;

        ServerInfo assignedServer = ProxyServer.getInstance().getServerInfo(match.getServerName());
        if (assignedServer == null)
            return;

        event.setTarget(assignedServer);
    }

}

package rip.bolt.nerve.match.listeners;

import java.util.Optional;

import javax.inject.Inject;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.MatchStatus;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.match.MatchStatusListener;
import rip.bolt.nerve.utils.Messages;
import rip.bolt.nerve.utils.Servers;
import rip.bolt.nerve.utils.Sounds;

public class AutomoveManager implements MatchStatusListener {

    private ProxyServer server;
    private Servers servers;
    private Sounds sounds;

    @Inject
    public AutomoveManager(ProxyServer server, Servers servers, Sounds sounds) {
        this.server = server;
        this.servers = servers;
        this.sounds = sounds;
    }

    @Override
    public void matchStatusUpdate(Match match) {
        if (match.getStatus() != MatchStatus.LOADED)
            return;

        Optional<RegisteredServer> assignedServer = server.getServer(match.getServer());
        if (!assignedServer.isPresent())
            return;

        for (Team team : match.getTeams()) {
            inner: for (User participant : team.getPlayers()) {
                Optional<Player> player = server.getPlayer(participant.getUniqueId());
                if (!player.isPresent())
                    continue inner;

                doLogic(player.get(), assignedServer.get(), match);
            }
        }
    }

    public void doLogic(Player player, RegisteredServer assignedServer, Match match) {
        Optional<ServerConnection> server = player.getCurrentServer();
        if (!server.isPresent() || server.get().getServer().equals(assignedServer)) // no need
            return;

        if (servers.isLobby(server.get().getServerInfo().getName())) {
            player.createConnectionRequest(assignedServer).fireAndForget();
        } else {
            sounds.playDing(player);
            player.sendMessage(Messages.rankedMatchReady(assignedServer.getServerInfo().getName(), match.getMap()));
        }
    }

}

package rip.bolt.nerve.managers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.MatchStatus;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.utils.Messages;
import rip.bolt.nerve.utils.Sounds;

public class AutomoveManager implements MatchStatusListener {

    @Override
    public void matchStatusUpdate(Match match) {
        if (match.getStatus() != MatchStatus.LOADED)
            return;

        ServerInfo assignedServer = ProxyServer.getInstance().getServerInfo(match.getServer());
        for (Team team : match.getTeams()) {
            inner: for (User participant : team.getPlayers()) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(participant.getUniqueId());
                if (player == null)
                    continue inner;

                doLogic(player, assignedServer, match);
            }
        }
    }

    public void doLogic(ProxiedPlayer player, ServerInfo assignedServer, Match match) {
        if (player.getServer().getInfo() == assignedServer) // no need
            return;

        if (NervePlugin.isLobby(player.getServer().getInfo().getName())) {
            player.connect(assignedServer);
        } else {
            Sounds.playDing(player);
            player.sendMessage(Messages.rankedMatchReady(assignedServer.getName(), match.getMap()));
        }
    }

}

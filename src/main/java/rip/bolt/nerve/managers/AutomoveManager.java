package rip.bolt.nerve.managers;

import java.util.HashMap;
import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.User;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.utils.Messages;
import rip.bolt.nerve.utils.Sounds;

public class AutomoveManager {

    private HashMap<String, Match> matches;

    public AutomoveManager() {
        matches = new HashMap<String, Match>();
    }

    public void handleMove(ProxiedPlayer player) {
        Match match = getPlayerMatch(player.getUniqueId());

        if (match != null) {
            ServerInfo assignedServer = ProxyServer.getInstance().getServerInfo(match.getServer());
            if (assignedServer == null)
                return;

            doLogic(player, assignedServer, match);
        }
    }

    public Match getPlayerMatch(UUID uuid) {
        if (matches == null)
            return null;

        for (Match match : matches.values()) {
            for (Team team : match.getTeams()) {
                for (User participant : team.getPlayers()) {
                    if (participant.getUUID().equals(uuid))
                        return match;
                }
            }
        }

        return null;
    }

    public Match getMatchFromServerName(String serverName) {
        if (matches == null)
            return null;

        for (Match match : matches.values()) {
            if (match.getServer().equals(serverName))
                return match;
        }

        return null;
    }

    public HashMap<String, Match> getLatestMatches() {
        return matches;
    }

    public Match addMatch(Match match) {
        ServerInfo assignedServer = ProxyServer.getInstance().getServerInfo(match.getServer());
        for (Team team : match.getTeams()) {
            inner: for (User participant : team.getPlayers()) {
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(participant.getUUID());
                if (player == null)
                    continue inner;

                doLogic(player, assignedServer, match);
            }
        }

        return matches.put(match.getMatchId(), match);
    }

    public Match removeMatch(Match match) { return matches.remove(match.getMatchId()); }

    public void doLogic(ProxiedPlayer player, ServerInfo assignedServer, Match match) {
        if (player.getServer().getInfo() == assignedServer) // no need
            return;

        if (NervePlugin.isLobby(player.getServer().getInfo().getName())) {
            player.connect(assignedServer);
        } else {
            Sounds.playDing(player);
            player.sendMessage(Messages.rankedMatchReady(assignedServer.getName()));
        }
    }

}

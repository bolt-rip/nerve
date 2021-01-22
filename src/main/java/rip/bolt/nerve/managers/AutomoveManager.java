package rip.bolt.nerve.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.Participant;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.config.AppData;

public class AutomoveManager {

    private List<String> previousMatches;
    private List<Match> latestPolledMatches;

    public AutomoveManager() {
        previousMatches = new ArrayList<String>();
        ProxyServer.getInstance().getScheduler().schedule(NervePlugin.getInstance(), new Runnable() {

            @Override
            public void run() {
                latestPolledMatches = NervePlugin.getInstance().getAPIManager().getCurrentlyRunningMatches();
                if (latestPolledMatches == null)
                    return;

                for (Match match : latestPolledMatches) {
                    boolean existedInLastPoll = previousMatches.remove(match.getMatchId()); // whether this match's server was in the list
                    if (existedInLastPoll)
                        continue;

                    // this match has just been put into the LOADED state
                    // so let's notify players that their match is ready

                    ServerInfo assignedServer = ProxyServer.getInstance().getServerInfo(match.getServer());
                    for (Team team : match.getTeams()) {
                        inner: for (Participant participant : team.getPlayers()) {
                            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(participant.getUUID());
                            if (player == null)
                                continue inner;

                            doLogic(player, assignedServer, match);
                        }
                    }

                }

                for (Match match : latestPolledMatches)
                    previousMatches.add(match.getMatchId());
            }

        }, AppData.AutoMove.getPollDuration(), AppData.AutoMove.getPollDuration(), TimeUnit.SECONDS);
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
        if (latestPolledMatches == null)
            return null;

        for (Match match : latestPolledMatches) {
            for (Team team : match.getTeams()) {
                for (Participant participant : team.getPlayers()) {
                    if (participant.getUUID().equals(uuid))
                        return match;
                }
            }
        }

        return null;
    }

    public Match getMatchFromServerName(String serverName) {
        if (latestPolledMatches == null)
            return null;

        for (Match match : latestPolledMatches) {
            if (match.getServer().equals(serverName))
                return match;
        }

        return null;
    }

    public List<Match> getLatestPolledMatches() {
        return latestPolledMatches;
    }

    public void doLogic(ProxiedPlayer player, ServerInfo assignedServer, Match match) {
        if (player.getServer().getInfo() == assignedServer) // no need
            return;

        if (NervePlugin.isLobby(player.getServer().getInfo().getName())) {
            player.connect(assignedServer);
        } else {
            TextComponent info = new TextComponent("Your ranked match on server " + match.getServer() + " is starting soon! ");
            info.setColor(ChatColor.GOLD);

            TextComponent click = new TextComponent("Click here to join.");
            click.setColor(ChatColor.GREEN);
            click.setBold(true);

            TextComponent clickableText = new TextComponent(info, click);
            clickableText.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/server " + assignedServer.getName()));

            player.sendMessage(clickableText);
        }
    }

}

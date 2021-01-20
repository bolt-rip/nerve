package rip.bolt.nerve.listener;

import de.craftmania.dockerizedcraft.server.updater.events.PostAddServerEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.Participant;
import rip.bolt.nerve.api.definitions.Team;
import rip.bolt.nerve.managers.AutomoveManager;

public class ServerAddedListener implements Listener {

    private AutomoveManager automoveManager;

    public ServerAddedListener() {
        this.automoveManager = NervePlugin.getInstance().getAutomoveManager();
    }

    @EventHandler
    public void onPrivateServerAdd(PostAddServerEvent event) {
        ServerInfo serverInfo = event.getServerInfo();
        if (serverInfo.getName().startsWith("ranked-"))
            return;

        ProxiedPlayer requester = ProxyServer.getInstance().getPlayer(serverInfo.getName());

        if (requester != null) {
            if (NervePlugin.isLobby(requester.getServer().getInfo().getName())) {
                requester.connect(serverInfo);
            } else {
                TextComponent info = new TextComponent("Your private server has started up! ");
                info.setColor(ChatColor.GOLD);

                TextComponent click = new TextComponent("Click here to join.");
                click.setColor(ChatColor.GOLD);
                click.setBold(true);

                TextComponent clickableText = new TextComponent(info, click);
                clickableText.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/server " + serverInfo.getName()));

                requester.sendMessage(info);
            }
        }
    }

    @EventHandler
    public void onRankedServerAdd(PostAddServerEvent event) {
        ServerInfo serverInfo = event.getServerInfo();
        if (!serverInfo.getName().startsWith("ranked-"))
            return;

        Match match = automoveManager.getMatchFromServerName(serverInfo.getName());
        if (match != null) {
            ServerInfo assignedServer = ProxyServer.getInstance().getServerInfo(match.getServer());
            if (assignedServer == null)
                return;

            for (Team team : match.getTeams()) {
                for (Participant participant : team.getPlayers()) {
                    ProxiedPlayer player = ProxyServer.getInstance().getPlayer(participant.getUUID());
                    if (player == null)
                        continue;

                    automoveManager.doLogic(player, assignedServer, match);
                }
            }
        } else {
            automoveManager.reportNullMatch(serverInfo.getName()); // move participants once we've got the match
        }
    }

}

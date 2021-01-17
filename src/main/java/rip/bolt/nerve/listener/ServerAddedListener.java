package rip.bolt.nerve.listener;

import com.sk89q.minecraft.util.commands.ChatColor;

import de.craftmania.dockerizedcraft.server.updater.events.PostAddServerEvent;
import net.md_5.bungee.api.ProxyServer;
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
import rip.bolt.nerve.managers.PrivateServerManager;

public class ServerAddedListener implements Listener {

    private PrivateServerManager privateServerManager;
    private AutomoveManager automoveManager;

    public ServerAddedListener(PrivateServerManager manager) {
        this.privateServerManager = manager;
        this.automoveManager = NervePlugin.getInstance().getAutomoveManager();
    }

    @EventHandler
    public void onPrivateServerAdd(PostAddServerEvent event) {
        ServerInfo serverInfo = event.getServerInfo();
        ProxiedPlayer requester = ProxyServer.getInstance().getPlayer(serverInfo.getName());

        if (requester != null) {
            if (NervePlugin.isLobby(requester.getServer().getInfo().getName())) {
                requester.connect(serverInfo);
            } else {
                requester.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Your private server has started up!"));
                requester.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Type /server " + serverInfo.getName() + " to join."));
            }

            privateServerManager.serverStartup(requester);
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

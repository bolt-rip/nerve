package rip.bolt.nerve.listener;

import com.sk89q.minecraft.util.commands.ChatColor;

import de.craftmania.dockerizedcraft.server.updater.events.PostAddServerEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.managers.PrivateServerManager;
import rip.bolt.nerve.utils.ServerUtils;

public class PrivateServerAddedListener implements Listener {

    private PrivateServerManager manager;

    public PrivateServerAddedListener(PrivateServerManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onServerAdd(PostAddServerEvent event) {
        ServerInfo serverInfo = event.getServerInfo();
        ProxiedPlayer requester = ProxyServer.getInstance().getPlayer(serverInfo.getName());

        if (requester != null) {
            if (ServerUtils.isLobbyServer(requester.getServer().getInfo().getName())) {
                requester.connect(serverInfo);
            } else {
                requester.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Your private server has started up!"));
                requester.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Type /server " + serverInfo.getName() + " to join."));
            }

            manager.serverStartup(requester);
        }
    }

}

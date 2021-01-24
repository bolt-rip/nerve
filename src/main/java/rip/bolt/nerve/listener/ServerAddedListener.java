package rip.bolt.nerve.listener;

import de.craftmania.dockerizedcraft.server.updater.events.PostAddServerEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.utils.Messages;

public class ServerAddedListener implements Listener {

    @EventHandler
    public void onPrivateServerAdd(PostAddServerEvent event) {
        ServerInfo serverInfo = event.getServerInfo();
        if (serverInfo.getName().startsWith("ranked-"))
            return;

        ProxiedPlayer requester = ProxyServer.getInstance().getPlayer(serverInfo.getName());
        if (requester != null) {
            if (NervePlugin.isLobby(requester.getServer().getInfo().getName()))
                requester.connect(serverInfo);
            else
                requester.sendMessage(Messages.privateServerStarted(serverInfo.getName()));
        }
    }

}

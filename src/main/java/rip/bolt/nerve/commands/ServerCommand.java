package rip.bolt.nerve.commands;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import rip.bolt.nerve.PrivateServerRequester;
import rip.bolt.nerve.managers.PrivateServerManager;

public class ServerCommand extends Command implements TabExecutor {

    private PrivateServerManager manager;

    public ServerCommand(PrivateServerManager manager) {
        super("server", "bungeecord.command.server");
        this.manager = manager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        if (args.length == 0) {
            if (sender instanceof ProxiedPlayer)
                sender.sendMessage(TextComponent.fromLegacyText(ProxyServer.getInstance().getTranslation("current_server", ((ProxiedPlayer) sender).getServer().getInfo().getName())));

            TextComponent serverList = new TextComponent(ProxyServer.getInstance().getTranslation("server_list"));
            boolean first = true;
            for (ServerInfo server : servers.values()) {
                if (server.canAccess(sender)) {
                    TextComponent serverTextComponent = new TextComponent(ChatColor.GOLD + (first ? server.getName() : ", " + server.getName()));
                    int count = server.getPlayers().size();
                    serverTextComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(count + (count == 1 ? " player" : " players") + "\n").append("Click to connect to the server").italic(true).create()));
                    serverTextComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + server.getName()));
                    serverList.addExtra(serverTextComponent);
                    first = false;
                }
            }
            sender.sendMessage(serverList);
        } else {
            if (!(sender instanceof ProxiedPlayer))
                return;

            ProxiedPlayer player = (ProxiedPlayer) sender;
            ServerInfo server = servers.get(args[0]);

            if (args[0].equalsIgnoreCase(player.getName()) && server == null) { // request server
                if (manager.hasRequested(player)) {
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "You have already requested a private server! Please wait a minute for it to start up."));
                    return;
                }

                player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Requesting private server..."));
                if (!PrivateServerRequester.request(player.getName()))
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occured while requesting your private server!"));
                else
                    manager.request(player);

                return;
            }

            if (server == null)
                player.sendMessage(TextComponent.fromLegacyText(ProxyServer.getInstance().getTranslation("no_server")));
            else if (!server.canAccess(player))
                player.sendMessage(TextComponent.fromLegacyText(ProxyServer.getInstance().getTranslation("no_server_permission")));
            else
                player.connect(server);
        }
    }

    @Override
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
        return (args.length > 1) ? Collections.EMPTY_LIST : Iterables.transform(Iterables.filter(ProxyServer.getInstance().getServers().values(), new Predicate<ServerInfo>() // Waterfall: use #getServersCopy()
        {
            private final String lower = (args.length == 0) ? "" : args[0].toLowerCase(Locale.ROOT);

            @Override
            public boolean apply(ServerInfo input) {
                return input.getName().toLowerCase(Locale.ROOT).startsWith(lower) && input.canAccess(sender);
            }

        }), new Function<ServerInfo, String>() {

            @Override
            public String apply(ServerInfo input) {
                return input.getName();
            }

        });
    }

}
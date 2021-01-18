package rip.bolt.nerve.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import rip.bolt.nerve.PrivateServerRequester;

public class PrivateCommand extends Command {

    public PrivateCommand() {
        super("private");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        boolean senderIsStaff = sender.hasPermission("nerve.staff");
        boolean senderOwnsPrivate = sender.hasPermission("nerve.request");
        boolean senderCanRequest = senderIsStaff || senderOwnsPrivate;
        int maxArgs = senderIsStaff ? 1 : 0;

        if (!senderCanRequest) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You do not have permission to run this command."));
            return;
        }

        if (args.length > maxArgs) {
            if (senderIsStaff)
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/private [player]"));
            else
                sender.sendMessage(TextComponent.fromLegacyText("/private"));

            return;
        }

        if (args.length == 0) {
            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/private <player>"));
                return;
            }

            ProxiedPlayer player = (ProxiedPlayer) sender;
            ServerInfo conflictingServer = ProxyServer.getInstance().getServerInfo(player.getName());

            if (conflictingServer != null) {
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Connecting you to " + conflictingServer.getName() + "..."));
                player.connect(conflictingServer);
                return;
            }

            if (PrivateServerRequester.exists(player)) {
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "You have already requested a private server! Please wait for it to start up."));
                return;
            }

            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Requesting private server..."));
            if (!PrivateServerRequester.request(player.getName()))
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occured while requesting your private server!"));

            return;
        } else if (args.length == 1) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Player not found."));
                return;
            }

            ServerInfo conflictingServer = ProxyServer.getInstance().getServerInfo(target.getName());
            if (conflictingServer != null) {
                target.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Connecting you to " + conflictingServer.getName() + "..."));
                target.connect(conflictingServer);
                return;
            }

            if (PrivateServerRequester.exists(target)) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + target.getName() + " has already requested a private server!"));
                return;
            }

            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "Requesting private server for " + target.getName() + "..."));
            if (!PrivateServerRequester.request(target.getName()))
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occured while requesting a private server!"));

            return;
        }
    }

}
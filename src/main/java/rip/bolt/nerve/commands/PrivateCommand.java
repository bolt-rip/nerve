package rip.bolt.nerve.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.PrivateServerRequester;
import rip.bolt.nerve.utils.Messages;

public class PrivateCommand extends Command {

    public PrivateCommand() {
        super("private");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        NervePlugin.async(() -> {
            boolean senderIsStaff = sender.hasPermission("nerve.staff");
            boolean senderOwnsPrivate = sender.hasPermission("nerve.request");
            boolean senderCanRequest = senderIsStaff || senderOwnsPrivate;
            int maxArgs = senderIsStaff ? 1 : 0;

            if (!senderCanRequest) {
                sender.sendMessage(Messages.noPermsPrivateServer());
                return;
            }

            if (args.length > maxArgs) {
                if (senderIsStaff)
                    sender.sendMessage(Messages.colour(ChatColor.RED, "/private [player]"));
                else
                    sender.sendMessage(Messages.colour(ChatColor.RED, "/private"));

                return;
            }

            if (args.length == 0) {
                if (!(sender instanceof ProxiedPlayer)) {
                    sender.sendMessage(Messages.colour(ChatColor.RED, "/private <player>"));
                    return;
                }

                ProxiedPlayer player = (ProxiedPlayer) sender;
                ServerInfo conflictingServer = ProxyServer.getInstance().getServerInfo(player.getName());

                if (conflictingServer != null) {
                    player.sendMessage(Messages.colour(ChatColor.GOLD, "Connecting you to " + conflictingServer.getName() + "..."));
                    player.connect(conflictingServer);
                    return;
                }

                if (PrivateServerRequester.exists(player)) {
                    player.sendMessage(Messages.colour(ChatColor.GOLD, "You have already requested a private server! Please wait for it to start up."));
                    return;
                }

                player.sendMessage(Messages.colour(ChatColor.GOLD, "Requesting private server..."));
                if (!PrivateServerRequester.request(player.getName()))
                    player.sendMessage(Messages.colour(ChatColor.RED, "An error occured while requesting your private server!"));

                return;
            } else if (args.length == 1) {
                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Messages.colour(ChatColor.RED, "Player not found."));
                    return;
                }

                ServerInfo conflictingServer = ProxyServer.getInstance().getServerInfo(target.getName());
                if (conflictingServer != null) {
                    sender.sendMessage(Messages.colour(ChatColor.RED, target.getName() + "'s private server is already running!"));
                    return;
                }

                if (PrivateServerRequester.exists(target)) {
                    sender.sendMessage(Messages.colour(ChatColor.GOLD, target.getName() + " has already requested a private server!"));
                    return;
                }

                sender.sendMessage(Messages.colour(ChatColor.GOLD, "Requesting private server for " + target.getName() + "..."));
                if (!PrivateServerRequester.request(target.getName()))
                    sender.sendMessage(Messages.colour(ChatColor.RED, "An error occured while requesting a private server!"));

                return;
            }
        });
    }

}
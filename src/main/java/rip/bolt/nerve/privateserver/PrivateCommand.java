package rip.bolt.nerve.privateserver;

import java.util.Optional;

import javax.inject.Inject;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.format.NamedTextColor;
import rip.bolt.nerve.inject.commands.Commands;
import rip.bolt.nerve.utils.Executor;
import rip.bolt.nerve.utils.Messages;

public class PrivateCommand implements Commands {

    private Executor executor;
    private ProxyServer server;
    private PrivateServerRequester requester;

    @Inject
    public PrivateCommand(Executor executor, ProxyServer server, PrivateServerRequester requester) {
        this.executor = executor;
        this.server = server;
        this.requester = requester;
    }

    @Command(aliases = { "private" }, desc = "Request a private server", min = 0, max = 1)
    public void execute(CommandContext args, CommandSource sender) {
        executor.async(() -> {
            boolean senderIsStaff = sender.hasPermission("nerve.staff");
            boolean senderOwnsPrivate = sender.hasPermission("nerve.request");
            boolean senderCanRequest = senderIsStaff || senderOwnsPrivate;
            int maxArgs = senderIsStaff ? 1 : 0;

            if (!senderCanRequest) {
                sender.sendMessage(Messages.noPermsPrivateServer());
                return;
            }

            if (args.argsLength() > maxArgs) {
                if (senderIsStaff)
                    sender.sendMessage(Messages.colour(NamedTextColor.RED, "/private [player]"));
                else
                    sender.sendMessage(Messages.colour(NamedTextColor.RED, "/private"));

                return;
            }

            if (args.argsLength() == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Messages.colour(NamedTextColor.RED, "/private <player>"));
                    return;
                }

                Player player = (Player) sender;
                Optional<RegisteredServer> conflictingServer = server.getServer(player.getUsername());

                if (conflictingServer.isPresent()) {
                    player.sendMessage(Messages.colour(NamedTextColor.GOLD, "Connecting you to " + conflictingServer.get().getServerInfo().getName() + "..."));
                    player.createConnectionRequest(conflictingServer.get()).fireAndForget();
                    return;
                }

                if (requester.exists(player)) {
                    player.sendMessage(Messages.colour(NamedTextColor.GOLD, "You have already requested a private server! Please wait for it to start up."));
                    return;
                }

                player.sendMessage(Messages.colour(NamedTextColor.GOLD, "Requesting private server..."));
                if (!requester.request(player.getUsername()))
                    player.sendMessage(Messages.colour(NamedTextColor.RED, "An error occured while requesting your private server!"));

                return;
            } else if (args.argsLength() == 1) {
                Optional<Player> optionalTarget = server.getPlayer(args.getString(0));
                if (!optionalTarget.isPresent()) {
                    sender.sendMessage(Messages.colour(NamedTextColor.RED, "Player not found."));
                    return;
                }
                Player target = optionalTarget.get();

                Optional<RegisteredServer> conflictingServer = server.getServer(target.getUsername());
                if (conflictingServer.isPresent()) {
                    sender.sendMessage(Messages.colour(NamedTextColor.RED, target.getUsername() + "'s private server is already running!"));
                    return;
                }

                if (requester.exists(target)) {
                    sender.sendMessage(Messages.colour(NamedTextColor.GOLD, target.getUsername() + " has already requested a private server!"));
                    return;
                }

                sender.sendMessage(Messages.colour(NamedTextColor.GOLD, "Requesting private server for " + target.getUsername() + "..."));
                if (!requester.request(target.getUsername()))
                    sender.sendMessage(Messages.colour(NamedTextColor.RED, "An error occured while requesting a private server!"));

                return;
            }
        });
    }

}
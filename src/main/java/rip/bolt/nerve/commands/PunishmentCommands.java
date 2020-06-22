package rip.bolt.nerve.commands;

import java.util.List;

import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Punishment;
import rip.bolt.nerve.api.definitions.PunishmentType;
import rip.bolt.nerve.utils.BanFormatter;
import rip.bolt.nerve.utils.DurationFormatter;
import rip.bolt.nerve.utils.NameUtils;

public class PunishmentCommands {

    @Command(aliases = { "ban", "permban", "pb" }, desc = "Permanently ban a player", usage = "<player> <reason>", min = 2)
    public static void ban(final CommandContext cmd, CommandSender sender) throws CommandException {
        Commands.shouldProxyHandle(sender);

        ProxiedPlayer target = Commands.findPlayer(cmd.getString(0));
        String reason = cmd.getJoinedStrings(1);
        long start = System.currentTimeMillis() / 1000;
        long duration = -1;

        Punishment punishment = new Punishment(target.getUniqueId(), Commands.getSenderUUID(sender), PunishmentType.BAN, reason, start, duration);
        punish(sender, target, punishment);
    }

    @Command(aliases = { "tempban", "tb" }, desc = "Temporarily ban a player", usage = "<player> <time> <reason>", min = 2)
    public static void tempban(final CommandContext cmd, CommandSender sender) throws CommandException {
        Commands.shouldProxyHandle(sender);

        ProxiedPlayer target = Commands.findPlayer(cmd.getString(0));
        long duration = Commands.parseTime(cmd.getString(1));
        String reason = cmd.getJoinedStrings(2);
        long start = System.currentTimeMillis() / 1000;

        Punishment punishment = new Punishment(target.getUniqueId(), Commands.getSenderUUID(sender), PunishmentType.BAN, reason, start, duration);
        punish(sender, target, punishment);
    }

    @Command(aliases = { "kick" }, desc = "Kick a player", usage = "<player> <reason>", min = 2)
    public static void kick(final CommandContext cmd, CommandSender sender) throws CommandException {
        Commands.shouldProxyHandle(sender);

        ProxiedPlayer target = Commands.findPlayer(cmd.getString(0));
        String reason = cmd.getJoinedStrings(1);

        Punishment punishment = new Punishment(target.getUniqueId(), Commands.getSenderUUID(sender), PunishmentType.KICK, reason, System.currentTimeMillis() / 1000, 0);
        punish(sender, target, punishment);
    }

    @Command(aliases = { "warn" }, desc = "Warn a player", usage = "<player> <reason>", min = 2)
    public static void warn(final CommandContext cmd, CommandSender sender) throws CommandException {
        Commands.shouldProxyHandle(sender);

        ProxiedPlayer target = Commands.findPlayer(cmd.getString(0));
        String reason = cmd.getJoinedStrings(1);

        Punishment punishment = new Punishment(target.getUniqueId(), Commands.getSenderUUID(sender), PunishmentType.WARN, reason, System.currentTimeMillis() / 1000, 0);
        punish(sender, target, punishment);
    }

    @Command(aliases = { "punishmenthistory", "ph" }, desc = "View a player's punishment history", usage = "<player>", min = 1)
    public static void ph(final CommandContext cmd, CommandSender sender) throws CommandException {
        Commands.shouldProxyHandle(sender);

        ProxiedPlayer target = Commands.findPlayer(cmd.getString(0));
        sender.sendMessage(new TextComponent(ChatColor.YELLOW + "Looking up punishment history for " + target.getName()));

        List<Punishment> punishments = NervePlugin.getInstance().getAPIManager().getUserPunishments(target.getUniqueId());
        if (punishments.size() == 0)
            sender.sendMessage(new TextComponent(ChatColor.RED + "No punishments found."));

        for (Punishment punishment : punishments) {
            StringBuilder builder = new StringBuilder();

            builder.append(NameUtils.formatName(punishment.getPunisher()));
            builder.append(ChatColor.YELLOW).append(" \u00BB ");

            boolean includeDuration = punishment.getType() == PunishmentType.BAN && punishment.getDuration() != -1;
            if (includeDuration)
                builder.append(ChatColor.RED).append(DurationFormatter.format(punishment.getDuration(), false)).append(" ");

            String type = BanFormatter.calcColouredPunishmentType(punishment.getType(), punishment.getDuration());
            if (includeDuration)
                type = type.toLowerCase();

            builder.append(type);
            builder.append(ChatColor.YELLOW).append(" \u00BB ");
            builder.append(punishment.getReason());

            sender.sendMessage(new TextComponent(builder.toString()));
        }
    }

    public static void punish(CommandSender punisher, ProxiedPlayer target, Punishment punishment) {
        NervePlugin.getInstance().getAPIManager().submitPunishment(punishment);
        if (punishment.getType() != PunishmentType.WARN) {
            StringBuilder builder = new StringBuilder();

            builder.append(NameUtils.formatName(punisher)).append(ChatColor.GOLD).append(" \u00BB ");
            builder.append(BanFormatter.calcPunishmentType(punishment.getType(), punishment.getDuration())).append(" \u00BB ");
            builder.append(NameUtils.formatName(target)).append(ChatColor.GOLD).append(" \u00BB ");
            builder.append(ChatColor.AQUA).append(punishment.getReason());

            TextComponent component = new TextComponent(builder.toString());
            for (ProxiedPlayer player : target.getServer().getInfo().getPlayers())
                player.sendMessage(component);

            target.disconnect(new TextComponent(BanFormatter.getMessage(punishment)));
        } else {
            TextComponent adminChatMessage = new TextComponent(Commands.note("A") + " " + NameUtils.formatName(punisher) + ChatColor.YELLOW + " warned " + NameUtils.formatName(target) + ChatColor.YELLOW + " for " + ChatColor.WHITE + punishment.getReason());

            for (ProxiedPlayer online : target.getServer().getInfo().getPlayers())
                if (online.hasPermission("nerve.staff"))
                    online.sendMessage(adminChatMessage);

            Title title = ProxyServer.getInstance().createTitle();
            title.stay(200);
            title.title(new TextComponent(ChatColor.RED + "WARNING"));
            title.subTitle(new TextComponent(ChatColor.GOLD + punishment.getReason()));
            target.sendTitle(title);
        }
    }

}

package rip.bolt.nerve.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.utils.NameUtils;

public class ReportCommands {

    @Command(aliases = { "report" }, desc = "Report a player for breaking the rules", usage = "<player> <reason>", min = 2)
    public static void report(final CommandContext cmd, CommandSender sender) throws CommandException {
        ProxiedPlayer reporter = Commands.checkIfSenderIsPlayer(sender);
        ProxiedPlayer reported = Commands.findPlayer(cmd.getString(0));
        if (!reporter.getServer().getInfo().equals(reported.getServer().getInfo()))
            throw new CommandException("Player is not on the same server.");

        String reason = cmd.getJoinedStrings(1);
        TextComponent reportMessage = new TextComponent(Commands.note("R") + " " + NameUtils.formatName(reporter) + ChatColor.YELLOW + " reported " + NameUtils.formatName(reported) + ChatColor.YELLOW + " for " + ChatColor.WHITE + reason);
        String discordMessage = "[" + reporter.getServer().getInfo().getName() + "] " + reporter.getName() + " reported " + reported.getName() + " for " + reason;

        for (ProxiedPlayer online : reporter.getServer().getInfo().getPlayers())
            if (online.hasPermission("nerve.staff"))
                online.sendMessage(reportMessage);

        NervePlugin.getInstance().getDiscordManager().sendMessageToPunishments(discordMessage);
        reporter.sendMessage(new TextComponent(ChatColor.GOLD + "Thank you. Your report has been submitted."));
    }

}

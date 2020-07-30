package rip.bolt.nerve.commands;

import java.util.HashMap;
import java.util.Map;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.utils.NameUtils;

public class MessageCommands {

    private static Map<ProxiedPlayer, String> replyTo = new HashMap<ProxiedPlayer, String>();

    @Command(aliases = { "msg", "message", "pm", "dm", "privatemessage", "whisper", "tell", "pgm:msg", "pgm:tell", "pgm:pm", "pgm:dm" }, desc = "Send a private message to a player.", usage = "<player> <message>", min = 2)
    public static void msg(final CommandContext cmd, CommandSender sender) throws CommandException {
        Commands.isFeatureEnabled("private-messages", sender);

        ProxiedPlayer player = Commands.checkIfSenderIsPlayer(sender);
        ProxiedPlayer target = Commands.findPlayer(cmd.getString(0));

        target.sendMessage(new TextComponent(ChatColor.GRAY + "(From " + NameUtils.formatName(player) + ChatColor.GRAY + "): " + ChatColor.RESET + cmd.getJoinedStrings(1)));
        player.sendMessage(new TextComponent(ChatColor.GRAY + "(To " + NameUtils.formatName(target) + ChatColor.GRAY + "): " + ChatColor.RESET + cmd.getJoinedStrings(1)));
        replyTo.put(target, player.getName());
    }

    @Command(aliases = { "reply", "r", "pgm:reply", "pgm:r" }, desc = "Reply to a private message", min = 1)
    public static void reply(final CommandContext cmd, CommandSender sender) throws CommandException {
        Commands.isFeatureEnabled("private-messages", sender);

        ProxiedPlayer player = Commands.checkIfSenderIsPlayer(sender);

        String targetName = replyTo.get(player);
        if (targetName == null)
            throw new CommandException("You have no messages.");
        ProxiedPlayer target = Commands.findPlayer(targetName);

        target.sendMessage(new TextComponent(ChatColor.GRAY + "(From " + NameUtils.formatName(player) + ChatColor.GRAY + "): " + ChatColor.RESET + cmd.getJoinedStrings(0)));
        player.sendMessage(new TextComponent(ChatColor.GRAY + "(To " + NameUtils.formatName(target) + ChatColor.GRAY + "): " + ChatColor.RESET + cmd.getJoinedStrings(0)));
        replyTo.put(target, player.getName());
    }

}

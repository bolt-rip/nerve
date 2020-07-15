package rip.bolt.nerve.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.utils.NameUtils;

public class ChatCommands {

    @Command(aliases = { "s" }, desc = "Send a message to global staff chat.", usage = "<message>", min = 1)
    @CommandPermissions("nerve.staff")
    public static void s(final CommandContext cmd, CommandSender sender) throws CommandException {
        ProxiedPlayer player = Commands.checkIfSenderIsPlayer(sender);
        TextComponent adminChatMessage = new TextComponent(Commands.note("S") + " " + NameUtils.formatName(player) + ChatColor.RESET + ": " + cmd.getJoinedStrings(0));

        for (ProxiedPlayer online : ProxyServer.getInstance().getPlayers())
            if (online.hasPermission("nerve.staff"))
                online.sendMessage(adminChatMessage);
    }

}

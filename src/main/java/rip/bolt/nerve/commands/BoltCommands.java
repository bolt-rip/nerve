package rip.bolt.nerve.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.NestedCommand;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.utils.Components;

public class BoltCommands {

    public static class BoltParentCommand {

        @Command(aliases = { "bolt" }, desc = "Bolt management commands")
        @NestedCommand({ BoltCommands.class })
        public static void bolt(final CommandContext args, CommandSender sender) throws CommandException {

        }

    }

    @Command(aliases = { "veto" }, desc = "Veto a map", min = 1)
    public static void veto(final CommandContext cmd, CommandSender sender) throws CommandException {
        if (!(sender instanceof ProxiedPlayer))
            throw new CommandException("Only players can run this command!");

        String map = Components.toSpace(cmd.getJoinedStrings(0));
        NervePlugin.getInstance().getVetoManager().vetoMap((ProxiedPlayer) sender, map);
    }

}

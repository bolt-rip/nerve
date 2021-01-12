package rip.bolt.nerve.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import rip.bolt.nerve.NervePlugin;

public class RequeueCommand extends Command {

    public RequeueCommand() {
        super("requeue", "nerve.requeue");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer))
            return;

        ProxiedPlayer player = (ProxiedPlayer) sender;
        NervePlugin.getInstance().getRedisManager().sendRedisMessage("requeue", player.getUniqueId().toString());
    }

}
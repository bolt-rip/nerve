package rip.bolt.nerve.commands;

import java.util.UUID;

import com.sk89q.minecraft.util.commands.CommandException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.exception.DoNotHandleCommandException;
import rip.bolt.nerve.exception.PlayerNotFoundException;
import rip.bolt.nerve.utils.ServerUtils;

public class Commands {

    /**
     * Throws a CommandException if console tries to run the command.
     * 
     * @param sender the sender who ran the command
     * @return a ProxiedPlayer object if they're a player
     * @throws CommandException if console tries to run the command
     */
    public static ProxiedPlayer checkIfSenderIsPlayer(CommandSender sender) throws CommandException {
        if (!(sender instanceof ProxiedPlayer))
            throw new CommandException("Only players may use this command!");

        return (ProxiedPlayer) sender;
    }

    /**
     * Finds the player on the bungee server with the specified name.
     * 
     * @param query the player's username
     * @return the player object
     * @throws CommandException if the player is not found
     */
    public static ProxiedPlayer findPlayer(String query) throws CommandException {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(query);
        if (player == null)
            throw new PlayerNotFoundException();

        return player;
    }

    /**
     * Should BungeeCord handle this command, or should it be sent to the Bukkit server.
     * 
     * @param sender the sender running this command
     * @throws CommandException if the command should not be handled by BungeeCord
     */
    public static void shouldProxyHandle(CommandSender sender) throws CommandException {
        if (!(sender instanceof ProxiedPlayer))
            return;

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (ServerUtils.isPrivateServer(player.getServer().getInfo().getName()))
            throw new DoNotHandleCommandException();
    }

    /**
     * Returns the sender's UUID. Console will return 00000000-0000-0000-0000-000000000000.
     * 
     * @param sender the sender's UUID you want
     * @return the UUID
     */
    public static UUID getSenderUUID(CommandSender sender) {
        if (!(sender instanceof ProxiedPlayer))
            return UUID.fromString("00000000-0000-0000-0000-000000000000");

        return ((ProxiedPlayer) sender).getUniqueId();
    }

    /**
     * Converts a string (7d1h5m) to seconds.
     * 
     * @param input the string to be converted
     * @return the time in seconds
     */
    public static long parseTime(String input) throws CommandException {
        input = input.toLowerCase();
        long total = 0;
        String currentValue = "";

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                currentValue += c;
            } else if (Character.isAlphabetic(c)) {
                String unit = c + "";
                if (unit.equals("m")) { // this could be month
                    if (input.length() != i + 1) {
                        char next = input.charAt(i + 1);
                        if (next == 'o') {
                            unit += next;
                            i++;
                        }
                    }
                }

                long timeInSeconds = convert(Integer.parseInt(currentValue), unit);
                if (timeInSeconds == -1)
                    throw new CommandException("Illegal time unit. Valid times units are y, mo, d, h, m, s.");

                total += timeInSeconds;
                currentValue = "";
            }
        }

        return total;
    }

    private static long convert(int value, String unit) {
        switch (unit) {
        case "y":
            return value * 365 * 60 * 60 * 24;
        case "mo":
            return value * 31 * 60 * 60 * 24;
        case "d":
            return value * 60 * 60 * 24;
        case "h":
            return value * 60 * 60;
        case "m":
            return value * 60;
        case "s":
            return value;
        }
        return -1;
    }

    public static String note(String msg) {
        return note(msg, ChatColor.WHITE);
    }

    public static String note(String msg, ChatColor colour) {
        return colour + "[" + ChatColor.YELLOW + msg + colour + "]";
    }

}

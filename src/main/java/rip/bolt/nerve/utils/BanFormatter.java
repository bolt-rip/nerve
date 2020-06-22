package rip.bolt.nerve.utils;

import com.sk89q.minecraft.util.commands.ChatColor;

import rip.bolt.nerve.api.definitions.Punishment;
import rip.bolt.nerve.api.definitions.PunishmentType;

public class BanFormatter {

    public static String getMessage(Punishment punishment) {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.RED + calcPunishmentType(punishment.getType(), punishment.getDuration())).append(ChatColor.GOLD + "  \u00BB  ");
        builder.append(ChatColor.AQUA + punishment.getReason());
        if (punishment.getType() == PunishmentType.BAN && punishment.getDuration() != -1) {
            long timeLeft = punishment.getEndTime() - (System.currentTimeMillis() / 1000);
            builder.append("\n\n");
            builder.append(ChatColor.RED + "Ends in " + DurationFormatter.format(timeLeft));
        }

        return builder.toString();
    }

    public static String calcPunishmentType(PunishmentType type, long duration) {
        switch (type) {
        case BAN:
            return duration == -1 ? "Permanent Ban" : "Ban";
        case KICK:
            return "Kick";
        case WARN:
            return "Warn";
        default:
            return "Unknown punishment type " + type.toString();
        }
    }

    public static String calcColouredPunishmentType(PunishmentType type, long duration) {
        switch (type) {
        case BAN:
            return ChatColor.RED + (duration == -1 ? "Permanent Ban" : "Ban");
        case KICK:
            return ChatColor.YELLOW + "Kick";
        case WARN:
            return ChatColor.GREEN + "Warn";
        default:
            return "Unknown punishment type " + type.toString();
        }
    }

}

package rip.bolt.nerve.managers;

import java.awt.Color;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.ChatColor;
import rip.bolt.nerve.config.AppData;

public class DiscordManager {

    protected JDA jda;
    protected TextChannel punishmentsChannel;

    public DiscordManager() {
        try {
            JDA jda = new JDABuilder(AccountType.BOT).setToken(AppData.Discord.getToken()).build();
            jda.awaitReady();

            punishmentsChannel = jda.getTextChannelById(AppData.Discord.getChannelId());
            if (punishmentsChannel == null)
                System.out.println("[Nerve] Invalid channel id!");
            else
                System.out.println("[Nerve] Found #punishments.");
        } catch (Exception e) {
            System.out.println("[Nerve] Failed to login!");
            e.printStackTrace();
        }
    }

    public void sendReport(String server, String reporter, String reported, String reason, String staffOnline, int numStaffOnline) {
        if (punishmentsChannel == null)
            return;

        EmbedBuilder reportEmbed = new EmbedBuilder();
        reportEmbed.setTitle("Player report");
        reportEmbed.setColor(Color.ORANGE);

        reportEmbed.setDescription(reporter + " reported " + reported);
        reportEmbed.addField("Server", server, false);
        reportEmbed.addField("Reason", reason, false);

        reportEmbed.setFooter("Staff online (" + numStaffOnline + "): " + staffOnline);

        punishmentsChannel.sendMessage(reportEmbed.build()).queue();
    }

    public void sendMessageToPunishments(String message) {
        if (punishmentsChannel == null)
            return;

        punishmentsChannel.sendMessage(ChatColor.stripColor(message)).queue();
    }

}

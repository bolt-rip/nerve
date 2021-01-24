package rip.bolt.nerve.utils;

import static rip.bolt.nerve.utils.Components.command;

import com.sk89q.minecraft.util.commands.ChatColor;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Messages {

    public static BaseComponent[] privateServerStarted(String server) {
        return new BaseComponent[] { new TextComponent(ChatColor.GOLD + "Your private server has started up! Run "), command(ChatColor.GOLD.toString(), "server", server), new TextComponent(ChatColor.GOLD + " to connect.") };
    }

    public static BaseComponent[] rankedMatchReady(String server) {
        return new BaseComponent[] { new TextComponent(ChatColor.GOLD + "Your ranked match on server " + server + " is starting soon! Run "), command(ChatColor.GREEN.toString(), "server", server), new TextComponent(ChatColor.GOLD + " to connect.") };
    }

}

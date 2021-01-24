package rip.bolt.nerve.utils;

import com.sk89q.minecraft.util.commands.ChatColor;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Components {

    public static BaseComponent command(String style, String command, String... args) {
        if (!command.startsWith("/"))
            command = "/" + command;
        for (String arg : args)
            command += " " + Components.toArgument(arg);

        TextComponent component = new TextComponent(style + command);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.GREEN + "Click to run " + style + command)));

        return component;
    }

    static String toArgument(String input) {
        if (input == null)
            return null;
        return input.replace(" ", "┈");
    }

}

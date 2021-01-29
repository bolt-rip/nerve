package rip.bolt.nerve.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Components {

    public static BaseComponent command(ChatColor colour, String command, String... args) {
        if (!command.startsWith("/"))
            command = "/" + command;
        for (String arg : args)
            command += " " + Components.toArgument(arg);

        TextComponent component = new TextComponent(command);
        component.setColor(colour);
        component.setUnderlined(true);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] { Messages.colour(ChatColor.GREEN, "Click to run "), Messages.colour(colour, command) }));

        return component;
    }

    static String toArgument(String input) {
        if (input == null)
            return null;
        return input.replace(" ", "┈");
    }

}

package rip.bolt.nerve.utils;

import static net.kyori.adventure.text.Component.text;
import static rip.bolt.nerve.utils.Messages.colour;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class Components {

    public static TextComponent command(NamedTextColor colour, String command, String... args) {
        if (!command.startsWith("/"))
            command = "/" + command;
        for (String arg : args)
            command += " " + Components.toArgument(arg);

        TextComponent component = text(command);
        return command(colour, component, command);
    }

    public static TextComponent command(NamedTextColor colour, TextComponent displayed, String command, String... args) {
        if (!command.startsWith("/"))
            command = "/" + command;
        for (String arg : args)
            command += " " + Components.toArgument(arg);

        displayed = displayed.color(colour);
        displayed = displayed.decoration(TextDecoration.UNDERLINED, true);
        displayed = displayed.clickEvent(ClickEvent.runCommand(command));

        displayed = displayed.hoverEvent(HoverEvent.showText(text().append(colour(NamedTextColor.GREEN, "Click to run ")).append(colour(colour, command)).build()));

        return displayed;
    }

    public static String toArgument(String input) {
        if (input == null)
            return null;
        return input.replace(" ", "┈");
    }

    public static String toSpace(String input) {
        if (input == null)
            return null;
        return input.replace("┈", " ");
    }

}

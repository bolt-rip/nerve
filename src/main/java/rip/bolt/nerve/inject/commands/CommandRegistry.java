package rip.bolt.nerve.inject.commands;

import javax.inject.Inject;

import com.sk89q.bungee.util.CommandExecutor;
import com.sk89q.bungee.util.CommandRegistration;
import com.sk89q.bungee.util.VelocityCommandsManager;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import rip.bolt.nerve.inject.GuiceInjector;

public class CommandRegistry {

    private VelocityCommandsManager commands;
    private CommandRegistration cmdRegister;

    @Inject
    public CommandRegistry(GuiceInjector injector, CommandManager commandManager) {
        this.commands = new VelocityCommandsManager();
        this.commands.setInjector(injector);

        CommandExecutor<CommandSource> executor = new CommandExecutor<CommandSource>() {

            @Override
            public void onCommand(CommandSource sender, String cmd, String[] args) {
                try {
                    commands.execute(cmd, args, sender, sender);
                } catch (CommandPermissionsException e) {
                    sender.sendMessage(Component.text("You do not have permission to run this command!").color(NamedTextColor.RED));
                } catch (MissingNestedCommandException e) {
                    sender.sendMessage(Component.text(e.getUsage().replace("{cmd}", cmd)).color(NamedTextColor.RED));
                } catch (CommandUsageException e) {
                    sender.sendMessage(Component.text(e.getMessage()).color(NamedTextColor.RED));
                    sender.sendMessage(Component.text(e.getUsage()).color(NamedTextColor.RED));
                } catch (WrappedCommandException e) {
                    if (e.getCause() instanceof NumberFormatException) {
                        sender.sendMessage(Component.text("Number expected. String received instead").color(NamedTextColor.RED));
                    } else {
                        sender.sendMessage(Component.text("An unknown error has occured. Please check the console.").color(NamedTextColor.RED));
                        e.printStackTrace();
                    }
                } catch (CommandException e) {
                    sender.sendMessage(Component.text(e.getMessage()).color(NamedTextColor.RED));
                }
            }

        };

        cmdRegister = new CommandRegistration(this, commandManager, this.commands, executor);
    }

    public void register(Class<?> clazz) {
        cmdRegister.register(clazz);
    }

}

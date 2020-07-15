package rip.bolt.nerve;

import java.util.concurrent.TimeUnit;

import com.sk89q.bungee.util.BungeeCommandsManager;
import com.sk89q.bungee.util.CommandExecutor;
import com.sk89q.bungee.util.CommandRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import rip.bolt.nerve.api.APIManager;
import rip.bolt.nerve.api.PunishmentCache;
import rip.bolt.nerve.commands.ChatCommands;
import rip.bolt.nerve.commands.MessageCommands;
import rip.bolt.nerve.commands.PunishmentCommands;
import rip.bolt.nerve.commands.ReportCommands;
import rip.bolt.nerve.commands.ServerCommand;
import rip.bolt.nerve.config.Config;
import rip.bolt.nerve.config.ConfigManager;
import rip.bolt.nerve.exception.DoNotHandleCommandException;
import rip.bolt.nerve.exception.PlayerNotFoundException;
import rip.bolt.nerve.listener.JoinListener;
import rip.bolt.nerve.listener.PrivateServerAddedListener;
import rip.bolt.nerve.managers.DiscordManager;
import rip.bolt.nerve.managers.PrivateServerManager;
import rip.bolt.nerve.utils.NameFetcher;

public class NervePlugin extends Plugin {

    protected BungeeCommandsManager commands;
    protected CommandRegistration cmdRegister;

    protected PunishmentCache punishmentCache;
    protected APIManager apiManager;
    protected DiscordManager discordManager;

    protected Config appConfig;

    private static NervePlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        appConfig = new ConfigManager(this, "config").get();
        new ConfigManager(this, "template"); // copy template.yml from jar into plugins/Nerve/template.yml

        punishmentCache = new PunishmentCache();
        apiManager = new APIManager();
        discordManager = new DiscordManager();

        PrivateServerManager privateServerManager = new PrivateServerManager();

        ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PrivateServerAddedListener(privateServerManager));
        ProxyServer.getInstance().getScheduler().schedule(this, new NameFetcher(), 1, TimeUnit.DAYS);

        setupCommands();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new ServerCommand(privateServerManager));

        System.out.println("[Nerve] Nerve is now enabled!");
    }

    @Override
    public void onDisable() {
        System.out.println("[Nerve] Nerve is now disabled!");

        instance = null;
    }

    private void setupCommands() {
        this.commands = new BungeeCommandsManager();
        CommandExecutor<CommandSender> executor = new CommandExecutor<CommandSender>() {

            public void onCommand(CommandSender sender, String cmd, String[] args) {
                try {
                    commands.execute(cmd, args, sender, sender);
                } catch (DoNotHandleCommandException e) {
                    // send command to bukkit server
                    ((ProxiedPlayer) sender).chat("/" + cmd + " " + String.join(" ", args));
                } catch (CommandPermissionsException e) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to run this command!"));
                } catch (MissingNestedCommandException e) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + e.getUsage().replace("{cmd}", cmd)));
                } catch (PlayerNotFoundException e) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "No players matched query."));
                } catch (CommandUsageException e) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + e.getMessage()));
                    sender.sendMessage(new TextComponent(ChatColor.RED + e.getUsage()));
                } catch (WrappedCommandException e) {
                    if (e.getCause() instanceof NumberFormatException) {
                        sender.sendMessage(new TextComponent(ChatColor.RED + "Number expected. String received instead"));
                    } else {
                        sender.sendMessage(new TextComponent(ChatColor.RED + "An unknown error has occured. Please check the console."));
                        e.printStackTrace();
                    }
                } catch (CommandException e) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + e.getMessage()));
                }
            }
        };

        cmdRegister = new CommandRegistration(this, ProxyServer.getInstance().getPluginManager(), this.commands, executor);
        cmdRegister.register(MessageCommands.class);
        cmdRegister.register(PunishmentCommands.class);
        cmdRegister.register(ReportCommands.class);
        cmdRegister.register(ChatCommands.class);
    }

    public Config getAppConfig() {
        return appConfig;
    }

    public PunishmentCache getPunishmentCache() {
        return punishmentCache;
    }

    public APIManager getAPIManager() {
        return apiManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public static NervePlugin getInstance() {
        return instance;
    }

}

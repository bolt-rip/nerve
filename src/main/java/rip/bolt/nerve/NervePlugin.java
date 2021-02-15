package rip.bolt.nerve;

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
import net.md_5.bungee.api.plugin.Plugin;
import rip.bolt.nerve.api.APIManager;
import rip.bolt.nerve.commands.BoltCommands;
import rip.bolt.nerve.commands.PrivateCommand;
import rip.bolt.nerve.config.Config;
import rip.bolt.nerve.config.ConfigManager;
import rip.bolt.nerve.listener.JoinListener;
import rip.bolt.nerve.listener.MatchUpdateListener;
import rip.bolt.nerve.listener.QueueListener;
import rip.bolt.nerve.listener.ServerAddedListener;
import rip.bolt.nerve.managers.AutomoveManager;
import rip.bolt.nerve.managers.MatchRegistry;
import rip.bolt.nerve.managers.VetoManager;
import rip.bolt.nerve.redis.RedisManager;

public class NervePlugin extends Plugin {

    protected BungeeCommandsManager commands;
    protected CommandRegistration cmdRegister;

    protected APIManager apiManager;
    protected RedisManager redisManager;

    protected MatchRegistry matchRegistry;
    protected AutomoveManager automoveManager;
    protected VetoManager vetoManager;

    protected Config appConfig;

    private static NervePlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        appConfig = new ConfigManager(this, "config").get();
        new ConfigManager(this, "template"); // copy template.yml from jar into plugins/Nerve/template.yml

        apiManager = new APIManager();
        redisManager = new RedisManager();

        matchRegistry = new MatchRegistry();
        automoveManager = new AutomoveManager();
        vetoManager = new VetoManager(apiManager);

        matchRegistry.registerListener(automoveManager);
        matchRegistry.registerListener(vetoManager);

        ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerAddedListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new MatchUpdateListener(matchRegistry));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinListener(matchRegistry));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new QueueListener(matchRegistry));

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PrivateCommand());
        setupCommands();

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
                } catch (CommandPermissionsException e) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "You do not have permission to run this command!"));
                } catch (MissingNestedCommandException e) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + e.getUsage().replace("{cmd}", cmd)));
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
        cmdRegister.register(BoltCommands.BoltParentCommand.class);
    }

    public Config getAppConfig() {
        return appConfig;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public MatchRegistry getMatchRegistry() {
        return matchRegistry;
    }

    public AutomoveManager getAutomoveManager() {
        return automoveManager;
    }

    public VetoManager getVetoManager() {
        return vetoManager;
    }

    public static NervePlugin getInstance() {
        return instance;
    }

    public static boolean isLobby(String name) {
        return name.equals("lobby") || name.equals("occ-lobby");
    }

    public static void async(Runnable runnable) {
        ProxyServer.getInstance().getScheduler().runAsync(getInstance(), runnable);
    }

}

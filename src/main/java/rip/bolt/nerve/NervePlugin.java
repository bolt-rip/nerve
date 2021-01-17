package rip.bolt.nerve;

import com.sk89q.bungee.util.BungeeCommandsManager;
import com.sk89q.bungee.util.CommandRegistration;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import rip.bolt.nerve.api.APIManager;
import rip.bolt.nerve.commands.RequeueCommand;
import rip.bolt.nerve.commands.PrivateCommand;
import rip.bolt.nerve.config.Config;
import rip.bolt.nerve.config.ConfigManager;
import rip.bolt.nerve.listener.JoinListener;
import rip.bolt.nerve.listener.RequeueResponseListener;
import rip.bolt.nerve.listener.ServerAddedListener;
import rip.bolt.nerve.managers.AutomoveManager;
import rip.bolt.nerve.managers.PrivateServerManager;
import rip.bolt.nerve.managers.RedisManager;

public class NervePlugin extends Plugin {

    protected BungeeCommandsManager commands;
    protected CommandRegistration cmdRegister;

    protected APIManager apiManager;
    protected AutomoveManager automoveManager;
    protected RedisManager redisManager;

    protected Config appConfig;

    private static NervePlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        appConfig = new ConfigManager(this, "config").get();
        new ConfigManager(this, "template"); // copy template.yml from jar into plugins/Nerve/template.yml

        apiManager = new APIManager();
        automoveManager = new AutomoveManager();
        redisManager = new RedisManager();
        PrivateServerManager privateServerManager = new PrivateServerManager();

        ProxyServer.getInstance().getPluginManager().registerListener(this, new ServerAddedListener(privateServerManager));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new RequeueResponseListener());

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new RequeueCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PrivateCommand(privateServerManager));

        System.out.println("[Nerve] Nerve is now enabled!");
    }

    @Override
    public void onDisable() {
        System.out.println("[Nerve] Nerve is now disabled!");

        instance = null;
    }

    public Config getAppConfig() {
        return appConfig;
    }

    public APIManager getAPIManager() {
        return apiManager;
    }

    public AutomoveManager getAutomoveManager() {
        return automoveManager;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }

    public static NervePlugin getInstance() {
        return instance;
    }

    public static boolean isLobby(String name) {
        return name.equals("lobby") || name.equals("occ-lobby");
    }

}

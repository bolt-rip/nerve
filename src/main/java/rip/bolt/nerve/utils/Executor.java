package rip.bolt.nerve.utils;

import javax.inject.Inject;

import com.velocitypowered.api.proxy.ProxyServer;

import rip.bolt.nerve.NervePlugin;

public class Executor {

    private ProxyServer server;
    private NervePlugin plugin;

    @Inject
    public Executor(ProxyServer server, NervePlugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    public void async(Runnable runnable) {
        server.getScheduler().buildTask(plugin, runnable).schedule();
    }

}

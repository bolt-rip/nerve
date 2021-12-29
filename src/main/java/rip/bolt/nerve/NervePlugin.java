package rip.bolt.nerve;

import java.nio.file.Path;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.name.Named;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import rip.bolt.nerve.api.APIModule;
import rip.bolt.nerve.commands.BoltCommands;
import rip.bolt.nerve.config.Config;
import rip.bolt.nerve.config.ConfigManager;
import rip.bolt.nerve.gson.GsonModule;
import rip.bolt.nerve.inject.FacetBinder;
import rip.bolt.nerve.inject.FacetContext;
import rip.bolt.nerve.listener.QueueListener;
import rip.bolt.nerve.match.MatchModule;
import rip.bolt.nerve.match.listeners.AutomoveManager;
import rip.bolt.nerve.match.listeners.TeamInformationManager;
import rip.bolt.nerve.match.listeners.VetoManager;
import rip.bolt.nerve.privateserver.PrivateServerModule;
import rip.bolt.nerve.protocol.ProtocolModule;
import rip.bolt.nerve.redis.RedisModule;
import rip.bolt.nerve.utils.Messages;
import rip.bolt.nerve.utils.Sounds;

@Plugin(id = "nerve", name = "Nerve", version = "2.0.0-SNAPSHOT", authors = { "dentmaged" }, dependencies = @Dependency(id = "snap", optional = true))
public class NervePlugin extends AbstractModule {

    private ProxyServer server;
    private Logger logger;
    private Path dataFolder;

    @Inject
    public NervePlugin(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    @Subscribe(order = PostOrder.LAST)
    public void onProxyInitialise(ProxyInitializeEvent event) {
        Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {

            @Override
            public void configure() {
                bind(ProxyServer.class).toInstance(server);
                bind(PluginManager.class).toInstance(server.getPluginManager());
                bind(EventManager.class).toInstance(server.getEventManager());
                bind(CommandManager.class).toInstance(server.getCommandManager());
                bind(Logger.class).toInstance(logger);
                bind(Path.class).annotatedWith(DataDirectory.class).toInstance(dataFolder);
            }

        }, this);
    }

    @Override
    public void configure() {
        bind(NervePlugin.class).toInstance(this);

        install(new GsonModule());

        install(new APIModule());
        install(new RedisModule());

        install(new MatchModule());
        install(new ProtocolModule());
        install(new PrivateServerModule());

        FacetBinder facetBinder = new FacetBinder(binder());
        facetBinder.register(BoltCommands.BoltParentCommand.class);
        facetBinder.register(QueueListener.class);
        facetBinder.register(AutomoveManager.class);
        facetBinder.register(TeamInformationManager.class);
        facetBinder.register(VetoManager.class);

        binder().requestStaticInjection(Messages.class);
        bind(Sounds.class).in(Singleton.class);

        bind(FacetContext.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    Config config(@DataDirectory Path dataFolder) {
        return new ConfigManager(dataFolder.toFile(), "config").get();
    }

    @Provides
    @Singleton
    @Named("template")
    Config template(@DataDirectory Path dataFolder) {
        return new ConfigManager(dataFolder.toFile(), "template").get();
    }

}

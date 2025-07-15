package rip.bolt.nerve.privateserver;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import rip.bolt.nerve.utils.Messages;
import rip.bolt.nerve.utils.Servers;
import rip.bolt.nerve.utils.Sounds;
import rip.bolt.nerve.utils.reflection.Reflect;

public class ServerAddedListener {

    private ProxyServer server;
    private Logger logger;
    private Servers servers;
    private Sounds sounds;

    private Field serverInfoField;
    private Field serverField;

    @Inject
    public ServerAddedListener(ProxyServer server, Logger logger, Servers servers, Sounds sounds) {
        this.server = server;
        this.logger = logger;
        this.servers = servers;
        this.sounds = sounds;

        inject();
    }

    private void inject() {
        // DockerizedCraft is loaded by Snap. Snap acts as a sandbox, loading BungeeCord
        // plugins manually. BungeeCord and Velocity's event buses are separate. In order
        // to get our PostAddServerEvent, we have to register our own BungeeCord event
        // listener. However, since we don't depend on BungeeCord, we can't use @EventHandler
        // annotation. Therefore, we have to manually inject our listener into the EventBus.

        try {
            Object pluginManager = getPluginManager();
            if (pluginManager == null) {
                logger.warn("Unable to find BungeeCord plugin manager - assuming DockerizedCraft (via Snap) isn't loaded!");
                logger.warn("Private server startup notifications will not work!");
                return;
            }

            Object eventBus = Reflect.target(pluginManager.getClass()).findField("eventBus").setAccessible(true).get(pluginManager);
            Object dockerizedCraftPlugin = Reflect.target(pluginManager.getClass()).findMethod("getPlugin", String.class).invoke(pluginManager, "DockerizedCraft");
            ClassLoader classLoader = dockerizedCraftPlugin.getClass().getClassLoader();

            Class<?> postAddServerEvent = Class.forName("de.craftmania.dockerizedcraft.server.updater.events.PostAddServerEvent", true, classLoader);
            this.serverInfoField = postAddServerEvent.getDeclaredField("serverInfo");
            this.serverInfoField.setAccessible(true);

            Method onPostServerAdd = Reflect.target(getClass()).findMethod("onPostServerAdd", Object.class).setAccessible(true).getMethod();
            registerEventHandler(eventBus, postAddServerEvent, this, onPostServerAdd, (byte) 0); // 0 is normal priority
        } catch (ClassNotFoundException e) {
            logger.warn("Unable to find DockerizedCraft!");
            logger.warn("Private server startup notifications will not work!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getPluginManager() {
        try {
            Class<?> proxy = Class.forName("net.md_5.bungee.api.ProxyServer");
            if (proxy == null)
                return null;

            Object snapProxy = Reflect.target(proxy).findMethod("getInstance").invoke(null);
            Object pluginManager = Reflect.target(proxy).findMethod("getPluginManager").invoke(snapProxy);

            return pluginManager;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static void registerEventHandler(Object eventBus, Class<?> event, Object listener, Method handler, byte priority) {
        ReentrantLock lock = (ReentrantLock) Reflect.target(eventBus.getClass()).findField("lock").setAccessible(true).get(eventBus);
        lock.lock();

        try {
            Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority = (Map<Class<?>, Map<Byte, Map<Object, Method[]>>>) Reflect.target(eventBus.getClass()).findField("byListenerAndPriority").setAccessible(true).get(eventBus);
            Map<Byte, Map<Object, Method[]>> priorities = byListenerAndPriority.get(event);
            if (priorities == null) {
                priorities = new HashMap<Byte, Map<Object, Method[]>>();
                byListenerAndPriority.put(event, priorities);
            }

            Map<Object, Method[]> currentPriorityMap = priorities.get(priority);
            if (currentPriorityMap == null) {
                currentPriorityMap = new HashMap<Object, Method[]>();
                priorities.put(priority, currentPriorityMap);
            }
            currentPriorityMap.put(listener, new Method[] { handler });

            Reflect.target(eventBus.getClass()).findMethod("bakeHandlers", Class.class).setAccessible(true).invoke(eventBus, event);
        } finally {
            lock.unlock();
        }
    }

    protected void onPostServerAdd(Object event) {
        try {
            // SnapServerInfo stores the Velocity RegisteredServer
            Object snapServerInfo = serverInfoField.get(event);
            if (serverField == null) {
                serverField = snapServerInfo.getClass().getDeclaredField("server");
                serverField.setAccessible(true);
            }

            RegisteredServer registeredServer = (RegisteredServer) serverField.get(snapServerInfo);
            if (registeredServer.getServerInfo().getName().startsWith("bolt-"))
                return;

            Optional<Player> requester = server.getPlayer(registeredServer.getServerInfo().getName());
            if (requester.isPresent()) {
                Optional<ServerConnection> currentServer = requester.get().getCurrentServer();
                if (!currentServer.isPresent() || servers.isLobby(currentServer.get().getServerInfo().getName())) {
                    requester.get().createConnectionRequest(registeredServer).fireAndForget();
                } else {
                    sounds.playDing(requester.get());
                    requester.get().sendMessage(Messages.privateServerStarted(registeredServer.getServerInfo().getName()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

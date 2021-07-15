package rip.bolt.nerve.protocol;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.google.common.eventbus.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.connection.client.ClientPlaySessionHandler;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;

import rip.bolt.nerve.inject.listener.Listener;

public class PacketHandlerTracker implements Listener {

    private Map<MinecraftSessionHandler, PlayerPacketHandler> sessionHandlerMap;
    private Map<Player, PlayerPacketHandler> playerMap;

    private Field playerField;

    public PacketHandlerTracker() {
        this.sessionHandlerMap = new HashMap<MinecraftSessionHandler, PlayerPacketHandler>();
        this.playerMap = new HashMap<Player, PlayerPacketHandler>();

        try {
            this.playerField = ClientPlaySessionHandler.class.getDeclaredField("player");
            this.playerField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PlayerPacketHandler get(MinecraftSessionHandler velocityHandler) {
        PlayerPacketHandler handler = sessionHandlerMap.get(velocityHandler);
        if (handler == null) {
            try {
                ConnectedPlayer player = (ConnectedPlayer) playerField.get(velocityHandler);
                handler = new PlayerPacketHandler(player);
                sessionHandlerMap.put(velocityHandler, handler);
                playerMap.put(player, handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return handler;
    }

    public PlayerPacketHandler get(Player player) {
        return playerMap.get(player);
    }

    @Subscribe
    public void onPlayerSwitchServer(ServerPreConnectEvent event) { // fired before a connection is established with the new server
        PlayerPacketHandler handler = playerMap.remove(event.getPlayer());
        sessionHandlerMap.values().remove(handler);
    }

}

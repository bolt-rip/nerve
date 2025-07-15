package rip.bolt.nerve.utils;

import javax.inject.Inject;

import com.velocitypowered.api.proxy.Player;

import rip.bolt.nerve.protocol.PacketHandlerTracker;
import rip.bolt.nerve.protocol.PlayerPacketHandler;

public class Sounds {

    @Inject
    private PacketHandlerTracker tracker;

    public void playDing(Player player) {
        PlayerPacketHandler handler = tracker.get(player);
        if (handler != null)
            handler.playDing(1, 1.2f);
    }

}

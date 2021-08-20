package rip.bolt.nerve.utils;

import javax.inject.Inject;

import com.velocitypowered.api.proxy.Player;

import rip.bolt.nerve.protocol.PacketHandlerTracker;

public class Sounds {

    @Inject private PacketHandlerTracker tracker;

    public void playDing(Player player) {
        tracker.get(player).playDing(1, 1.2f);
    }

}

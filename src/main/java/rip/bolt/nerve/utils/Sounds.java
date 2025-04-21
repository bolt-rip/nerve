package rip.bolt.nerve.utils;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class Sounds {

    static final String DING_LEGACY = "random.orb";
    static final String DING_MODERN = "entity.experience_orb.pickup";

    public void playDing(Player player) {
        if (player.getProtocolVersion().compareTo(ProtocolVersion.MINECRAFT_1_9) < 0)
            player.playSound(Sound.sound(Key.key(DING_LEGACY), Sound.Source.MASTER, 1, 1.2f));
        else
            player.playSound(Sound.sound(Key.key(DING_MODERN), Sound.Source.MASTER, 1, 1.2f));
    }

}

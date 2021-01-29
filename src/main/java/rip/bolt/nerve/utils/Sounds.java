package rip.bolt.nerve.utils;

import de.exceptionflug.protocolize.world.Sound;
import de.exceptionflug.protocolize.world.SoundCategory;
import de.exceptionflug.protocolize.world.WorldModule;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Sounds {

    public static void playDing(ProxiedPlayer player) {
        WorldModule.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 1, 1.2f);
    }

}

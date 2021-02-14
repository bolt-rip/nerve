package rip.bolt.nerve.managers;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import rip.bolt.nerve.api.definitions.Match;

public interface MatchStatusListener {

    public default void playerJoin(ProxiedPlayer player, Match match) {

    }

    public default void matchStatusUpdate(Match match) {

    }

}

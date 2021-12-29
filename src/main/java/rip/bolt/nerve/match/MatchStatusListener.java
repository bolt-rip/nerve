package rip.bolt.nerve.match;

import com.velocitypowered.api.proxy.Player;

import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.inject.Facet;

public interface MatchStatusListener extends Facet {

    public default void playerJoin(Player player, Match match) {

    }

    public default void matchStatusUpdate(Match match) {

    }

}

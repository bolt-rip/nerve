package rip.bolt.nerve.listener;

import java.util.List;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.api.definitions.Punishment;
import rip.bolt.nerve.api.definitions.PunishmentType;
import rip.bolt.nerve.utils.BanFormatter;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PostLoginEvent event) {
        List<Punishment> punishments = NervePlugin.getInstance().getAPIManager().getActiveUserPunishments(event.getPlayer().getUniqueId());
        Punishment latestExpiriringBan = null;

        for (Punishment punishment : punishments) {
            if (punishment.getType() != PunishmentType.BAN)
                continue;

            if (latestExpiriringBan == null)
                latestExpiriringBan = punishment;

            if (latestExpiriringBan.getEndTime() < punishment.getEndTime())
                latestExpiriringBan = punishment;
        }

        if (latestExpiriringBan != null && latestExpiriringBan.getEndTime() > System.currentTimeMillis() / 1000) { // we shouldn't need to worry about this
            event.getPlayer().disconnect(new TextComponent(BanFormatter.getMessage(latestExpiriringBan)));
        }
    }

}

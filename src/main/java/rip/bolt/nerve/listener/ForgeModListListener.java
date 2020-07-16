package rip.bolt.nerve.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.utils.ModInfo;

public class ForgeModListListener implements Listener {

    private Map<ProxiedPlayer, List<ModInfo>> mods = new HashMap<ProxiedPlayer, List<ModInfo>>();

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxyServer.getInstance().getScheduler().schedule(NervePlugin.getInstance(), new Runnable() {

            @Override
            public void run() {
                event.getPlayer().sendData("FML|HS", new byte[] { -2, 0 });
                event.getPlayer().sendData("FML|HS", new byte[] { 0, 2, 0, 0, 0, 0 });
                event.getPlayer().sendData("FML|HS", new byte[] { 2, 0, 0, 0, 0 });
            }

        }, 100, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onForgeReply(PluginMessageEvent event) {
        if (event.getTag().equals("FML|HS")) {
            if (event.getSender() instanceof ProxiedPlayer) {
                ProxiedPlayer player = (ProxiedPlayer) event.getSender();
                if (this.mods.containsKey(player))
                    return;

                List<ModInfo> mods = new ArrayList<ModInfo>();
                byte[] data = event.getData();

                for (int i = 2; i < data.length;) {
                    int start = i + 1;
                    int end = start + data[i];

                    String modId = new String(Arrays.copyOfRange(data, start, end));
                    i = end;

                    start = i + 1;
                    end = start + data[i];

                    String version = new String(Arrays.copyOfRange(data, start, end));
                    mods.add(new ModInfo(modId, version));
                    i = end;
                }

                if (mods.size() > 0) { // don't send empty mod list to Nettle
                    this.mods.put(player, mods);
                    player.getServer().sendData("NettleMods", getData(mods));
                }
            } else if (event.getReceiver() instanceof ProxiedPlayer) { // nettle sending forge handshake to player - stop this or else they'll disconnect
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerDisconnectEvent event) {
        mods.remove(event.getPlayer());
    }

    @EventHandler
    public void onConnect(ServerConnectedEvent event) {
        List<ModInfo> mods = this.mods.get(event.getPlayer());
        if (mods == null || mods.size() == 0)
            return;

        event.getServer().sendData("NettleMods", getData(mods));
    }

    private byte[] getData(List<ModInfo> mods) {
        JSONArray data = new JSONArray();
        for (ModInfo mod : mods) {
            JSONObject info = new JSONObject();
            info.put("id", mod.getModId());
            info.put("version", mod.getVersion());

            data.put(info);
        }

        return data.toString().getBytes();
    }

}

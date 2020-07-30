package rip.bolt.nerve.feature;

import java.util.ArrayList;
import java.util.List;

import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.utils.GlobUtils;

public class Feature {

    private String name;
    private List<String> servers;

    public Feature(String name, List<String> servers) {
        this.name = name;
        this.servers = servers == null ? new ArrayList<String>() : servers;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabledOnServer(String name) {
        for (String server : servers) {
            boolean enabled = true;
            if (server.startsWith("-")) {
                server = server.substring(1);
                enabled = false;
            }

            String regex = null;
            if (server.startsWith("glob:")) {
                regex = GlobUtils.convertGlobToRegex(server.substring(5));
            } else if (server.startsWith("regex:")) {
                regex = server.substring(6);
            }

            if ((regex == null && server.equals(name)) || (regex != null && name.matches(regex)))
                return enabled;
        }

        return false;
    }

    public static boolean isFeatureEnabledOnServer(String name, String server) {
        return NervePlugin.getInstance().getFeatureManager().isFeatureEnabled(name, server);
    }

}

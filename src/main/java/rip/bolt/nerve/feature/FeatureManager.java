package rip.bolt.nerve.feature;

import java.util.ArrayList;
import java.util.List;

import rip.bolt.nerve.NervePlugin;
import rip.bolt.nerve.config.ConfigSection;

public class FeatureManager {

    private List<Feature> features = new ArrayList<Feature>();

    public void loadFromConfig() {
        List<ConfigSection> featureSections = NervePlugin.getInstance().getAppConfig().getSectionList("features");
        if (featureSections == null)
            return;

        for (ConfigSection section : featureSections) {
            String name = section.getKeys().iterator().next();

            features.add(new Feature(name, section.getSection(name).getStringList("servers")));
        }
    }

    public Feature getFeatureByName(String name) {
        for (Feature feature : features)
            if (feature.getName().equalsIgnoreCase(name))
                return feature;

        return null;
    }

    public boolean isFeatureEnabled(String name, String server) {
        Feature feature = getFeatureByName(name);
        if (feature == null)
            return false;

        return feature.isEnabledOnServer(server);
    }

}

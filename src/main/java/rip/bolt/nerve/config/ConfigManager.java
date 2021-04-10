package rip.bolt.nerve.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import net.md_5.bungee.api.plugin.Plugin;
import rip.bolt.nerve.utils.FileUtils;

public class ConfigManager {

    protected File path;

    public ConfigManager(Plugin plugin, String name) {
        this.path = new File(plugin.getDataFolder(), name + ".yml");

        try {
            if (!path.exists())
                FileUtils.copyInputStreamToFile(plugin.getClass().getResourceAsStream("/" + name + ".yml"), path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Config get() {
        try {
            return new Config(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}

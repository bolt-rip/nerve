package rip.bolt.nerve.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.velocitypowered.api.plugin.annotation.DataDirectory;

import rip.bolt.nerve.utils.FileUtils;

public class ConfigManager {

    protected File path;

    public ConfigManager(@DataDirectory File dataFolder, String name) {
        this.path = new File(dataFolder, name + ".yml");

        try {
            if (!path.exists())
                FileUtils.copyInputStreamToFile(ConfigManager.class.getResourceAsStream("/" + name + ".yml"), path);
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

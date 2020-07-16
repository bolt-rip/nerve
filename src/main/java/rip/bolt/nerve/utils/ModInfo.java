package rip.bolt.nerve.utils;

public class ModInfo {

    public String modId, version;

    public ModInfo(String modId, String version) {
        this.modId = modId;
        this.version = version;
    }

    public String getModId() {
        return modId;
    }

    public String getVersion() {
        return version;
    }

}

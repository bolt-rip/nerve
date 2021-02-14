package rip.bolt.nerve.api.definitions;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PoolInformation {

    private int queueSize;
    private List<String> maps;

    public int getQueueSize() {
        return queueSize;
    }

    public List<String> getMaps() {
        return maps;
    }

}

package rip.bolt.nerve.api.definitions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Veto {

    private String map;

    public Veto(String map) {
        this.map = map;
    }

    public String getMap() {
        return map;
    }

}

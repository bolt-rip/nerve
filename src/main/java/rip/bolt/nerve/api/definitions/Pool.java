package rip.bolt.nerve.api.definitions;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pool {

    private int id;
    private int players;
    private List<PGMMap> maps;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayers() {
        return this.players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public List<PGMMap> getMaps() {
        return this.maps;
    }

    public void setMaps(List<PGMMap> maps) {
        this.maps = maps;
    }

}

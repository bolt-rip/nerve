package rip.bolt.nerve.api.definitions;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pool {

    private Integer id;
    private Integer players;
    private List<PGMMap> maps;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlayers() {
        return this.players;
    }

    public void setPlayers(Integer players) {
        this.players = players;
    }

    public List<PGMMap> getMaps() {
        return this.maps;
    }

    public void setMaps(List<PGMMap> maps) {
        this.maps = maps;
    }

}

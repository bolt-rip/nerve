package rip.bolt.nerve.api.definitions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PGMMap {

    private Integer id;
    private String name;

    public PGMMap() {
    }

    public PGMMap(Integer mapId) {
        this.id = mapId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

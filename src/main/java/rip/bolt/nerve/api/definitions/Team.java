package rip.bolt.nerve.api.definitions;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {

    private String name;

    @JsonProperty("players")
    private List<Participant> participants;

    public Team() {
    }

    public Team(String name, List<Participant> participants) {
        this.name = name;
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public List<Participant> getPlayers() {
        return this.participants;
    }

}
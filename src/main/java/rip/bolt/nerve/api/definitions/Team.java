package rip.bolt.nerve.api.definitions;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Team {

    private String name;

    private List<Participation> participations;

    public Team() {
    }

    public Team(String name, List<Participation> participations) {
        this.name = name;
        this.participations = participations;
    }

    public String getName() {
        return name;
    }

    public List<Participation> getParticipations() {
        return participations;
    }

    public List<User> getPlayers() {
        return participations.stream().map(Participation::getUser).collect(Collectors.toList());
    }

}
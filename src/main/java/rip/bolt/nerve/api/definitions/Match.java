package rip.bolt.nerve.api.definitions;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import rip.bolt.nerve.api.MatchStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Match {

    @JsonProperty("id")
    private String matchId;

    private String map;
    private String server;

    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Team> teams;

    private Team winner;

    private MatchStatus status;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public Team getWinner() {
        return winner;
    }

    public void setWinner(Team winner) {
        this.winner = winner;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public int getQueueSize() {
        return teams.get(0).getParticipations().size();
    }

}

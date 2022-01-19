package rip.bolt.nerve.api.definitions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.velocitypowered.api.proxy.Player;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Match {

    private String id;
    private PGMMap map;
    private String server;
    private int seriesId;

    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Team> teams;
    private Team winner;

    private Instant createdAt;
    private Instant startedAt;
    private Instant endedAt;

    private MatchStatus status;

    @JsonIgnore
    private long lastUpdateTime;

    public Match() {
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public PGMMap getMap() {
        return map;
    }

    public void setMap(PGMMap map) {
        this.map = map;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getSeriesId() {
        return this.seriesId;
    }

    public void setSeriesId(int seriesId) {
        this.seriesId = seriesId;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public int getQueueSize() {
        return teams.get(0).getParticipations().size() * 2;
    }

    public Team getPlayerTeam(Player player) {
        return getPlayerTeam(player.getUniqueId());
    }

    public Team getPlayerTeam(UUID player) {
        for (Team team : teams)
            for (Participation participation : team.getParticipations())
                if (participation.getUser().getUniqueId().equals(player))
                    return team;

        return null;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

}

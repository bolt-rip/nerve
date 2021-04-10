package rip.bolt.nerve.api.definitions;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueUpdate {

    protected Action action;
    protected User user;
    protected int limit;
    protected List<UUID> players;

    public Action getAction() {
        return action;
    }

    public User getUser() {
        return user;
    }

    public int getLimit() {
        return limit;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public static enum Action {

        JOIN,
        LEAVE;

    }

}

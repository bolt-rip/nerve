package rip.bolt.nerve.api.definitions;

import java.util.List;

public class Match {

    private String matchCode, serverName;
    private List<Participant> participants;

    public String getMatchCode() {
        return matchCode;
    }

    public String getServerName() {
        return serverName;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

}

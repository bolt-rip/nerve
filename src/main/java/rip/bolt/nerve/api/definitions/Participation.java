package rip.bolt.nerve.api.definitions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Participation {

    private User user;

    public Participation(User user) {
        this.user = user;
    }

    public Participation() {
        
    }

    public User getUser() {
        return user;
    }

}

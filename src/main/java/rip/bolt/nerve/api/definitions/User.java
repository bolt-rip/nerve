package rip.bolt.nerve.api.definitions;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    private String uuid;

    public UUID getUUID() {
        return UUID.fromString(uuid);
    }

}
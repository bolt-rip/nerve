package rip.bolt.nerve.api.definitions;

import java.util.UUID;

public class Participant {

    private String uuid;

    public UUID getUUID() {
        return UUID.fromString(uuid);
    }

}

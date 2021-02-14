package rip.bolt.nerve.api.definitions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BoltResponse {

    private boolean success;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

}

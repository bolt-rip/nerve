package rip.bolt.nerve.api;

import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.config.AppData;

public class APIManager {

    private Client client;
    private WebTarget currentlyRunningMatchesEndpoint;

    public APIManager() {
        client = ClientBuilder.newClient().register(JacksonJsonProvider.class).register(AuthorisationHeaderFilter.class);
        currentlyRunningMatchesEndpoint = client.target(AppData.API.getURL()).path(AppData.API.getCurrentlyRunningMatchesPath());
    }

    public List<Match> getCurrentlyRunningMatches() {
        try {
            WebTarget endpoint = currentlyRunningMatchesEndpoint;
            Invocation.Builder builder = endpoint.request(MediaType.APPLICATION_JSON);

            return builder.get(new GenericType<List<Match>>() {
            });
        } catch (NotFoundException e) {
            // ignore
            return null;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

}

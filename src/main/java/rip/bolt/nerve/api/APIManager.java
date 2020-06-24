package rip.bolt.nerve.api;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import rip.bolt.nerve.api.definitions.Punishment;
import rip.bolt.nerve.config.AppData;

public class APIManager {

    private Client client;
    private WebTarget userPunishmentsEndpoint, activeUserPunishmentsEndpoint, submitUserPunishmentEndpoint;

    public APIManager() {
        client = ClientBuilder.newClient().register(JacksonJsonProvider.class).register(AuthorisationHeaderFilter.class);

        userPunishmentsEndpoint = client.target(AppData.API.getURL() + AppData.userPunishmentEndpoint);
        activeUserPunishmentsEndpoint = userPunishmentsEndpoint.queryParam("active", "true");
        submitUserPunishmentEndpoint = client.target(AppData.API.getURL() + AppData.submitUserPunishmentEndpoint);
    }

    public List<Punishment> getActiveUserPunishments(UUID uuid) {
        WebTarget endpoint = activeUserPunishmentsEndpoint.resolveTemplate("uuid", uuid);
        Invocation.Builder builder = endpoint.request(MediaType.APPLICATION_JSON);

        return builder.get(new GenericType<List<Punishment>>() {
        });
    }

    public List<Punishment> getUserPunishments(UUID uuid) {
        WebTarget endpoint = userPunishmentsEndpoint.resolveTemplate("uuid", uuid);
        Invocation.Builder builder = endpoint.request(MediaType.APPLICATION_JSON);

        return builder.get(new GenericType<List<Punishment>>() {
        });
    }

    public void submitPunishment(Punishment punishment) {
        WebTarget endpoint = submitUserPunishmentEndpoint.resolveTemplate("uuid", punishment.getPlayer());
        Invocation.Builder builder = endpoint.request(MediaType.APPLICATION_JSON);

        Response response = builder.post(Entity.entity(punishment, MediaType.APPLICATION_JSON));
        if (response.getStatus() != 200 || response.getStatus() != 201)
            System.out.println("[Nerve] Error adding punishment for " + punishment.getPlayer() + " for " + punishment.getReason() + "\n" + response.readEntity(String.class));
    }

}

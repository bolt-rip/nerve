package rip.bolt.nerve.api;

import java.util.List;
import java.util.UUID;

import rip.bolt.jsonrest.Client;
import rip.bolt.jsonrest.Endpoint;
import rip.bolt.jsonrest.Response;
import rip.bolt.nerve.api.definitions.Punishment;
import rip.bolt.nerve.config.AppData;

public class APIManager {

    private Client client;
    private Endpoint userPunishmentsEndpoint, activeUserPunishmentsEndpoint, submitUserPunishmentEndpoint;

    public APIManager() {
        client = new Client();
        client.setAuthorisationKey(AppData.API.getKey());

        userPunishmentsEndpoint = client.endpoint(AppData.API.getURL() + AppData.userPunishmentEndpoint);
        activeUserPunishmentsEndpoint = userPunishmentsEndpoint.parameter("active", "true");
        submitUserPunishmentEndpoint = client.endpoint(AppData.API.getURL() + AppData.submitUserPunishmentEndpoint);        
    }

    public List<Punishment> getActiveUserPunishments(UUID uuid) {
        Endpoint endpoint = activeUserPunishmentsEndpoint.resolve("uuid", uuid);
        Response response = endpoint.get();

        return response.asList(Punishment.class);
    }

    public List<Punishment> getUserPunishments(UUID uuid) {
        Endpoint endpoint = userPunishmentsEndpoint.resolve("uuid", uuid);
        Response response = endpoint.get();

        return response.asList(Punishment.class);
    }

    public void submitPunishment(Punishment punishment) {
        Endpoint endpoint = submitUserPunishmentEndpoint.resolve("uuid", punishment.getPlayer());

        Response response = endpoint.post(punishment);
        if (response.getStatusCode() != 200 || response.getStatusCode() != 201)
            System.out.println("[Nerve] Error adding punishment for " + punishment.getPlayer() + " for " + punishment.getReason() + "\n" + response.as(String.class));
    }

}

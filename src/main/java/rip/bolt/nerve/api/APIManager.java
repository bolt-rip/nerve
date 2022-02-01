package rip.bolt.nerve.api;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rip.bolt.nerve.api.definitions.BoltResponse;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.Pool;
import rip.bolt.nerve.api.definitions.PGMMap;

public class APIManager {

    private final APIService apiService;

    @Inject
    public APIManager(ObjectMapper objectMapper, APIConfig config) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> chain.proceed(chain.request().newBuilder().header("Authorization", "Bearer " + config.key()).build()));

        Retrofit retrofit = new Retrofit.Builder().baseUrl(config.url()).addConverterFactory(JacksonConverterFactory.create(objectMapper)).addCallAdapterFactory(new DefaultCallAdapterFactory<>()).client(httpClient.build()).build();
        apiService = retrofit.create(APIService.class);
    }

    public Pool getPool(int seriesId, int queueSize) {
        return apiService.getPool(seriesId, queueSize);
    }

    public BoltResponse veto(Match match, UUID uuid, PGMMap map) {
        return apiService.veto(match.getId(), uuid.toString(), map);
    }

    public List<Match> matches() {
        return apiService.matches();
    }

}

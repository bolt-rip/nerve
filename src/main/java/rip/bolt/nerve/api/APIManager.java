package rip.bolt.nerve.api;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rip.bolt.nerve.api.definitions.BoltResponse;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.PoolInformation;
import rip.bolt.nerve.api.definitions.Veto;
import rip.bolt.nerve.config.AppData;

public class APIManager {

    private final APIService apiService;

    public APIManager(ObjectMapper objectMapper) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> chain.proceed(chain.request().newBuilder().header("Authorization", "Bearer " + AppData.API.getKey()).build()));

        Retrofit retrofit = new Retrofit.Builder().baseUrl(AppData.API.getURL()).addConverterFactory(JacksonConverterFactory.create(objectMapper)).addCallAdapterFactory(new DefaultCallAdapterFactory<>()).client(httpClient.build()).build();
        apiService = retrofit.create(APIService.class);
    }

    public PoolInformation getPoolInformation(int queueSize) {
        return apiService.getPoolInformation(queueSize);
    }

    public BoltResponse veto(Match match, UUID uuid, Veto veto) {
        return apiService.veto(match.getId(), uuid.toString(), veto);
    }

    public List<Match> matches() {
        return apiService.matches();
    }

}

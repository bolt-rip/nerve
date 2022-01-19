package rip.bolt.nerve.api;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rip.bolt.nerve.api.definitions.BoltResponse;
import rip.bolt.nerve.api.definitions.Match;
import rip.bolt.nerve.api.definitions.Pool;
import rip.bolt.nerve.api.definitions.PGMMap;

public interface APIService {

    @GET("series/{series}/pool")
    Pool getPool(@Path("series") int seriesId, @Query("players") int queueSize);

    @POST("ranked/matches/{match}/player/{uuid}/vote")
    BoltResponse veto(@Path("match") String match, @Path("uuid") String uuid, @Body PGMMap map);

    @GET("ranked/matches")
    List<Match> matches();

}

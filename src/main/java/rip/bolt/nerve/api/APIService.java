package rip.bolt.nerve.api;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rip.bolt.nerve.api.definitions.BoltResponse;
import rip.bolt.nerve.api.definitions.PoolInformation;
import rip.bolt.nerve.api.definitions.Veto;

public interface APIService {

    @GET("config/pools/{queueSize}")
    PoolInformation getPoolInformation(@Path("queueSize") int queueSize);

    @POST("ranked/matches/{match}/player/{uuid}/vote")
    BoltResponse veto(@Path("match") String match, @Path("uuid") String uuid, @Body Veto veto);

}

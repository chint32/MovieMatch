package cotey.hinton.moviedate.feature_main.data.remote

import cotey.hinton.moviedate.feature_auth.domain.models.SpotifySearchDetailsResponse
import cotey.hinton.moviedate.feature_auth.domain.models.SpotifySearchResponse
import cotey.hinton.moviedate.feature_auth.domain.models.SpotifyTop200Response
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyApi {

    @GET("/top_200_tracks")
    suspend fun getSpotifyTop200(
    ) : Response<List<SpotifyTop200Response>>


    @GET("/search/")
    suspend fun searchSpotifySongsByTitle(
        @Query("q") searchValue: String = "",
        @Query("type") type: String = "tracks",
    ) : Response<SpotifySearchResponse>

    @GET("/tracks/")
    suspend fun searchSpotifySongById(
        @Query("ids") id: String = "",
    ) : Response<SpotifySearchDetailsResponse>


}
package cotey.hinton.moviedate.feature_auth.data.remote

import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import retrofit2.Response
import retrofit2.http.GET

interface ImdbTop100Api {
    @GET("/")
    suspend fun getTop100() : Response<List<Movie>>
}
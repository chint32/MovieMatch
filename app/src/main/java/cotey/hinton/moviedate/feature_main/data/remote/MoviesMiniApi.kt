package cotey.hinton.moviedate.feature_main.data.remote

import cotey.hinton.moviedate.feature_main.domain.models.GetMovieIdResponse
import cotey.hinton.moviedate.feature_main.domain.models.MovieDetailsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MoviesMiniApi {

    @GET("/movie/imdb_id/byTitle/{name}")
    suspend fun searchForMovieByTitle(
        @Path("name") name: String
    ) : Response<GetMovieIdResponse>


    @GET("/movie/id/{movie_id}")
    suspend fun getMovieDetailsById(
        @Path("movie_id") movieId: String
    ) : Response<MovieDetailsResponse>
}
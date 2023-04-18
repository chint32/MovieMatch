package cotey.hinton.moviedate.feature_main.data.repository

import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import cotey.hinton.moviedate.feature_main.data.remote.MoviesMiniApi
import cotey.hinton.moviedate.feature_main.domain.repository.MoviesMiniRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MoviesMiniRepositoryImpl @Inject constructor(private val api: MoviesMiniApi) : MoviesMiniRepository {
    override fun getMovieDetailsFromTitle(title: String): Flow<List<Movie>> {
        return flow {
            val response = api.searchForMovieByTitle(title)

            if (response.code() == 200 && response.isSuccessful) {
                if (response.body() != null) {
                    val result = ArrayList<Movie>()
                    for (i in response.body()!!.results.indices) {
                        val detailResponse =
                            api.getMovieDetailsById(response.body()!!.results[i].imdb_id!!)
                        if (detailResponse.code() == 200 && detailResponse.isSuccessful) {
                            if (detailResponse.body() != null) {
                                val movie = Movie(
                                    -1,
                                    detailResponse.body()!!.results.title!!,
                                    detailResponse.body()!!.results.banner!!,
                                    detailResponse.body()!!.results.rating.toString(),
                                    "",
                                    detailResponse.body()!!.results.year!!,
                                    detailResponse.body()!!.results.image_url!!,
                                    detailResponse.body()!!.results.description!!,
                                    detailResponse.body()!!.results.trailer!!,
                                    emptyList(),
                                    emptyList(),
                                    emptyList(),
                                    detailResponse.body()!!.results.imdb_id!!
                                )
                                result.add(movie)
                            }
                        }
                    }
                    emit(result)
                }
                else println("body is null")
            }
            else println("error")
        }
    }
}
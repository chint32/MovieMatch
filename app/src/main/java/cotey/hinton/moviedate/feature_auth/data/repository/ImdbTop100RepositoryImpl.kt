package cotey.hinton.moviedate.feature_auth.data.repository

import cotey.hinton.moviedate.feature_auth.data.remote.ImdbTop100Api
import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import cotey.hinton.moviedate.feature_auth.domain.repository.ImdbTop100Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ImdbTop100RepositoryImpl @Inject constructor(private val api: ImdbTop100Api) : ImdbTop100Repository {
    override fun getTop100(): Flow<List<Movie>> = flow {
        val response = api.getTop100()
        if (response.code() == 200 && response.isSuccessful) {
            if (response.body() != null) {
                emit(response.body()!!)
            } else {
                println("Network call to IMDB Top 100 movies was successful but the body is NULL")
            }
        } else {
            println("Error retrieving IMDB Top 100 movies")
        }
    }
}
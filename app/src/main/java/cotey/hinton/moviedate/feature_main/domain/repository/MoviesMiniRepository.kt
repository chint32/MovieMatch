package cotey.hinton.moviedate.feature_main.domain.repository

import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import kotlinx.coroutines.flow.Flow

interface MoviesMiniRepository {

    fun getMovieDetailsFromTitle(
        title: String
    ) : Flow<List<Movie>>
}
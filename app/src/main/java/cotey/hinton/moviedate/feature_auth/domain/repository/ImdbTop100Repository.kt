package cotey.hinton.moviedate.feature_auth.domain.repository

import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import kotlinx.coroutines.flow.Flow

interface ImdbTop100Repository {
    fun getTop100() : Flow<List<Movie>>
}
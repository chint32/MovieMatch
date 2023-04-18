package cotey.hinton.moviedate.feature_main.domain.repository

import cotey.hinton.moviedate.feature_auth.domain.models.SpotifySearchDetailsResponse
import cotey.hinton.moviedate.feature_auth.domain.models.SpotifySearchResponse
import cotey.hinton.moviedate.feature_auth.domain.models.SpotifyTop200Response
import kotlinx.coroutines.flow.Flow

interface SpotifyRepository {

    fun getSpotifyTop200() : Flow<List<SpotifyTop200Response>>

    fun getSpotifySongsByTitle(searchValue: String) : Flow<SpotifySearchResponse>
    fun getSpotifySongsById(id: String) : Flow<SpotifySearchDetailsResponse>
}
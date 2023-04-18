package cotey.hinton.moviedate.feature_main.data.repository

import cotey.hinton.moviedate.feature_auth.domain.models.SpotifySearchDetailsResponse
import cotey.hinton.moviedate.feature_auth.domain.models.SpotifySearchResponse
import cotey.hinton.moviedate.feature_auth.domain.models.SpotifyTop200Response
import cotey.hinton.moviedate.feature_main.data.remote.SpotifyApi
import cotey.hinton.moviedate.feature_main.domain.repository.SpotifyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SpotifyRepositoryImpl @Inject constructor(private val api: SpotifyApi) : SpotifyRepository {
    override fun getSpotifyTop200(): Flow<List<SpotifyTop200Response>> {
        println("****** Spotify Top 200 **********")
        return flow {
            val response = api.getSpotifyTop200()

            if (response.code() == 200 && response.isSuccessful) {
                if (response.body() != null) {
                    emit(response.body()!!)
                } else {
                    println("body is null")
                }
            } else {
                println("error: ${response.errorBody().toString()}")
                println(response.message())
                println(response.raw().toString())
            }
        }
    }

    override fun getSpotifySongsByTitle(searchValue: String): Flow<SpotifySearchResponse> {
        println("****** Spotify Search **********")
        return flow {
            val response = api.searchSpotifySongsByTitle(searchValue)

            if (response.code() == 200 && response.isSuccessful) {
                if (response.body() != null) {
                    emit(response.body()!!)
                } else {
                    println("body is null")
                }
            } else {
                println("error: ${response.message()}")
            }
        }
    }

    override fun getSpotifySongsById(id: String): Flow<SpotifySearchDetailsResponse> {
        println("****** Spotify Search Details**********")
        println("id = $id")

        return flow {
                val response = api.searchSpotifySongById(id)

                if (response.code() == 200 && response.isSuccessful) {
                    if (response.body() != null) {
                        emit(response.body()!!)
                    } else {
                        println("body is null")
                    }
                } else {
                    println("error")
                }
        }
    }


}
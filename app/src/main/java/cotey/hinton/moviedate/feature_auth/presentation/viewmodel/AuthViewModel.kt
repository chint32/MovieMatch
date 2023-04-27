package cotey.hinton.moviedate.feature_auth.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cotey.hinton.moviedate.feature_auth.data.repository.FirebaseAuthRepositoryImpl
import cotey.hinton.moviedate.feature_auth.data.repository.ImdbTop100RepositoryImpl
import cotey.hinton.moviedate.feature_auth.domain.models.Artist
import cotey.hinton.moviedate.feature_auth.domain.models.TrackMetaData
import cotey.hinton.moviedate.feature_auth.presentation.screens.auth.AuthState
import cotey.hinton.moviedate.feature_auth.presentation.screens.create_profile.CreateProfileState
import cotey.hinton.moviedate.feature_auth.presentation.screens.select_favorites.SelectFavoritesState
import cotey.hinton.moviedate.feature_auth.presentation.screens.shared.SharedState
import cotey.hinton.moviedate.feature_main.data.repository.MoviesMiniRepositoryImpl
import cotey.hinton.moviedate.feature_main.data.repository.SpotifyRepositoryImpl
import cotey.hinton.moviedate.feature_main.presentation.screens.song_details.SongDetailsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseRepository: FirebaseAuthRepositoryImpl,
    private val top100Repository: ImdbTop100RepositoryImpl,
    private val repository: MoviesMiniRepositoryImpl,
    private val spotifyRepository: SpotifyRepositoryImpl
) : ViewModel() {

    private val _authState = AuthState()
    val authState: AuthState = _authState

    fun login() {
        _authState.isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firebaseRepository.login(_authState.email.value, _authState.pw.value).collect {
                    _authState.authResult.value = it.first
                    _authState.error.value =
                        if (it.second == null) null else it.second!!.errorCode
                    _authState.isLoading.value = false
                }
            } catch (e: Exception) {
                println("Error!!!!!!!!!!!  " + e.message)
            }
        }
    }

    private val _sharedState = SharedState()
    val sharedState: SharedState = _sharedState

    private val _createProfileState = CreateProfileState()
    val createProfileState: CreateProfileState = _createProfileState

    private val _selectFavoritesState = SelectFavoritesState()
    val selectFavoritesState: SelectFavoritesState = _selectFavoritesState

    fun register() {
        _selectFavoritesState.isLoading.value = true
        viewModelScope.launch {
            try {
                firebaseRepository.register(
                    _authState.email.value,
                    _authState.pw.value,
                    _createProfileState.images,
                    _sharedState.userInfo.value
                ).collect {
                    _selectFavoritesState.authResult.value = it.first
                    _selectFavoritesState.error.value =
                        if (it.second == null) null else it.second!!.errorCode
                    _selectFavoritesState.isLoading.value = false
                }
            } catch (e: Exception) {
                println("Error!!!!!!!!!!!  " + e.message)
            }
        }
    }

    private var hasSongsBeenCalled = false
    fun getSpotifyTop200() {
        if (hasSongsBeenCalled) return
        hasSongsBeenCalled = true
        _selectFavoritesState.isLoading.value = true
        viewModelScope.launch {
            spotifyRepository.getSpotifyTop200().collect { response ->
                println(response)
                if (_selectFavoritesState.top200Songs.isNotEmpty())
                    _selectFavoritesState.top200Songs.clear()
                _selectFavoritesState.top200Songs.addAll(response.map {
                    it.trackMetadata.id = it.trackMetadata.trackUri.substringAfterLast(":")
                    it.trackMetadata })
            }
            _selectFavoritesState.isLoading.value = false
        }
    }

    fun searchSongsByTitle(title: String) {
        if (title.isBlank()) {
            if (_selectFavoritesState.top200Songs.isNotEmpty())
                _selectFavoritesState.top200Songs.clear()
        } else {
            if (_selectFavoritesState.searchedSongs.isNotEmpty())
                _selectFavoritesState.searchedSongs.clear()
        }
        _selectFavoritesState.isLoading.value = true
        viewModelScope.launch {
            try {
                if (title.isBlank()) {
                    spotifyRepository.getSpotifyTop200().collect { result ->
                        _selectFavoritesState.top200Songs.addAll(result.map { it.trackMetadata })
                    }
                } else {
                    spotifyRepository.getSpotifySongsByTitle(title).collect { result ->
                        println(result)
                        _selectFavoritesState.searchedSongs.addAll(result.tracks.map {
                            TrackMetaData(
                                it.data.albumOfTrack.name,
                                it.data.albumOfTrack.uri,
                                it.data.id,
                                it.data.albumOfTrack.coverArt.sources[0].url,
                                listOf(Artist(it.data.artists.items[0].profile.name))
                            )
                        })
                    }
                }
                _selectFavoritesState.isLoading.value = false

            } catch (e: Exception) {
                println("Error!!!!!!!!!!!  " + e.message)
            }
        }
    }

    private val _songDetailsScreenState = SongDetailsState()
    val songDetailsState = _songDetailsScreenState

    fun getSongDetails(id: String) {
        _songDetailsScreenState.isLoading.value = true
        viewModelScope.launch {
            try {
                spotifyRepository.getSpotifySongsById(id).collect {
                    _songDetailsScreenState.songDetails.value = it.tracks[0]
                }
                _songDetailsScreenState.isLoading.value = false

            } catch (e: Exception) {
                println("Error!!!!!!!!!!!  " + e.message)
            }
        }
    }

    private var hasMoviesBeenCalled = false
    fun getTop100() {
        if (hasMoviesBeenCalled) return
        hasMoviesBeenCalled = true
        _selectFavoritesState.isLoading.value = true
        viewModelScope.launch {
            try {
                top100Repository.getTop100().collect {
                    if (_selectFavoritesState.top100Movies.isNotEmpty())
                        _selectFavoritesState.top100Movies.clear()
                    _selectFavoritesState.top100Movies.addAll(it)
                }
                _selectFavoritesState.isLoading.value = false

            } catch (e: Exception) {
                println("Error!!!!!!!!!!!  " + e.message)
            }
        }
    }

    fun searchMoviesByTitle(title: String) {
        if (title.isBlank()) {
            if (_selectFavoritesState.top100Movies.isNotEmpty())
                _selectFavoritesState.top100Movies.clear()
        } else {
            if (_selectFavoritesState.searchedMovies.isNotEmpty())
                _selectFavoritesState.searchedMovies.clear()
        }
        _selectFavoritesState.isLoading.value = true
        viewModelScope.launch {
            try {
                if (title.isBlank()) {
                    top100Repository.getTop100().collect {
                        _selectFavoritesState.top100Movies.addAll(it)
                    }
                } else {
                    repository.getMovieDetailsFromTitle(title).collect {
                        _selectFavoritesState.searchedMovies.addAll(it)
                    }
                }
                _selectFavoritesState.isLoading.value = false

            } catch (e: Exception) {
                println("Error!!!!!!!!!!!  " + e.message)
            }
        }
    }


    fun addImage(uri: Uri, index: Int) {
        _createProfileState.images[index] = uri
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                firebaseRepository.logout().collect {
                    // handle logout
                }
            } catch (e: Exception) {
                println("Error!!!!!!!!!!!  " + e.message)
            }
        }
    }
}
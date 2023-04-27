package cotey.hinton.moviedate.feature_main.presentation.viewmodel

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import cotey.hinton.moviedate.feature_auth.data.repository.ImdbTop100RepositoryImpl
import cotey.hinton.moviedate.feature_auth.domain.models.Artist
import cotey.hinton.moviedate.feature_auth.domain.models.TrackMetaData
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo
import cotey.hinton.moviedate.feature_main.data.repository.FirebaseMainRepositoryImpl
import cotey.hinton.moviedate.feature_main.data.repository.MoviesMiniRepositoryImpl
import cotey.hinton.moviedate.feature_main.data.repository.SpotifyRepositoryImpl
import cotey.hinton.moviedate.feature_main.domain.models.ImageMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import cotey.hinton.moviedate.feature_main.domain.models.TextMessage
import cotey.hinton.moviedate.feature_main.presentation.screens.conversations.ConversationsScreenState
import cotey.hinton.moviedate.feature_main.presentation.screens.edit_movies.EditFavoritesScreenState
import cotey.hinton.moviedate.feature_main.presentation.screens.main.MainScreenState
import cotey.hinton.moviedate.feature_main.presentation.screens.affections.AffectionsScreenState
import cotey.hinton.moviedate.feature_main.presentation.screens.messages.MessagesScreenState
import cotey.hinton.moviedate.feature_main.presentation.screens.profile_details.ProfileDetailsScreenState
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.state.SharedState
import cotey.hinton.moviedate.feature_main.presentation.screens.song_details.SongDetailsState
import kotlinx.coroutines.tasks.await

@RequiresApi(Build.VERSION_CODES.N)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val firebaseMainRepositoryImpl: FirebaseMainRepositoryImpl,
    private val top100Repository: ImdbTop100RepositoryImpl,
    private val repository: MoviesMiniRepositoryImpl,
    private val spotifyRepository: SpotifyRepositoryImpl
) : ViewModel() {

    private val _sharedState = SharedState()
    val sharedState = _sharedState

    private val _profileDetailsScreenState = ProfileDetailsScreenState()
    val profileDetailsScreenState = _profileDetailsScreenState

    private val _mainScreenState = MainScreenState()
    val mainScreenState = _mainScreenState

    init {
        initializeStartingData()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initializeStartingData() {
        _mainScreenState.isLoading.value = true
        viewModelScope.launch {
            // get user info to curate data for this user
            firebaseMainRepositoryImpl.getUserInfo(FirebaseAuth.getInstance().currentUser!!.uid)
                .collect { _sharedState.myUserInfo.value = it }
            // get other users near this user
            firebaseMainRepositoryImpl.getUsersNearby(_sharedState.myUserInfo.value).collect {
                if (_mainScreenState.users.isNotEmpty()) _mainScreenState.users.clear()
                _mainScreenState.users.addAll(it)
                _mainScreenState.isLoaded.value = true
                _mainScreenState.isLoading.value = false
            }
            // store fcm token if first login
            if (_sharedState.myUserInfo.value.isFirstLogin) {
                _sharedState.myUserInfo.value.fcmToken =
                    FirebaseMessaging.getInstance().token.await()
                _sharedState.myUserInfo.value.isFirstLogin = false
                updateUserInfo(_sharedState.myUserInfo.value, null, null)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun updateUserInfo(
        userInfo: UserInfo,
        images: SnapshotStateList<Uri?>?,
        removeUserInfo: UserInfo?
    ) {
        viewModelScope.launch {
            firebaseMainRepositoryImpl.updateUserInfo(userInfo, images).collect { result ->
                if (result && removeUserInfo != null) {
                    _mainScreenState.users.removeIf { it.first == removeUserInfo }
                }
            }
        }
    }

    private val _affectionsScreenState = AffectionsScreenState()
    val affectionsScreenState = _affectionsScreenState

    fun acknowledgeNewMatches() {
        viewModelScope.launch {
            firebaseMainRepositoryImpl.acknowledgeNewMatches(FirebaseAuth.getInstance().currentUser!!.uid)
                .collect {
                    if (it) println("acknowledged new matches")
                }
        }
    }

    fun acknowledgeNewLikes() {
        viewModelScope.launch {
            firebaseMainRepositoryImpl.acknowledgeNewLikes(FirebaseAuth.getInstance().currentUser!!.uid)
                .collect {
                    if (it) println("acknowledged new likes")
                }
        }
    }

    fun addMatch(matchedUserInfo: UserInfo) {
        viewModelScope.launch {
            firebaseMainRepositoryImpl.updateLikeToMatch(
                _sharedState.myUserInfo.value.uid,
                matchedUserInfo.uid
            )
                .collect {
                    if (it) println("Match added")
                }
        }
    }

    fun getLikesAndMatches() {
        _affectionsScreenState.isLoading.value = true
        viewModelScope.launch {
            firebaseMainRepositoryImpl.getAffections(FirebaseAuth.getInstance().currentUser!!.uid)
                .collect {
                    if (it == null) {
                        _affectionsScreenState.isLoading.value = false
                        return@collect
                    }
                    if (it.first.isEmpty() && it.second.isEmpty()) {
                        _affectionsScreenState.isLoading.value = false
                        return@collect
                    }
                    if (_affectionsScreenState.likesMe.isNotEmpty()) _affectionsScreenState.likesMe.clear()
                    for (pair in it.first) {
                        firebaseMainRepositoryImpl.getUserInfo(pair.first).collect { matchInfo ->
                            val matchPair = Pair(matchInfo, pair.second)
                            if (!_affectionsScreenState.likesMe.contains(matchPair))
                                _affectionsScreenState.likesMe.add(matchPair)
                        }
                    }
                    if (_affectionsScreenState.matches.isNotEmpty()) _affectionsScreenState.matches.clear()
                    for (pair in it.second) {
                        firebaseMainRepositoryImpl.getUserInfo(pair.first).collect { matchInfo ->
                            val matchPair = Pair(matchInfo, pair.second)
                            if (!_affectionsScreenState.matches.contains(matchPair))
                                _affectionsScreenState.matches.add(matchPair)
                        }
                    }
                    _affectionsScreenState.isLoading.value = false
                }
        }
    }

    fun addLike(otherUserInfo: UserInfo) {
        viewModelScope.launch {
            firebaseMainRepositoryImpl.addLike(_sharedState.myUserInfo.value, otherUserInfo)
                .collect {
                    if (it) println("Likes me added")
                }
        }
    }


    private val _conversationsScreenState = ConversationsScreenState()
    val conversationsScreenState = _conversationsScreenState

    fun getConversations() {
        println("getting conversations")
        _conversationsScreenState.isLoading.value = true
        viewModelScope.launch {
            firebaseMainRepositoryImpl.getConversations().collect {
                if (_conversationsScreenState.conversations.isNotEmpty())
                    _conversationsScreenState.conversations.clear()
                _conversationsScreenState.conversations.addAll(it)
                _conversationsScreenState.isLoading.value = false
            }
        }
    }

    private val _messagesScreenState = MessagesScreenState()
    val messagesScreenState = _messagesScreenState

    fun listenForMessages(otherUid: String) {
        _messagesScreenState.isLoading.value = true
        viewModelScope.launch {
            firebaseMainRepositoryImpl.listenForMessages(
                _sharedState.myUserInfo.value.uid,
                otherUid
            )
                .collect {
                    if (_messagesScreenState.messages.isNotEmpty()) _messagesScreenState.messages.clear()
                    _messagesScreenState.messages.addAll(it)
                    _messagesScreenState.isLoading.value = false
                }
        }
    }

    fun sendTextMessage(message: TextMessage) {
        viewModelScope.launch {
            firebaseMainRepositoryImpl.sendTextMessage(
                _sharedState.myUserInfo.value,
                _sharedState.otherUserInfo.value,
                message
            ).collect {
                println("Send text message success? $it")
            }
        }
    }

    fun sendImageMessage(message: ImageMessage, image: Uri) {
        viewModelScope.launch {
            firebaseMainRepositoryImpl.sendImageMessage(
                _sharedState.myUserInfo.value,
                _sharedState.otherUserInfo.value,
                message,
                image
            ).collect {
                println("Send image message success? $it")
            }
        }
    }

    private val _editFavoritesScreenState = EditFavoritesScreenState()
    val editMoviesScreenState = _editFavoritesScreenState

    private var hasBeenAdded = false
    fun addFavoritesMoviesToEdit() {
        if (hasBeenAdded) return
        _editFavoritesScreenState.favoriteMovies.addAll(sharedState.myUserInfo.value.favoriteMovies)
        hasBeenAdded = true

    }

    private var hasSongBeenAdded = false
    fun addFavoriteSongsToEdit() {
        if (hasSongBeenAdded) return
        _editFavoritesScreenState.favoriteSongs.addAll(sharedState.myUserInfo.value.favoriteTracks)
        hasSongBeenAdded = true

    }

    private var hasBeenCalled = false
    fun getTop100() {
        if (hasBeenCalled) return
        hasBeenCalled = true
        _editFavoritesScreenState.isLoading.value = true
        viewModelScope.launch {
            try {
                top100Repository.getTop100().collect {
                    println(it)
                    if (_editFavoritesScreenState.top100Movies.isNotEmpty())
                        _editFavoritesScreenState.top100Movies.clear()
                    _editFavoritesScreenState.top100Movies.addAll(it)
                }
                _editFavoritesScreenState.isLoading.value = false

            } catch (e: Exception) {
                _editFavoritesScreenState.isLoading.value = false
                println("Error!!!!!!!!!!!  " + e.message)
            }
        }
    }

    private var hasGetSongsBeenCalled = false
    fun getSpotifyTop200() {
        if (hasGetSongsBeenCalled) return
        hasGetSongsBeenCalled = true
        _editFavoritesScreenState.isLoading.value = true
        viewModelScope.launch {
            try {
                spotifyRepository.getSpotifyTop200().collect { response ->
                    if (_editFavoritesScreenState.top200Songs.isNotEmpty())
                        _editFavoritesScreenState.top200Songs.clear()
                    _editFavoritesScreenState.top200Songs.addAll(response.map {
                        it.trackMetadata.id = it.trackMetadata.trackUri.substringAfterLast(":")
                        it.trackMetadata
                    })
                }
                _editFavoritesScreenState.isLoading.value = false

            } catch (e: Exception) {
                _editFavoritesScreenState.isLoading.value = false
                println("Error!!!!!!!!!!!  " + e.message)
            }
        }
    }

    fun searchSongsByTitle(title: String) {
        if (title.isBlank()) {
            if (_editFavoritesScreenState.top200Songs.isNotEmpty())
                _editFavoritesScreenState.top200Songs.clear()
        } else {
            if (_editFavoritesScreenState.searchedSongs.isNotEmpty())
                _editFavoritesScreenState.searchedSongs.clear()
        }
        _editFavoritesScreenState.isLoading.value = true
        viewModelScope.launch {
            try {
                if (title.isBlank()) {
                    spotifyRepository.getSpotifyTop200().collect { result ->
                        _editFavoritesScreenState.top200Songs.addAll(result.map { it.trackMetadata })
                    }
                } else {
                    spotifyRepository.getSpotifySongsByTitle(title).collect { result ->
                        println(result)
                        _editFavoritesScreenState.searchedSongs.addAll(result.tracks.map {
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
                _editFavoritesScreenState.isLoading.value = false

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
                    println(it)
                    _songDetailsScreenState.songDetails.value = it.tracks[0]
                }
                _songDetailsScreenState.isLoading.value = false

            } catch (e: Exception) {
                println("Error!!!!!!!!!!!  " + e.message)
            }
        }
    }

    fun getMovies(searchValue: String) {
        editMoviesScreenState.isLoading.value = true
        viewModelScope.launch {
            try {
                if (searchValue == "") {
                    top100Repository.getTop100().collect {
                        if (_editFavoritesScreenState.top100Movies.isNotEmpty())
                            _editFavoritesScreenState.top100Movies.clear()
                        _editFavoritesScreenState.top100Movies.addAll(it)
                    }
                } else {
                    repository.getMovieDetailsFromTitle(searchValue).collect {
                        if (_editFavoritesScreenState.searchedMovies.isNotEmpty())
                            _editFavoritesScreenState.searchedMovies.clear()
                        _editFavoritesScreenState.searchedMovies.addAll(it)
                    }
                }
                editMoviesScreenState.isLoading.value = false
            } catch (e: Exception) {
                println("Error!!!!!!!!!!!  " + e.message)
                editMoviesScreenState.isLoading.value = false
            }
        }
    }
}
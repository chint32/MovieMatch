package cotey.hinton.moviedate.feature_auth.presentation.screens.select_favorites

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.firebase.auth.AuthResult
import cotey.hinton.moviedate.feature_auth.domain.models.TrackMetaData
import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import cotey.hinton.moviedate.feature_auth.domain.models.TrackDetails

data class SelectFavoritesState @OptIn(ExperimentalPagerApi::class) constructor(
    val top100Movies: SnapshotStateList<Movie> = mutableStateListOf<Movie>(),
    val searchedMovies: SnapshotStateList<Movie> = mutableStateListOf<Movie>(),
    val favoriteMovies: SnapshotStateList<Movie> = mutableStateListOf<Movie>(),
    val top200Songs: SnapshotStateList<TrackMetaData> = mutableStateListOf(),
    val searchedSongs: SnapshotStateList<TrackMetaData> = mutableStateListOf(),
    val favoriteSongs: SnapshotStateList<TrackMetaData> = mutableStateListOf(),
    var songDetails: MutableState<TrackDetails?> = mutableStateOf(null),
    val searchText: MutableState<String> = mutableStateOf(""),
    var pagerState: PagerState = PagerState(2),
    var isLoading : MutableState<Boolean> = mutableStateOf(false),
    var authResult: MutableState<AuthResult?> = mutableStateOf(null),
    var error : MutableState<String?> = mutableStateOf(null),
    var registrationSuccessAnim1: MutableTransitionState<Boolean> =
        MutableTransitionState(false).apply {
            targetState = false
        },
    var registrationSuccessAnim2: MutableTransitionState<Boolean> =
        MutableTransitionState(false).apply {
            targetState = false
        },
)
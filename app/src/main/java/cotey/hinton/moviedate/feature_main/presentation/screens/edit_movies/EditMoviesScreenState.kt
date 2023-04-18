package cotey.hinton.moviedate.feature_main.presentation.screens.edit_movies

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import cotey.hinton.moviedate.feature_auth.domain.models.TrackMetaData
import cotey.hinton.moviedate.feature_auth.domain.models.Movie

data class EditMoviesScreenState @OptIn(ExperimentalPagerApi::class) constructor(
    val top100Movies: SnapshotStateList<Movie> = mutableStateListOf(),
    val searchedMovies: SnapshotStateList<Movie> = mutableStateListOf(),
    val favoriteMovies: SnapshotStateList<Movie> = mutableStateListOf(),
    val top200Songs: SnapshotStateList<TrackMetaData> = mutableStateListOf(),
    val searchedSongs: SnapshotStateList<TrackMetaData> = mutableStateListOf(),
    val favoriteSongs: SnapshotStateList<TrackMetaData> = mutableStateListOf(),
    val searchText: MutableState<String> = mutableStateOf(""),
    var isLoading : MutableState<Boolean> = mutableStateOf(false),
    var pagerState: PagerState = PagerState(2),
)

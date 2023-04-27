package cotey.hinton.moviedate.feature_main.presentation.screens.edit_movies

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cotey.hinton.moviedate.feature_auth.domain.models.TrackMetaData
import cotey.hinton.moviedate.feature_auth.domain.models.Movie

data class EditFavoritesScreenState constructor(
    val top100Movies: SnapshotStateList<Movie> = mutableStateListOf(),
    val searchedMovies: SnapshotStateList<Movie> = mutableStateListOf(),
    val favoriteMovies: SnapshotStateList<Movie> = mutableStateListOf(),
    val top200Songs: SnapshotStateList<TrackMetaData> = mutableStateListOf(),
    val searchedSongs: SnapshotStateList<TrackMetaData> = mutableStateListOf(),
    val favoriteSongs: SnapshotStateList<TrackMetaData> = mutableStateListOf(),
    var isLoading : MutableState<Boolean> = mutableStateOf(false),
)

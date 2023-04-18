package cotey.hinton.moviedate.feature_main.presentation.screens.song_details

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cotey.hinton.moviedate.feature_auth.domain.models.TrackDetails

data class SongDetailsState(
    var songDetails: MutableState<TrackDetails?> = mutableStateOf(null),
    var isLoading : MutableState<Boolean> = mutableStateOf(false),
)

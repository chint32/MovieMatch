package cotey.hinton.moviedate.feature_main.presentation.screens.shared.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo

data class SharedState(
    val myUserInfo: MutableState<UserInfo> = mutableStateOf(UserInfo()),
    val otherUserInfo: MutableState<UserInfo> = mutableStateOf(UserInfo()),
    val moviesToAdd: SnapshotStateList<Movie> = mutableStateListOf(),
    val moviesToRemove: SnapshotStateList<Movie> = mutableStateListOf()
)

package cotey.hinton.moviedate.feature_main.presentation.screens.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo

data class MainScreenState(
    val users: SnapshotStateList<Pair<UserInfo, Int>> = mutableStateListOf(),
    val isLoading: MutableState<Boolean> = mutableStateOf(false),
    val isLoaded: MutableState<Boolean> = mutableStateOf(false)
)

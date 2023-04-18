package cotey.hinton.moviedate.feature_auth.presentation.screens.shared

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo

data class SharedState (
    val userInfo: MutableState<UserInfo> = mutableStateOf(UserInfo()),
        )
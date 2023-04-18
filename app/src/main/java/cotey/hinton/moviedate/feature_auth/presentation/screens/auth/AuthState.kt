package cotey.hinton.moviedate.feature_auth.presentation.screens.auth

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.firebase.auth.AuthResult

data class AuthState @OptIn(ExperimentalPagerApi::class) constructor(
    var email: MutableState<String> = mutableStateOf(""),
    var pw: MutableState<String> = mutableStateOf(""),
    var showPw : MutableState<Boolean> = mutableStateOf(false),
    var pagerState: PagerState = PagerState(2),
    var isLoading : MutableState<Boolean> = mutableStateOf(false),
    var authResult: MutableState<AuthResult?> = mutableStateOf(null),
    var error : MutableState<String?> = mutableStateOf(null),
    var loginSuccessAnim1: MutableTransitionState<Boolean> =
        MutableTransitionState(false).apply {
            targetState = false
    },
    var loginSuccessAnim2: MutableTransitionState<Boolean> =
        MutableTransitionState(false).apply {
            targetState = false
    }
)

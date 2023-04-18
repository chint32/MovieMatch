package cotey.hinton.moviedate.feature_main.presentation.screens.affections

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo

data class AffectionsScreenState @OptIn(ExperimentalPagerApi::class) constructor(
    val matches: SnapshotStateList<Pair<UserInfo, Boolean>> = mutableStateListOf(),
    val likesMe: SnapshotStateList<Pair<UserInfo, Boolean>> = mutableStateListOf(),
    var pagerState: PagerState = PagerState(2),
    var isLoading : MutableState<Boolean> = mutableStateOf(false),
)

package cotey.hinton.moviedate.feature_main.presentation.screens.profile_details

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState

data class ProfileDetailsScreenState @OptIn(ExperimentalPagerApi::class) constructor(
    val isEditMode: MutableState<Boolean> =  mutableStateOf(false),
    var pagerState: PagerState = PagerState(pageCount = 0),
    val bitmap: MutableState<Bitmap?> = mutableStateOf(null),
    val uriList: SnapshotStateList<Uri?> = mutableStateListOf(null, null, null)
)

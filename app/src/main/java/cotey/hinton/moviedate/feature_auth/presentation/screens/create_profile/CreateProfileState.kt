package cotey.hinton.moviedate.feature_auth.presentation.screens.create_profile

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
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo

data class CreateProfileState @OptIn(ExperimentalPagerApi::class) constructor(
    val images: SnapshotStateList<Uri?> = mutableStateListOf(null, null, null),
)

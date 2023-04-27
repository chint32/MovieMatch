package cotey.hinton.moviedate.feature_main.presentation.screens.affections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.ui.theme.Pink
import cotey.hinton.moviedate.util.WindowSizeClass

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AffectionsItem(
    windowSizeClass: WindowSizeClass,
    isNewMatch: Boolean,
    otherUserInfo: UserInfo,
    viewModel: MainViewModel,
    navController: NavController
) {
    val matchPercent =
        calculateMatchPercentage(viewModel.sharedState.myUserInfo.value, otherUserInfo)
    val bgColor = if (isNewMatch) Pink.copy(.7f) else Color.DarkGray.copy(.7f)
    val imageSize = if (windowSizeClass == WindowSizeClass.COMPACT) 80.dp else 140.dp
    val fontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 18.sp else 28.sp

    Box(
        Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))) {
        Row(
            modifier = Modifier
                .background(bgColor)
                .fillMaxSize()
                .padding(10.dp)
                .clickable {
                    viewModel.sharedState.otherUserInfo.value = otherUserInfo
                    navController.navigate(
                        Screens.ProfileDetailsScreen.route
                                + "?isMyProfile=${false}"
                                + "&isMatch=${true}",
                    )
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlideImage(
                model = otherUserInfo.images[0],
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                text = otherUserInfo.screenName + "(${(matchPercent * 100).toInt()}%)",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontSize = fontSize
            )
        }
    }
}

private fun calculateMatchPercentage(myUserInfo: UserInfo, otherUserInfo: UserInfo): Float {

    var numFavoritesInCommon = 0
    for (i in myUserInfo.favoriteMovies.indices) {
        for (j in otherUserInfo.favoriteMovies.indices) {
            if (myUserInfo.favoriteMovies[i] == otherUserInfo.favoriteMovies[j]) {
                numFavoritesInCommon++
                break
            }
        }
    }
    for (i in myUserInfo.favoriteTracks.indices) {
        for (j in otherUserInfo.favoriteTracks.indices) {
            if (myUserInfo.favoriteTracks[i] == otherUserInfo.favoriteTracks[j]) {
                numFavoritesInCommon++
                break
            }
        }
    }
    return numFavoritesInCommon.toFloat() / (myUserInfo.favoriteMovies.size + myUserInfo.favoriteTracks.size)
}

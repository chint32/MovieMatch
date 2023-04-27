package cotey.hinton.moviedate.feature_main.presentation.screens.view_movies

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.domain.models.TrackMetaData
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.ui.theme.Pink
import cotey.hinton.moviedate.util.WindowSizeClass

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MainSongItem(
    windowSizeClass: WindowSizeClass,
    track: TrackMetaData,
    viewModel: MainViewModel,
    navController: NavController
) {
    val isFavorite = remember {
        mutableStateOf(viewModel.sharedState.myUserInfo.value.favoriteTracks.contains(track))
    }
    val iconSize = if(windowSizeClass == WindowSizeClass.COMPACT) 24.dp else 40.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {

        GlideImage(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    viewModel.getSongDetails(track.id)
                    navController.navigate(
                        Screens.SongDetailsScreen.route +
                                "?trackImage=${track.displayImageUri}"
                    )
                },
            model = track.displayImageUri,
            contentDescription = "Movie poster",
            contentScale = ContentScale.Crop
        )

        Row(Modifier.fillMaxWidth(.96f), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = {
                if (viewModel.sharedState.myUserInfo.value.favoriteTracks.contains(track))
                    viewModel.sharedState.myUserInfo.value.favoriteTracks.remove(track)
                else viewModel.sharedState.myUserInfo.value.favoriteTracks.add(track)
                viewModel.updateUserInfo(viewModel.sharedState.myUserInfo.value, null, null)
                isFavorite.value = viewModel.sharedState.myUserInfo.value.favoriteTracks.contains(track)
            }) {
                Icon(
                    imageVector = if (isFavorite.value) Icons.Outlined.Star
                    else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = Pink,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

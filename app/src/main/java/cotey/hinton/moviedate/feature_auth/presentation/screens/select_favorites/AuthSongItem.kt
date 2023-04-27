package cotey.hinton.moviedate.feature_auth.presentation.screens.select_favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.gson.Gson
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.domain.models.TrackMetaData
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.ui.theme.Pink

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AuthSongItem(
    fontSize: TextUnit,
    iconSize: Dp,
    song: TrackMetaData,
    favoriteSongs: SnapshotStateList<TrackMetaData>,
    viewModel: AuthViewModel,
    navController: NavController,
    showFavoriteIcon: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.BottomCenter
    ) {
        GlideImage(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (showFavoriteIcon) {
                        viewModel.getSongDetails(song.id)
                        navController.navigate(
                            Screens.SongDetailsScreen.route +
                                    "?trackImage=${song.displayImageUri}"
                        )
                    } else {
                        if (!favoriteSongs.contains(song))
                            favoriteSongs.add(song)
                        else favoriteSongs.remove(song)
                    }
                }
                .drawWithCache {
                    val gradient = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black),
                        startY = size.height / 3,
                        endY = size.height
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(gradient, blendMode = BlendMode.Multiply)
                    }
                },
            model = song.displayImageUri,
            contentDescription = "Song poster",
            contentScale = ContentScale.Crop
        )

        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            Text(
                text = song.trackName,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = fontSize,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = song.artists[0].name,
                modifier = Modifier.fillMaxWidth(),
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter){
        if (showFavoriteIcon) {
            Row(
                Modifier.fillMaxWidth().padding(20.dp, 20.dp),
                horizontalArrangement = Arrangement.End) {
                IconButton(onClick = {
                    if (favoriteSongs.contains(song))
                        favoriteSongs.remove(song)
                    else favoriteSongs.add(song)
                }) {
                    Icon(
                        imageVector = if (favoriteSongs.contains(song)) Icons.Outlined.Favorite
                        else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = Pink,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}

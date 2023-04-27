package cotey.hinton.moviedate.feature_main.presentation.screens.song_details

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.feature_main.presentation.screens.song_details.audio_visualizer.AudioPlayer
import cotey.hinton.moviedate.feature_main.presentation.screens.song_details.audio_visualizer.VisualizerComputer
import cotey.hinton.moviedate.feature_main.presentation.screens.song_details.audio_visualizer.VisualizerData
import cotey.hinton.moviedate.ui.theme.Pink
import cotey.hinton.moviedate.util.WindowSizeClass

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SongDetailsScreen(
    windowSizeClass: WindowSizeClass,
    songDetailsState: SongDetailsState,
    trackImage: String
) {

    val context = LocalContext.current
    val audioPlayer = AudioPlayer()
    VisualizerComputer.setupPermissions(context.getActivity()!!)
    val fontSize = if(windowSizeClass == WindowSizeClass.COMPACT) 20.sp else 30.sp
    val contentAlpha = if (songDetailsState.isLoading.value) .5f else 1f
    val imageSize = if(windowSizeClass == WindowSizeClass.COMPACT) 300.dp else 500.dp

    // clean up audio player on decomposition
    DisposableEffect(audioPlayer){
        onDispose {
            audioPlayer.stop()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {


        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.TopCenter
        ) {

            GlideImage(
                model = trackImage,
                contentDescription = "Song poster",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageSize)
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
                contentScale = ContentScale.FillBounds
            )

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    if (songDetailsState.songDetails.value != null) {

                        if(windowSizeClass != WindowSizeClass.COMPACT)
                            Spacer(modifier = Modifier.height(100.dp))

                        Text(
                            text = songDetailsState.songDetails.value!!.album.name,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = fontSize
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = songDetailsState.songDetails.value!!.album.artists.joinToString { it.name },
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray,
                            fontSize = fontSize
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = songDetailsState.songDetails.value!!.album.release_date,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray,
                            fontSize = fontSize
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        VideoPlayer(audioPlayer, songDetailsState.songDetails.value!!.preview_url)
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
        if(songDetailsState.isLoading.value)
            ProgressIndicatorClickDisabled()
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(audioPlayer: AudioPlayer, url: String) {

    val visualizerData = remember { mutableStateOf(VisualizerData()) }
    val (isPlaying, setPlaying) = remember { mutableStateOf(false) }
    Content(isPlaying, setPlaying, visualizerData)
    if (isPlaying) {
        audioPlayer.play(url, visualizerData)
    } else {
        audioPlayer.stop()
    }
}


@Composable
fun Content(
    isPlaying: Boolean,
    setPlaying: (Boolean) -> Unit,
    visualizerData: MutableState<VisualizerData>
) {

    Box(contentAlignment = Alignment.Center) {

        FancyTubularStackedBarEqualizer(
            Modifier
                .fillMaxWidth(.7f)
                .aspectRatio(1.6f)
                .padding(vertical = 4.dp),
            data = visualizerData.value,
            barCount = 48,
            maxStackCount = 16,
        )
        FloatingActionButton(
            onClick = {
                setPlaying(!isPlaying)
            },
            modifier = Modifier
                .padding(2.dp),
            backgroundColor = Pink
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

fun Context.getActivity(): ComponentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is ComponentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

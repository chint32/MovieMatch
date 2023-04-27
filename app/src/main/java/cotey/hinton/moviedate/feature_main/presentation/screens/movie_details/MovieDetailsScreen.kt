package cotey.hinton.moviedate.feature_main.presentation.screens.movie_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import cotey.hinton.moviedate.util.WindowSizeClass


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieDetailsScreen(
    windowSizeClass: WindowSizeClass,
    movieJsonString: String
) {
    val movie = Gson().fromJson(movieJsonString, Movie::class.java)
    val fontSize = if(windowSizeClass == WindowSizeClass.COMPACT) 18.sp else 28.sp
    val imageHeight = if(windowSizeClass == WindowSizeClass.COMPACT) 275.dp else 400.dp
    Column(
        Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GlideImage(
            model = movie.image,
            contentDescription = "Movie poster",
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = "${movie.year} | ${
                movie.genre.toString().substring(1, movie.genre.toString().length - 1)
            } | ${movie.rating}/10",
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            textAlign = TextAlign.Center,
            fontSize = fontSize
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Summary",
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Start,
            fontSize = fontSize
        )
        Text(
            text = movie.description,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            textAlign = TextAlign.Start,
            fontSize = fontSize
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Directors",
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Start,
            fontSize = fontSize
        )
        Text(
            text = movie.director.toString()
                .substring(1, movie.director.toString().length - 1),
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            textAlign = TextAlign.Start,
            fontSize = fontSize
        )
        Spacer(modifier = Modifier.height(10.dp))
        AndroidView(
            factory = {
                val view = YouTubePlayerView(it)
                view.addYouTubePlayerListener(
                    object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            super.onReady(youTubePlayer)
                            youTubePlayer.loadVideo(
                                movie.trailer.substringAfter("embed/"),
                                0f
                            )
                        }
                    }
                )
                view
            },
            update = { view ->
                // Update the view
            }
        )
        Spacer(modifier = Modifier.height(80.dp))
    }
}


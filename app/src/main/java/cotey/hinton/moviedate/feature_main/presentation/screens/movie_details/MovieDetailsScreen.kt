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
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import cotey.hinton.moviedate.feature_auth.domain.models.Movie


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieDetailsScreen(
    movieJsonString: String
) {
    val movie = Gson().fromJson(movieJsonString, Movie::class.java)

    Column(
        Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GlideImage(
            model = movie.image,
            contentDescription = "Movie poster",
            modifier = Modifier
                .fillMaxWidth()
                .height(275.dp),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = "${movie.year} | ${
                movie.genre.toString().substring(1, movie.genre.toString().length - 1)
            } | ${movie.rating}/10",
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Summary",
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Start,
        )
        Text(
            text = movie.description,
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            textAlign = TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Directors",
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            textAlign = TextAlign.Start,
        )
        Text(
            text = movie.director.toString()
                .substring(1, movie.director.toString().length - 1),
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            textAlign = TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(10.dp))
        AndroidView(
            factory = {
                val view = YouTubePlayerView(it)
                val fragment = view.addYouTubePlayerListener(
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


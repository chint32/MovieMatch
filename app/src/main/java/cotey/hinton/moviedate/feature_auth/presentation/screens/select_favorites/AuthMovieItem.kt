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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.gson.Gson
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.ui.theme.Pink

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AuthMovieItem(
    iconSize: Dp,
    movie: Movie,
    favoriteMovies: SnapshotStateList<Movie>,
    navController: NavController,
    showFavoriteIcon: Boolean
) {
    val movieJsonString = Gson().toJson(movie).toString()
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
                    if (showFavoriteIcon) {
                        navController.navigate(
                            Screens.MovieDetailsScreen.route +
                                    "?movieJsonString=$movieJsonString"
                        )
                    } else {
                        if (!favoriteMovies.contains(movie)) favoriteMovies.add(movie)
                        else favoriteMovies.remove(movie)
                    }
                },
            model = movie.image,
            contentDescription = "Movie poster",
            contentScale = ContentScale.Crop
        )

        if (showFavoriteIcon) {
            Row(Modifier.fillMaxWidth(.96f), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = {
                    if (favoriteMovies.contains(movie))
                       favoriteMovies.remove(movie)
                    else favoriteMovies.add(movie)
                }) {
                    Icon(
                        imageVector = if (favoriteMovies.contains(movie)) Icons.Outlined.Favorite
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
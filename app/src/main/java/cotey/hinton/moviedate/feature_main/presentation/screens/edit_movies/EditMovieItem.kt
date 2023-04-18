package cotey.hinton.moviedate.feature_main.presentation.screens.edit_movies

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
import com.google.gson.Gson
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.ui.theme.Pink

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EditMovieItem(
    movie: Movie,
    viewModel: MainViewModel,
    navController: NavController,
    showFavoriteIcon: Boolean
) {

    Box(
        modifier = if(showFavoriteIcon)
            Modifier.fillMaxSize().padding(10.dp).clip(RoundedCornerShape(20.dp))
        else
            Modifier.width(100.dp).height(160.dp).padding(4.dp).clip(RoundedCornerShape(10.dp))
    ) {

        GlideImage(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (showFavoriteIcon) {
                        navController.navigate(
                            Screens.MovieDetailsScreen.route +
                                    "?movieJsonString=${Gson().toJson(movie)}"
                        )
                    } else {
                        if (!viewModel.editMoviesScreenState.favoriteMovies.contains(movie))
                            viewModel.editMoviesScreenState.favoriteMovies.add(movie)
                        else viewModel.editMoviesScreenState.favoriteMovies.remove(movie)
                    }
                },
            model = movie.image,
            contentDescription = "Movie poster",
            contentScale = ContentScale.Crop
        )
        if (showFavoriteIcon) {
            Row(Modifier.fillMaxWidth(.96f), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = {
                    if (viewModel.editMoviesScreenState.favoriteMovies.contains(movie))
                        viewModel.editMoviesScreenState.favoriteMovies.remove(movie)
                    else viewModel.editMoviesScreenState.favoriteMovies.add(movie)
                }) {
                    Icon(
                        imageVector = if (viewModel.editMoviesScreenState.favoriteMovies.contains(movie)) Icons.Outlined.Star
                        else Icons.Outlined.StarBorder,
                        contentDescription = null,
                        tint = Pink
                    )
                }
            }
        }
    }
}


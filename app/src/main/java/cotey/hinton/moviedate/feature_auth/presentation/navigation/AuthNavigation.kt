package cotey.hinton.moviedate.feature_auth.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.presentation.screens.create_profile.CreateProfileScreen
import cotey.hinton.moviedate.feature_auth.presentation.screens.auth.AuthScreen
import cotey.hinton.moviedate.feature_auth.presentation.screens.select_favorites.SelectFavoritesScreen
import cotey.hinton.moviedate.feature_auth.presentation.screens.shared.SharedState
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.feature_main.presentation.screens.movie_details.MovieDetailsScreen
import cotey.hinton.moviedate.feature_main.presentation.screens.song_details.SongDetailsScreen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AuthNavigation(navController: NavHostController, paddingValues: PaddingValues) {
    val viewModel: AuthViewModel = hiltViewModel()


    NavHost(
        navController = navController,
        startDestination = Screens.AuthScreen.route,
    ) {
        composable(route = Screens.AuthScreen.route) {
            AuthScreen(navController, viewModel)
        }
        composable(route = Screens.CreateProfileScreen.route) {
            CreateProfileScreen(navController, viewModel)
        }
        composable(route = Screens.SelectFavoritesScreen.route){
            SelectFavoritesScreen(navController, viewModel)
        }
        composable(route = Screens.MovieDetailsScreen.route +
                "?movieJsonString={movieJsonString}",
            arguments = listOf(
                navArgument(name = "movieJsonString") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        )
        {
            val movieJsonString = it.arguments?.getString("movieJsonString")!!
            MovieDetailsScreen(movieJsonString = movieJsonString)
        }
        composable(route = Screens.SongDetailsScreen.route +
                "?trackImage={trackImage}",
            arguments = listOf(
                navArgument(name = "trackImage") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        )
        {
            val trackImage = it.arguments?.getString("trackImage")!!
            SongDetailsScreen(viewModel.songDetailsState, trackImage)
        }
    }
}
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
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.presentation.screens.auth.AuthScreen
import cotey.hinton.moviedate.feature_auth.presentation.screens.create_profile.CreateProfileScreen
import cotey.hinton.moviedate.feature_auth.presentation.screens.select_favorites.SelectFavoritesScreen
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.feature_main.presentation.screens.movie_details.MovieDetailsScreen
import cotey.hinton.moviedate.feature_main.presentation.screens.song_details.SongDetailsScreen
import cotey.hinton.moviedate.util.WindowSizeClass

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AuthNavigation(windowSizeClass: WindowSizeClass, navController: NavHostController, paddingValues: PaddingValues) {
    val viewModel: AuthViewModel = hiltViewModel()


    NavHost(
        navController = navController,
        startDestination = Screens.AuthScreen.route,
    ) {
        composable(route = Screens.AuthScreen.route) {
            AuthScreen(windowSizeClass, navController, viewModel)
        }
        composable(route = Screens.CreateProfileScreen.route) {
            CreateProfileScreen(windowSizeClass, navController, viewModel)
        }
        composable(route = Screens.SelectFavoritesScreen.route){
            SelectFavoritesScreen(windowSizeClass, navController, viewModel)
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
            MovieDetailsScreen(windowSizeClass, movieJsonString)
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
            SongDetailsScreen(windowSizeClass, viewModel.songDetailsState, trackImage)
        }
    }
}
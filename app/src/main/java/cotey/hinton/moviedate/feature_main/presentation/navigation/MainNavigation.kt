package cotey.hinton.moviedate.feature_main.presentation.navigation

import android.annotation.SuppressLint
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
import cotey.hinton.moviedate.feature_main.presentation.screens.main.MainScreen
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.feature_main.presentation.screens.conversations.ConversationsScreen
import cotey.hinton.moviedate.feature_main.presentation.screens.affections.AffectionsScreen
import cotey.hinton.moviedate.feature_main.presentation.screens.messages.MessagesScreen
import cotey.hinton.moviedate.feature_main.presentation.screens.edit_movies.EditFavoritesScreen
import cotey.hinton.moviedate.feature_main.presentation.screens.movie_details.MovieDetailsScreen
import cotey.hinton.moviedate.feature_main.presentation.screens.profile_details.ProfileDetailsScreen
import cotey.hinton.moviedate.feature_main.presentation.screens.song_details.SongDetailsScreen
import cotey.hinton.moviedate.feature_main.presentation.screens.view_movies.ViewMoviesScreen
import cotey.hinton.moviedate.util.WindowSizeClass

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MainNavigation(windowSizeClass: WindowSizeClass, navController: NavHostController, paddingValues: PaddingValues) {
    val mainViewModel: MainViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screens.MainScreen.route,
    ) {
        composable(route = Screens.MatchesScreen.route) {
            AffectionsScreen(
                windowSizeClass,
                navController,
                mainViewModel
            )
        }

        composable(route = Screens.ConversationsScreen.route) {
            ConversationsScreen(
                windowSizeClass,
                navController,
                mainViewModel
            )
        }

        composable(route = Screens.EditMoviesScreen.route) {
            EditFavoritesScreen(
                windowSizeClass,
                navController,
                mainViewModel
            )
        }

        composable(route = Screens.MainScreen.route) {
            MainScreen(
                windowSizeClass,
                navController = navController,
                mainViewModel
            )
        }

        composable(
            route = Screens.ProfileDetailsScreen.route
                    + "?isMyProfile={isMyProfile}"
                    + "&isMatch={isMatch}",
            arguments = listOf(
                navArgument(
                    name = "isMyProfile"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument(
                    name = "isMatch"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) {
            val isMyProfile = it.arguments?.getBoolean("isMyProfile")!!
            val isMatch = it.arguments?.getBoolean("isMatch")!!
            ProfileDetailsScreen(
                windowSizeClass,
                navController,
                mainViewModel,
                isMyProfile,
                isMatch
            )
        }

        composable(route = Screens.MessagesScreen.route) {
            MessagesScreen(windowSizeClass, mainViewModel)
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
            SongDetailsScreen(windowSizeClass, mainViewModel.songDetailsState, trackImage)
        }

        composable(
            route = Screens.ViewMoviesScreen.route +
                    "?isMyProfile={isMyProfile}",
            arguments = listOf(
                navArgument(
                    name = "isMyProfile"
                ) {
                    type = NavType.BoolType
                    defaultValue = false
                },
            )
        ) {
            val isMyProfile = it.arguments?.getBoolean("isMyProfile")!!
            ViewMoviesScreen(
                windowSizeClass,
                mainViewModel,
                navController,
                isMyProfile
            )
        }
    }
}
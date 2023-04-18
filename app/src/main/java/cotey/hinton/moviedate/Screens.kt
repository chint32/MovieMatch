package cotey.hinton.moviedate

sealed class Screens(val route: String) {
    object AuthScreen : Screens("login_screen")
    object CreateProfileScreen : Screens("create_profile_screen")
    object SelectFavoritesScreen : Screens("select_favorites_screen")
    object ConversationsScreen : Screens("conversations_screen")
    object MainScreen : Screens("main_screen")
    object MatchesScreen : Screens("matches_screen")
    object ProfileDetailsScreen : Screens("profile_details_screen")
    object MessagesScreen : Screens("messages_screen")
    object MovieDetailsScreen : Screens("movie_details_screen")
    object SongDetailsScreen : Screens("song_details_screen")
    object EditMoviesScreen : Screens("edit_movies_screen")
    object ViewMoviesScreen : Screens("view_movies_screen")
}
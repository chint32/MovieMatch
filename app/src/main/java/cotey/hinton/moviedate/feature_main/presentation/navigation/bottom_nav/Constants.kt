package cotey.hinton.moviedate.feature_main.presentation.navigation.bottom_nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import cotey.hinton.moviedate.Screens

object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Messages",
            icon = Icons.Filled.Email,
            route = Screens.ConversationsScreen.route
        ),
        BottomNavItem(
            label = "Home",
            icon = Icons.Filled.Home,
            route = Screens.MainScreen.route
        ),
        BottomNavItem(
            label = "Matches",
            icon = Icons.Filled.Favorite,
            route = Screens.MatchesScreen.route
        )
    )
}
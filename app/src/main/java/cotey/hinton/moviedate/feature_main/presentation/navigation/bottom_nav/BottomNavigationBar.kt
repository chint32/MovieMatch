package cotey.hinton.moviedate.feature_main.presentation.navigation.bottom_nav

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import cotey.hinton.moviedate.ui.theme.Pink
import cotey.hinton.moviedate.util.WindowSizeClass

@Composable
fun BottomNavigationBar(windowSizeClass: WindowSizeClass, navController: NavHostController) {

    BottomNavigation(
        backgroundColor = Color.DarkGray.copy(),
        contentColor = Pink,
        modifier = Modifier.height(if(windowSizeClass == WindowSizeClass.COMPACT) 50.dp else 75.dp)
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Constants.BottomNavItems.forEach { navItem ->
            BottomNavigationItem(
                selected = currentRoute == navItem.route,
                onClick = { navController.navigate(navItem.route) },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.label,
                        modifier = Modifier.size(if(windowSizeClass == WindowSizeClass.COMPACT) 24.dp else 40.dp)
                    )
                },
                label = {
                    Text(text = navItem.label, fontSize = if(windowSizeClass == WindowSizeClass.COMPACT) 16.sp else 26.sp)
                },
                alwaysShowLabel = false
            )
        }
    }
}
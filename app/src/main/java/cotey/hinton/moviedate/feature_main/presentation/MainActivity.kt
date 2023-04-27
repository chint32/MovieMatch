package cotey.hinton.moviedate.feature_main.presentation

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.WindowMetricsCalculator
import com.google.firebase.auth.FirebaseAuth
import cotey.hinton.moviedate.feature_main.presentation.navigation.bottom_nav.BottomNavigationBar
import cotey.hinton.moviedate.feature_main.presentation.navigation.MainNavigation
import cotey.hinton.moviedate.R
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.presentation.AuthActivity
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.feature_main.services.FCMService
import cotey.hinton.moviedate.ui.theme.MovieDateTheme
import cotey.hinton.moviedate.ui.theme.Pink
import cotey.hinton.moviedate.util.WindowSizeClass
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        val window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = (ContextCompat.getColor(this, R.color.black))

        super.onCreate(savedInstanceState)

        setContent {
            val windowSizeClass = calculateWindowSizeClass()
            MovieDateTheme {

                if (FirebaseAuth.getInstance().currentUser == null)
                    LocalContext.current.startActivity(
                        Intent(
                            LocalContext.current,
                            AuthActivity::class.java
                        )
                    )

                startService(Intent(this, FCMService::class.java))

                val navController = rememberNavController()
                var mDisplayMenu by remember { mutableStateOf(false) }
                val authViewModel: AuthViewModel = hiltViewModel()
                val mContext = LocalContext.current
                val fontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 16.sp else 32.sp
                val iconSize = if (windowSizeClass == WindowSizeClass.COMPACT) 24.dp else 48.dp
                val optionsMenuWidth =
                    if (windowSizeClass == WindowSizeClass.COMPACT) 110.dp else 200.dp
                val optionsMenuHeight =
                    if (windowSizeClass == WindowSizeClass.COMPACT) 110.dp else 150.dp
                val optionsMenuPadding =
                    if (windowSizeClass == WindowSizeClass.COMPACT) 0.dp else 10.dp

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        backgroundColor = Color.Black,
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Movie Matcher",
                                        color = Pink,
                                        fontSize = fontSize

                                    )
                                },
                                actions = {
                                    // Creating Icon button for dropdown menu
                                    IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                                        Icon(
                                            Icons.Default.MoreVert,
                                            "",
                                            tint = Pink,
                                            modifier = Modifier.size(iconSize)
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = mDisplayMenu,
                                        onDismissRequest = { mDisplayMenu = false },
                                        modifier = Modifier
                                            .height(optionsMenuHeight)
                                            .width(optionsMenuWidth)
                                    ) {

                                        DropdownMenuItem(onClick = {
                                            mDisplayMenu = false
                                            navController.navigate(
                                                Screens.ProfileDetailsScreen.route +
                                                        "?isMyProfile=${true}" +
                                                        "&isMatch=${false}"
                                            )
                                        }) {
                                            Text(
                                                text = "My Profile",
                                                fontSize = fontSize,
                                                modifier = Modifier.padding(optionsMenuPadding)
                                            )
                                        }
                                        DropdownMenuItem(onClick = {
                                            authViewModel.logout()
                                            mContext.startActivity(
                                                Intent(
                                                    mContext,
                                                    AuthActivity::class.java
                                                )
                                            )
                                        }) {
                                            Text(
                                                text = "Logout",
                                                fontSize = fontSize,
                                                modifier = Modifier.padding(optionsMenuPadding)
                                            )
                                        }
                                    }
                                },

                                backgroundColor = Color.Black,
                                contentColor = Color.White,
                                elevation = 12.dp
                            )
                        },
                        bottomBar = { BottomNavigationBar(windowSizeClass, navController) },
                        content = { paddingValues ->

                            Box(Modifier.fillMaxSize()) {
                                Canvas(
                                    modifier = Modifier
                                        .rotate(55f)
                                        .size(size = 200.dp)
                                ) {
                                    drawRect(
                                        color = Color.Gray.copy(alpha = .20f),
                                        size = Size(width = 190.dp.toPx(), height = 170.dp.toPx()),
                                        topLeft = Offset(x = 90.dp.toPx(), y = -230.dp.toPx()),
                                        style = Fill
                                    )
                                }
                                Canvas(
                                    modifier = Modifier
                                        .rotate(55f)
                                        .size(size = 200.dp)
                                ) {
                                    drawRect(
                                        color = Color.Gray.copy(alpha = .20f),
                                        size = Size(width = 170.dp.toPx(), height = 170.dp.toPx()),
                                        topLeft = Offset(x = 230.dp.toPx(), y = -160.dp.toPx()),
                                        style = Fill
                                    )
                                }
                                MainNavigation(windowSizeClass, navController, paddingValues)

                            }
                        }
                    )
                }
            }
        }
    }

    private fun calculateWindowSizeClass(): WindowSizeClass {
        val metrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)

        val widthDp = metrics.bounds.width() /
                resources.displayMetrics.density
        val widthWindowSizeClass = when {
            widthDp < 600f -> WindowSizeClass.COMPACT
            widthDp < 900f -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }
        return widthWindowSizeClass
    }
}



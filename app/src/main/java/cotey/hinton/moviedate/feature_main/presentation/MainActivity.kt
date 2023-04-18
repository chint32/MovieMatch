package cotey.hinton.moviedate.feature_main.presentation

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import cotey.hinton.moviedate.BottomNavigationBar
import cotey.hinton.moviedate.feature_main.presentation.navigation.MainNavigation
import cotey.hinton.moviedate.R
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.presentation.AuthActivity
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.feature_main.services.FCMService
import cotey.hinton.moviedate.ui.theme.MovieDateTheme
import cotey.hinton.moviedate.ui.theme.Pink
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = (ContextCompat.getColor(this, R.color.black))

        super.onCreate(savedInstanceState)

        setContent {
            MovieDateTheme {

                if(FirebaseAuth.getInstance().currentUser == null)
                    LocalContext.current.startActivity(Intent(LocalContext.current, AuthActivity::class.java))

                startService(Intent(this, FCMService::class.java))

                val navController = rememberNavController()
                var mDisplayMenu by remember { mutableStateOf(false) }
                val authViewModel: AuthViewModel = hiltViewModel()
                val mContext = LocalContext.current

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        // below line we are
                        // creating a top bar.
                        backgroundColor = Color.Black,
                        topBar = {
                            TopAppBar(
                                // in below line we are
                                // adding title to our top bar.
                                title = {
                                    // inside title we are
                                    // adding text to our toolbar.
                                    Text(
                                        text = "Movie Matcher",
                                        // below line is use
                                        // to give text color.
                                        color = Pink
                                    )
                                },
                                actions = {
                                    // Creating Icon button for dropdown menu
                                    IconButton(onClick = { mDisplayMenu = !mDisplayMenu }) {
                                        Icon(Icons.Default.MoreVert, "", tint = Pink)
                                    }

                                    DropdownMenu(
                                        expanded = mDisplayMenu,
                                        onDismissRequest = { mDisplayMenu = false }
                                    ) {

                                        DropdownMenuItem(onClick = {
                                            mDisplayMenu = false
                                            navController.navigate(
                                                Screens.ProfileDetailsScreen.route +
                                                        "?isMyProfile=${true}" +
                                                        "&isMatch=${false}"
                                            )
                                        }) {
                                            Text(text = "My Profile")
                                        }
                                        DropdownMenuItem(onClick = {
                                            authViewModel.logout()
                                            mContext.startActivity(Intent(mContext, AuthActivity::class.java))
                                        }) {
                                            Text(text = "Logout")
                                        }
                                    }
                                },

                                backgroundColor = Color.Black,
                                contentColor = Color.White,
                                elevation = 12.dp
                            )
                        },
                        bottomBar = { BottomNavigationBar(navController = navController) }
                        ,
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
                                MainNavigation(navController, paddingValues)

                            }
                        }
                    )
                }
            }
        }
    }
}



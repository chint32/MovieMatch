package cotey.hinton.moviedate.feature_auth.presentation

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
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
//import androidx.compose.material.*
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
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.WindowMetricsCalculator
import com.google.firebase.auth.FirebaseAuth
import cotey.hinton.moviedate.R
import cotey.hinton.moviedate.feature_auth.presentation.navigation.AuthNavigation
import cotey.hinton.moviedate.feature_main.presentation.MainActivity
import cotey.hinton.moviedate.ui.theme.MovieDateTheme
import cotey.hinton.moviedate.util.WindowSizeClass
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = (ContextCompat.getColor(this, R.color.black))

        setContent {
            val windowSizeClass = calculateWindowSizeClass()
            MovieDateTheme {

                if (FirebaseAuth.getInstance().currentUser != null)
                    startActivity(Intent(LocalContext.current, MainActivity::class.java))
                val navController = rememberNavController()

                Surface( modifier = Modifier.fillMaxSize() ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        backgroundColor = Color.Black,
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
                                AuthNavigation(
                                    windowSizeClass,
                                    navController = navController,
                                    paddingValues = paddingValues
                                )
                            }
                        }
                    )
                }
            }
        }
    }
    private fun calculateWindowSizeClass() : WindowSizeClass {
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


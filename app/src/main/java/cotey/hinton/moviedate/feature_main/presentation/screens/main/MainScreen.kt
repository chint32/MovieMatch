package cotey.hinton.moviedate.feature_main.presentation.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.util.WindowSizeClass

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: MainViewModel
) {
    val contentAlpha = if (viewModel.mainScreenState.isLoading.value) .5f else 1f
    val isEmpty = remember {
        mutableStateOf(true)
    }
    isEmpty.value =
        viewModel.mainScreenState.users.isEmpty()
    Box(Modifier.fillMaxSize()
        .alpha(contentAlpha),
        contentAlignment = Alignment.Center) {


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.92f)
                .padding(20.dp)
                .clip(RoundedCornerShape(10.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            println(isEmpty.value)
            if (!isEmpty.value) {
                CardStack(
                    items = viewModel.mainScreenState.users,
                    onEmptyStack = {
                        isEmpty.value = true
                    },
                    navController = navController,
                    viewModel = viewModel,
                    windowSizeClass = windowSizeClass
                )
            } else {
                Text(text = "No more cards", fontWeight = FontWeight.Bold)
            }
        }
        if(viewModel.mainScreenState.isLoading.value)
            ProgressIndicatorClickDisabled()
    }
}
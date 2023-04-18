package cotey.hinton.moviedate.feature_main.presentation.screens.affections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AffectionsScreen(
    navController: NavController,
    viewModel: MainViewModel
) {

    var hasBeenCalled by remember { mutableStateOf(false) }
    if(!hasBeenCalled) {
        viewModel.getLikesAndMatches()
        hasBeenCalled = true
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(Modifier.fillMaxSize()
            .alpha(if (viewModel.affectionsScreenState.isLoading.value) .5f else 1f),
            Arrangement.SpaceEvenly) {
            Tabs(pagerState = viewModel.affectionsScreenState.pagerState)
            TabsContent(viewModel, navController)
        }
        if(viewModel.affectionsScreenState.isLoading.value)
            ProgressIndicatorClickDisabled()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPagerApi
@Composable
fun Tabs(pagerState: PagerState) {

    val list = listOf("Likes", "Matches")
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 0.dp,
                color = Color.Transparent
            )
        },
    ) {
        list.forEachIndexed { index, _ ->
            Tab(
                text = {
                    if ((pagerState.currentPage == index)) {
                        Text(
                            list[index],
                            color = Color.White,
                            modifier = Modifier.alpha(1f),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    } else {
                        Text(
                            list[index],
                            color = Color.White,
                            modifier = Modifier.alpha(.4f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold

                        )
                    }
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@ExperimentalPagerApi
@Composable
fun TabsContent(
    viewModel: MainViewModel,
    navController: NavController,
) {
    HorizontalPager(state = viewModel.affectionsScreenState.pagerState) { page ->
        when (page) {
            0 -> {
                LikesContent(viewModel, navController)
            }
            1 -> {
                MatchesContent(viewModel, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MatchesContent(viewModel: MainViewModel, navController: NavController){

    var hasBeenCalled by remember { mutableStateOf(false) }
    if(!hasBeenCalled) {
        viewModel.acknowledgeNewMatches()
        hasBeenCalled = true
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(viewModel.affectionsScreenState.matches.sortedByDescending { it.second }) { pair ->
            AffectionsItem(
                pair.second,
                pair.first,
                viewModel,
                navController
            )
        }
    }

}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun LikesContent(viewModel: MainViewModel, navController: NavController){

    var hasBeenCalled by remember { mutableStateOf(false) }
    if(!hasBeenCalled) {
        viewModel.acknowledgeNewLikes()
        hasBeenCalled = true
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(viewModel.affectionsScreenState.likesMe.sortedByDescending { it.second }) { pair ->
            AffectionsItem(
                pair.second,
                pair.first,
                viewModel,
                navController
            )
        }
    }
}

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
import com.google.accompanist.pager.rememberPagerState
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.util.WindowSizeClass
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AffectionsScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: MainViewModel
) {

    var hasBeenCalled by remember { mutableStateOf(false) }
    if(!hasBeenCalled) {
        viewModel.getLikesAndMatches()
        hasBeenCalled = true
    }
    val pagerState = rememberPagerState(pageCount = 2)
    val contentAlpha = if (viewModel.affectionsScreenState.isLoading.value) .5f else 1f

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(
            Modifier
                .fillMaxSize()
                .alpha(contentAlpha),
            Arrangement.SpaceEvenly) {
            Tabs(windowSizeClass, pagerState)
            TabsContent(windowSizeClass, pagerState, viewModel, navController)
        }
        if(viewModel.affectionsScreenState.isLoading.value)
            ProgressIndicatorClickDisabled()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPagerApi
@Composable
fun Tabs(windowSizeClass: WindowSizeClass, pagerState: PagerState) {

    val list = listOf("Likes", "Matches")
    val selectedTabFontSize = if(windowSizeClass == WindowSizeClass.COMPACT) 36.sp else 46.sp
    val unselectedTabFontSize = if(windowSizeClass == WindowSizeClass.COMPACT) 20.sp else 30.sp
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
                            fontSize = selectedTabFontSize,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    } else {
                        Text(
                            list[index],
                            color = Color.White,
                            modifier = Modifier.alpha(.4f),
                            fontSize = unselectedTabFontSize,
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
    windowSizeClass: WindowSizeClass,
    pagerState: PagerState,
    viewModel: MainViewModel,
    navController: NavController,
) {
    HorizontalPager(pagerState) { page ->
        when (page) {
            0 -> {
                LikesContent(windowSizeClass, viewModel, navController)
            }
            1 -> {
                MatchesContent(windowSizeClass, viewModel, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MatchesContent(windowSizeClass: WindowSizeClass, viewModel: MainViewModel, navController: NavController){

    var hasBeenCalled by remember { mutableStateOf(false) }
    if(!hasBeenCalled) {
        viewModel.acknowledgeNewMatches()
        hasBeenCalled = true
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(viewModel.affectionsScreenState.matches.sortedByDescending { it.second }) { pair ->
            AffectionsItem(
                windowSizeClass,
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
fun LikesContent(windowSizeClass: WindowSizeClass, viewModel: MainViewModel, navController: NavController){

    var hasBeenCalled by remember { mutableStateOf(false) }
    if(!hasBeenCalled) {
        viewModel.acknowledgeNewLikes()
        hasBeenCalled = true
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(viewModel.affectionsScreenState.likesMe.sortedByDescending { it.second }) { pair ->
            AffectionsItem(
                windowSizeClass,
                pair.second,
                pair.first,
                viewModel,
                navController
            )
        }
    }
}

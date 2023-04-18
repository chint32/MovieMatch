package cotey.hinton.moviedate.feature_main.presentation.screens.view_movies

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ViewMoviesScreen(
    viewModel: MainViewModel,
    navController: NavController,
    isMyProfile: Boolean
) {
    val pagerState = rememberPagerState(pageCount = 2)
    Column {
        Tabs(pagerState = pagerState)
        TabsContent(pagerState, viewModel, navController, isMyProfile)
    }

}

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPagerApi
@Composable
fun Tabs(pagerState: PagerState) {

    val list = listOf("Movies", "Songs")
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
    pagerState: PagerState,
    viewModel: MainViewModel,
    navController: NavController,
    isMyProfile: Boolean
) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> {
                MainMoviesContent(viewModel, navController, isMyProfile)
            }
            1 -> {
                MainSongsContent(viewModel, navController, isMyProfile)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MainMoviesContent(
    viewModel: MainViewModel,
    navController: NavController,
    isMyProfile: Boolean
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.9f),
        columns = GridCells.Fixed(count = 2)
    ) {
        items(
            if (isMyProfile) viewModel.sharedState.myUserInfo.value.favoriteMovies
            else viewModel.sharedState.otherUserInfo.value.favoriteMovies
        ) { movie ->
            MainMovieItem(movie, viewModel, navController)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MainSongsContent(
    viewModel: MainViewModel,
    navController: NavController,
    isMyProfile: Boolean
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(.9f),
        columns = GridCells.Fixed(count = 2)
    ) {
        items(
            if (isMyProfile) viewModel.sharedState.myUserInfo.value.favoriteTracks
            else viewModel.sharedState.otherUserInfo.value.favoriteTracks
        ) { track ->
            MainSongItem(track, viewModel, navController)
        }
    }
}

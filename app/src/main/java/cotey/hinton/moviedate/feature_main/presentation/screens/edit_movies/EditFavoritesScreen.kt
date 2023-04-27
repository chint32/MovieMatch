package cotey.hinton.moviedate.feature_main.presentation.screens.edit_movies

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.ui.theme.Pink
import cotey.hinton.moviedate.util.WindowSizeClass
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EditFavoritesScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: MainViewModel

) {
    viewModel.getTop100()
    viewModel.getSpotifyTop200()
    viewModel.addFavoritesMoviesToEdit()
    viewModel.addFavoriteSongsToEdit()

    val pagerState = rememberPagerState(pageCount = 2)
    val searchText = remember { mutableStateOf("") }
    val contentAlpha = if (viewModel.editMoviesScreenState.isLoading.value) .5f else 1f


    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.93f)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            SearchMoviesTextField(windowSizeClass, searchText, pagerState, viewModel)
            Tabs(windowSizeClass, pagerState)
            TabsContent(windowSizeClass, searchText, pagerState, viewModel, navController)
            SubmitFavoritesButton(windowSizeClass, viewModel)
        }

        if (viewModel.editMoviesScreenState.isLoading.value) {
            ProgressIndicatorClickDisabled()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchMoviesTextField(
    windowSizeClass: WindowSizeClass,
    searchText: MutableState<String>,
    pagerState: PagerState,
    viewModel: MainViewModel
) {
    val fontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 16.sp else 26.sp
    val iconSize = if (windowSizeClass == WindowSizeClass.COMPACT) 24.dp else 34.dp

    TextField(
        value = searchText.value,
        onValueChange = { searchText.value = it },
        placeholder = {
            Text(
                text = "Enter search here",
                color = Color.Gray.copy(.5f),
                fontSize = fontSize
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            focusedIndicatorColor = Pink,
            unfocusedIndicatorColor = Pink.copy(.5f)
        ),
        trailingIcon = {
            IconButton(onClick = {
                if (pagerState.currentPage == 0)
                    viewModel.getMovies(searchText.value)
                else {
                    viewModel.searchSongsByTitle(searchText.value)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = Pink,
                    contentDescription = "Search urban dictionary for definitions of given word",
                    modifier = Modifier.size(iconSize)
                )
            }
        },
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(fontSize = fontSize)
    )
}


@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPagerApi
@Composable
fun Tabs(windowSizeClass: WindowSizeClass, pagerState: PagerState) {

    val list = listOf("Movies", "Songs")
    val selectedTabFontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 36.sp else 46.sp
    val unselectedTabFontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 20.sp else 30.sp
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
    searchText: MutableState<String>,
    pagerState: PagerState,
    viewModel: MainViewModel,
    navController: NavController,
) {
    HorizontalPager(pagerState) { page ->
        when (page) {
            0 -> {
                EditMoviesContent(windowSizeClass, searchText, viewModel, navController)
            }

            1 -> {
                EditSongsContent(windowSizeClass, searchText, viewModel, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun EditMoviesContent(
    windowSizeClass: WindowSizeClass,
    searchText: MutableState<String>,
    viewModel: MainViewModel,
    navController: NavController
) {
    Column() {
        if (searchText.value.isBlank())
            Top100MoviesVerticalGrid(windowSizeClass, viewModel, navController)
        else
            SearchMoviesVerticalGrid(windowSizeClass, viewModel, navController)

        if (viewModel.editMoviesScreenState.favoriteMovies.isNotEmpty())
            FavoriteMoviesHorizontalScroll(windowSizeClass, viewModel, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Top100MoviesVerticalGrid(
    windowSizeClass: WindowSizeClass,
    viewModel: MainViewModel,
    navController: NavController
) {
    val fillHeight = if (viewModel.editMoviesScreenState.favoriteMovies.isNotEmpty()) .60f else .93f
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fillHeight)
            .padding(6.dp, 0.dp, 6.dp, 6.dp),
        columns = GridCells.Fixed(count = 2),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(viewModel.editMoviesScreenState.top100Movies) { movie ->
            EditMovieItem(
                windowSizeClass,
                movie = movie,
                viewModel,
                navController,
                true
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun SearchMoviesVerticalGrid(
    windowSizeClass: WindowSizeClass,
    viewModel: MainViewModel,
    navController: NavController
) {
    val fillHeight = if (viewModel.editMoviesScreenState.favoriteMovies.isNotEmpty()) .65f else .93f
    LazyVerticalGrid(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxHeight(fillHeight)
            .fillMaxWidth(),
        columns = GridCells.Fixed(count = 2)
    ) {
        items(viewModel.editMoviesScreenState.searchedMovies) { movie ->
            EditMovieItem(
                windowSizeClass,
                movie = movie,
                viewModel,
                navController,
                true
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun FavoriteMoviesHorizontalScroll(
    windowSizeClass: WindowSizeClass,
    viewModel: MainViewModel,
    navController: NavController
) {
    val height = if (windowSizeClass == WindowSizeClass.COMPACT) 120.dp else 240.dp
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(viewModel.editMoviesScreenState.favoriteMovies) { movie ->
            EditMovieItem(
                windowSizeClass,
                movie,
                viewModel,
                navController,
                false
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun EditSongsContent(
    windowSizeClass: WindowSizeClass,
    searchText: MutableState<String>,
    viewModel: MainViewModel,
    navController: NavController
) {
    Column() {
        if (searchText.value.isBlank())
            Top200SongsVerticalGrid(viewModel, navController)
        else
            SearchSongsVerticalGrid(viewModel, navController)

        if (viewModel.editMoviesScreenState.favoriteSongs.isNotEmpty())
            FavoriteSongsHorizontalScroll(windowSizeClass, viewModel, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Top200SongsVerticalGrid(
    viewModel: MainViewModel,
    navController: NavController
) {
    val fillHeight = if (viewModel.editMoviesScreenState.favoriteSongs.isNotEmpty()) .65f else .93f
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fillHeight)
            .padding(6.dp),
        columns = GridCells.Fixed(count = 2),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(viewModel.editMoviesScreenState.top200Songs) { song ->
            EditSongItem(
                song,
                viewModel,
                navController,
                true
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun SearchSongsVerticalGrid(
    viewModel: MainViewModel,
    navController: NavController
) {
    val fillHeight = if (viewModel.editMoviesScreenState.favoriteSongs.isNotEmpty()) .65f else .93f
    LazyVerticalGrid(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxHeight(fillHeight)
            .fillMaxWidth(),
        columns = GridCells.Fixed(count = 2)
    ) {
        items(viewModel.editMoviesScreenState.searchedSongs) { song ->
            EditSongItem(
                song,
                viewModel,
                navController,
                true
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun FavoriteSongsHorizontalScroll(
    windowSizeClass: WindowSizeClass,
    viewModel: MainViewModel,
    navController: NavController
) {
    val height = if (windowSizeClass == WindowSizeClass.COMPACT) 100.dp else 240.dp
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(viewModel.editMoviesScreenState.favoriteSongs) { song ->
            EditSongItem(
                song,
                viewModel,
                navController,
                false
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun SubmitFavoritesButton(windowSizeClass: WindowSizeClass, viewModel: MainViewModel) {
    val mContext = LocalContext.current
    val fontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 18.sp else 28.sp
    Button(
        modifier = Modifier
            .fillMaxWidth(.8f)
            .padding(0.dp, 0.dp, 0.dp, 20.dp),
        onClick = {
            if (viewModel.editMoviesScreenState.favoriteMovies.size < 3) {
                Toast.makeText(
                    mContext,
                    "Please select at least 3 favorite movies",
                    Toast.LENGTH_SHORT
                ).show()
                return@Button
            }
            viewModel.sharedState.myUserInfo.value.favoriteMovies.clear()
            viewModel.sharedState.myUserInfo.value.favoriteMovies.addAll(viewModel.editMoviesScreenState.favoriteMovies)
            viewModel.sharedState.myUserInfo.value.favoriteTracks.clear()
            viewModel.sharedState.myUserInfo.value.favoriteTracks.addAll(viewModel.editMoviesScreenState.favoriteSongs)
            viewModel.updateUserInfo(viewModel.sharedState.myUserInfo.value, null, null)
        },
        shape = CircleShape
    ) {
        Text(
            text = "Set Favorites",
            fontSize = fontSize
        )
    }
}




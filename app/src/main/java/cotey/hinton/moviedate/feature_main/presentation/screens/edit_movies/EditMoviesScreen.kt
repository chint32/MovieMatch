package cotey.hinton.moviedate.feature_main.presentation.screens.edit_movies

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.ui.theme.Pink
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun EditMoviesScreen(
    navController: NavController,
    viewModel: MainViewModel

) {
    viewModel.getTop100()
    viewModel.getSpotifyTop200()
    viewModel.addFavoritesMoviesToEdit()
    viewModel.addFavoriteSongsToEdit()
    Box(
        Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.93f)
                .alpha(if (viewModel.editMoviesScreenState.isLoading.value) .5f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            SearchMoviesTextField(viewModel)
            Tabs(pagerState = viewModel.editMoviesScreenState.pagerState)
            TabsContent(viewModel, navController)
            SubmitFavoritesButton(viewModel)
        }

        if (viewModel.editMoviesScreenState.isLoading.value) {
            ProgressIndicatorClickDisabled()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchMoviesTextField(viewModel: MainViewModel) {
    TextField(
        value = viewModel.editMoviesScreenState.searchText.value,
        onValueChange = { viewModel.editMoviesScreenState.searchText.value = it },
        placeholder = {
            Text(text = "Enter search here", color = Color.Gray.copy(.5f))
        },
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            focusedIndicatorColor = Pink,
            unfocusedIndicatorColor = Pink.copy(.5f)
        ),
        trailingIcon = {
            IconButton(onClick = {
                if (viewModel.editMoviesScreenState.pagerState.currentPage == 0)
                    viewModel.getMovies(viewModel.editMoviesScreenState.searchText.value)
                else {
                    viewModel.searchSongsByTitle(viewModel.editMoviesScreenState.searchText.value)
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = Pink,
                    contentDescription = "Search urban dictionary for definitions of given word"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
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
    viewModel: MainViewModel,
    navController: NavController,
) {
    HorizontalPager(viewModel.editMoviesScreenState.pagerState) { page ->
        when (page) {
            0 -> {
                EditMoviesContent(viewModel, navController)
            }
            1 -> {
                EditSongsContent(viewModel, navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun EditMoviesContent(viewModel: MainViewModel, navController: NavController) {
    Column() {
        if (viewModel.editMoviesScreenState.searchText.value.isBlank())
            Top100MoviesVerticalGrid(viewModel, navController)
        else
            SearchMoviesVerticalGrid(viewModel, navController)

        if (viewModel.editMoviesScreenState.favoriteMovies.isNotEmpty())
            FavoriteMoviesHorizontalScroll(viewModel, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Top100MoviesVerticalGrid(
    viewModel: MainViewModel,
    navController: NavController
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (viewModel.editMoviesScreenState.favoriteMovies.isNotEmpty()) .65f else .93f)
            .padding(6.dp),
        columns = GridCells.Fixed(count = 2),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(viewModel.editMoviesScreenState.top100Movies) { movie ->
            EditMovieItem(
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
    viewModel: MainViewModel,
    navController: NavController
) {
    LazyVerticalGrid(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxHeight(if (viewModel.editMoviesScreenState.favoriteMovies.isNotEmpty()) .65f else .93f)
            .fillMaxWidth(),
        columns = GridCells.Fixed(count = 2)
    ) {
        items(viewModel.editMoviesScreenState.searchedMovies) { movie ->
            EditMovieItem(
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
    viewModel: MainViewModel,
    navController: NavController
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(viewModel.editMoviesScreenState.favoriteMovies) { movie ->
            EditMovieItem(
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
    viewModel: MainViewModel,
    navController: NavController
) {
    Column() {
        if (viewModel.editMoviesScreenState.searchText.value.isBlank())
            Top200SongsVerticalGrid(viewModel, navController)
        else
            SearchSongsVerticalGrid(viewModel, navController)

        if (viewModel.editMoviesScreenState.favoriteSongs.isNotEmpty())
            FavoriteSongsHorizontalScroll(viewModel, navController)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Top200SongsVerticalGrid(
    viewModel: MainViewModel,
    navController: NavController
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(if (viewModel.editMoviesScreenState.favoriteSongs.isNotEmpty()) .65f else .93f)
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
    LazyVerticalGrid(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxHeight(if (viewModel.editMoviesScreenState.favoriteSongs.isNotEmpty()) .65f else .93f)
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
    viewModel: MainViewModel,
    navController: NavController
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(viewModel.editMoviesScreenState.favoriteSongs) { song ->
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
fun SubmitFavoritesButton(viewModel: MainViewModel) {
    val mContext = LocalContext.current
    Button(
        modifier = Modifier
            .fillMaxWidth(.8f)
            .padding(0.dp, 20.dp),
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
        Text(text = "Set Favorites")
    }
}




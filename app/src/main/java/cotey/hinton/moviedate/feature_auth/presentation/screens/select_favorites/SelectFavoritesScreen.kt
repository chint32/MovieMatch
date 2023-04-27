package cotey.hinton.moviedate.feature_auth.presentation.screens.select_favorites

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import cotey.hinton.moviedate.feature_auth.domain.models.Movie
import cotey.hinton.moviedate.feature_auth.domain.models.TrackMetaData
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.feature_main.presentation.MainActivity
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.ui.theme.Pink
import cotey.hinton.moviedate.util.WindowSizeClass
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun SelectFavoritesScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: AuthViewModel
) {
    val mContext = LocalContext.current
    val registrationSuccessAnim1: MutableTransitionState<Boolean> =
        MutableTransitionState(false).apply {
            targetState = false
        }
    val registrationSuccessAnim2: MutableTransitionState<Boolean> =
        MutableTransitionState(false).apply {
            targetState = false
        }
    viewModel.getTop100()
    viewModel.getSpotifyTop200()
    handleRegistrationErrors(mContext, viewModel)
    handleRegistrationSuccess(registrationSuccessAnim1, viewModel)
    val pagerState = rememberPagerState(2)
    val searchText = remember { mutableStateOf("") }
    val favoriteMovies = remember { mutableStateListOf<Movie>() }
    val favoriteSongs = remember { mutableStateListOf<TrackMetaData>() }
    val fontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 16.sp else 26.sp
    val iconSize = if (windowSizeClass == WindowSizeClass.COMPACT) 24.dp else 48.dp

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .alpha(if (viewModel.selectFavoritesState.isLoading.value) .5f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
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
                    IconButton(modifier = Modifier.testTag("search_icon"), onClick = {
                        if (pagerState.currentPage == 0)
                            viewModel.searchMoviesByTitle(searchText.value)
                        else
                            viewModel.searchSongsByTitle(searchText.value)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = Pink,
                            contentDescription = "",
                            modifier = Modifier.size(iconSize)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(fontSize = fontSize)
            )
            Tabs(windowSizeClass, pagerState)
            TabsContent(
                windowSizeClass,
                fontSize,
                iconSize,
                searchText,
                favoriteMovies,
                favoriteSongs,
                pagerState,
                viewModel,
                navController
            )
            Button(
                modifier = Modifier.fillMaxWidth(.8f),
                onClick = {
                    if (favoriteMovies.size < 3) {
                        Toast.makeText(
                            mContext,
                            "Please select at least 3 favorite movies",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    } else if (favoriteSongs.size < 3) {
                        Toast.makeText(
                            mContext,
                            "Please select at least 3 favorite songs",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    viewModel.sharedState.userInfo.value.favoriteMovies.addAll(favoriteMovies)
                    viewModel.sharedState.userInfo.value.favoriteTracks.addAll(favoriteSongs)
                    viewModel.register()
                },
                shape = CircleShape
            ) {
                Text(text = "Create Profile", fontSize = fontSize)
            }
        }

        if (viewModel.selectFavoritesState.isLoading.value) {
            ProgressIndicatorClickDisabled()
        }
        SuccessfulRegistrationAnimation(
            mContext,
            registrationSuccessAnim1,
            registrationSuccessAnim2
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPagerApi
@Composable
fun Tabs(windowSizeClass: WindowSizeClass, pagerState: PagerState) {

    val list = listOf("Movies", "Songs")
    val tabFontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 36.sp else 46.sp
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
                            fontSize = tabFontSize,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    } else {
                        Text(
                            list[index],
                            color = Color.White,
                            modifier = Modifier.alpha(.4f),
                            fontSize = tabFontSize.times(.8f),
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

@ExperimentalPagerApi
@Composable
fun TabsContent(
    windowSizeClass: WindowSizeClass,
    fontSize: TextUnit,
    iconSize: Dp,
    searchText: MutableState<String>,
    favoriteMovies: SnapshotStateList<Movie>,
    favoriteSongs: SnapshotStateList<TrackMetaData>,
    pagerState: PagerState,
    viewModel: AuthViewModel,
    navController: NavController,
) {
    HorizontalPager(pagerState) { page ->
        when (page) {
            0 -> {
                MoviesContent(
                    windowSizeClass,
                    iconSize,
                    searchText,
                    favoriteMovies,
                    viewModel,
                    navController
                )
            }

            1 -> {
                SongsContent(
                    windowSizeClass,
                    fontSize,
                    iconSize,
                    searchText,
                    favoriteSongs,
                    viewModel,
                    navController
                )
            }
        }
    }
}

@Composable
fun MoviesContent(
    windowSizeClass: WindowSizeClass,
    iconSize: Dp,
    searchText: MutableState<String>,
    favoriteMovies: SnapshotStateList<Movie>,
    viewModel: AuthViewModel,
    navController: NavController
) {
    val favoritesRowHeight = if (windowSizeClass == WindowSizeClass.COMPACT) 120.dp else 200.dp
    Column() {

        if (searchText.value.isBlank()) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(if (favoriteMovies.isNotEmpty()) .67f else .93f)
                    .padding(6.dp),
                columns = GridCells.Fixed(count = 2)
            ) {
                items(viewModel.selectFavoritesState.top100Movies) { movie ->
                    AuthMovieItem(iconSize, movie, favoriteMovies, navController, true)
                }
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxHeight(if (favoriteMovies.isNotEmpty()) .67f else .93f)
                    .fillMaxWidth(),
                columns = GridCells.Fixed(count = 2)
            ) {
                items(viewModel.selectFavoritesState.searchedMovies) { movie ->
                    AuthMovieItem(iconSize, movie, favoriteMovies, navController, true)
                }
            }
        }
        if (favoriteMovies.isNotEmpty()) {
            LazyRow(
                Modifier
                    .fillMaxWidth()
                    .height(favoritesRowHeight)
            ) {
                items(favoriteMovies) { movie ->
                    AuthMovieItem(iconSize, movie, favoriteMovies, navController, false)
                }
            }
        }
    }

}

@Composable
fun SongsContent(
    windowSizeClass: WindowSizeClass,
    fontSize: TextUnit,
    iconSize: Dp,
    searchText: MutableState<String>,
    favoriteSongs: SnapshotStateList<TrackMetaData>,
    viewModel: AuthViewModel,
    navController: NavController,
) {
    Column {
        if (searchText.value.isBlank()) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(if (favoriteSongs.isNotEmpty()) .67f else .93f)
                    .padding(6.dp),
                columns = GridCells.Fixed(count = 2)
            ) {
                items(viewModel.selectFavoritesState.top200Songs) { song ->
                    AuthSongItem(
                        fontSize,
                        iconSize,
                        song,
                        favoriteSongs,
                        viewModel,
                        navController,
                        true
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxHeight(if (favoriteSongs.isNotEmpty()) .67f else .93f)
                    .fillMaxWidth(),
                columns = GridCells.Fixed(count = 2)
            ) {
                items(viewModel.selectFavoritesState.searchedSongs) { song ->
                    AuthSongItem(
                        fontSize,
                        iconSize,
                        song,
                        favoriteSongs,
                        viewModel,
                        navController,
                        true
                    )
                }
            }
        }
        if (favoriteSongs.isNotEmpty()) {
            LazyRow(
                Modifier
                    .fillMaxWidth()
                    .height(if (windowSizeClass == WindowSizeClass.COMPACT) 120.dp else 200.dp)
            ) {
                items(favoriteSongs) { song ->
                    AuthSongItem(
                        fontSize,
                        iconSize,
                        song,
                        favoriteSongs,
                        viewModel,
                        navController,
                        false
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SuccessfulRegistrationAnimation(
    mContext: Context,
    registrationSuccessAnim1: MutableTransitionState<Boolean>,
    registrationSuccessAnim2: MutableTransitionState<Boolean>
) {
    AnimatedVisibility(
        visibleState = registrationSuccessAnim1,
        enter = scaleIn(tween(700)),
    ) {
        when {
            registrationSuccessAnim1.isIdle &&
                    registrationSuccessAnim1.currentState -> {
                registrationSuccessAnim2.targetState = true
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        color = Pink,
                        radius = this.size.maxDimension
                    )
                },
            contentAlignment = Alignment.Center
        ) {

            AnimatedVisibility(
                visibleState = registrationSuccessAnim2,
                enter = scaleIn(tween(1000))
            ) {
                when {
                    registrationSuccessAnim2.isIdle &&
                            registrationSuccessAnim2.currentState -> {
                        mContext.startActivity(Intent(mContext, MainActivity::class.java))
                    }
                }
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                tint = Pink
            )
        }
    }
}

fun handleRegistrationSuccess(
    registrationSuccessAnim1: MutableTransitionState<Boolean>,
    viewModel: AuthViewModel
) {
    when (viewModel.selectFavoritesState.authResult.value) {
        null -> {}
        else -> {
            registrationSuccessAnim1.apply { targetState = true }
        }
    }
}

fun handleRegistrationErrors(mContext: Context, viewModel: AuthViewModel) {

    when (viewModel.selectFavoritesState.error.value) {
        "ERROR_EMAIL_ALREADY_IN_USE" -> {
            Toast.makeText(mContext, "Error: That email is already in use.", Toast.LENGTH_SHORT)
                .show()
            viewModel.selectFavoritesState.error.value = null
        }

        "ERROR_INVALID_EMAIL" -> {
            Toast.makeText(mContext, "Error: Invalid email adress", Toast.LENGTH_SHORT).show()
            viewModel.selectFavoritesState.error.value = null
        }

        "ERROR_WEAK-PASSWORD" -> {
            Toast.makeText(mContext, "Error: Invalid email address", Toast.LENGTH_SHORT).show()
            viewModel.selectFavoritesState.error.value = null
        }

        else -> {}
    }
}


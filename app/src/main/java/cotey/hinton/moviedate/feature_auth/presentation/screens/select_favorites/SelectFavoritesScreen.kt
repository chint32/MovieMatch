package cotey.hinton.moviedate.feature_auth.presentation.screens.select_favorites

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.gson.Gson
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo
import cotey.hinton.moviedate.feature_auth.presentation.screens.shared.SharedState
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.feature_main.presentation.MainActivity
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.ui.theme.Pink
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun SelectFavoritesScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val mContext = LocalContext.current
    viewModel.getTop100()
    viewModel.getSpotifyTop200()
    handleRegistrationErrors(mContext, viewModel)
    handleRegistrationSuccess(viewModel)

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
                value = viewModel.selectFavoritesState.searchText.value,
                onValueChange = { viewModel.selectFavoritesState.searchText.value = it },
                placeholder = {
                    Text(text = "Enter search here", color = Color.Gray.copy(.5f))
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White,
                    focusedIndicatorColor = Pink,
                    unfocusedIndicatorColor = Pink.copy(.5f)
                ),
                trailingIcon = {
                    IconButton(modifier = Modifier.testTag("search_icon"), onClick = {
                        if(viewModel.selectFavoritesState.pagerState.currentPage == 0)
                            viewModel.searchMoviesByTitle(viewModel.selectFavoritesState.searchText.value)
                        else
                            viewModel.searchSongsByTitle(viewModel.selectFavoritesState.searchText.value)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = Pink,
                            contentDescription = ""
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Tabs(pagerState = viewModel.selectFavoritesState.pagerState)
            TabsContent(viewModel, navController)
            Button(
                modifier = Modifier.fillMaxWidth(.8f),
                onClick = {
                    if (viewModel.selectFavoritesState.favoriteMovies.size < 3) {
                        Toast.makeText(
                            mContext,
                            "Please select at least 3 favorite movies",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    else if(viewModel.selectFavoritesState.favoriteSongs.size < 3) {
                        Toast.makeText(
                            mContext,
                            "Please select at least 3 favorite movies",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    viewModel.sharedState.userInfo.value.favoriteMovies.addAll(viewModel.selectFavoritesState.favoriteMovies)
                    viewModel.sharedState.userInfo.value.favoriteTracks.addAll(viewModel.selectFavoritesState.favoriteSongs)
                    viewModel.register()
                },
                shape = CircleShape
            ) {
                Text(text = "Create Profile")
            }
        }

        if (viewModel.selectFavoritesState.isLoading.value) {
            ProgressIndicatorClickDisabled()
        }
        SuccessfulRegistrationAnimation(mContext, viewModel)
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

@ExperimentalPagerApi
@Composable
fun TabsContent(
    viewModel: AuthViewModel,
    navController: NavController,
) {
    HorizontalPager(state = viewModel.selectFavoritesState.pagerState) { page ->
        when (page) {
            0 -> {
                MoviesContent(viewModel, navController)
            }
            1 -> {
                SongsContent(viewModel, navController)
            }
        }
    }
}

@Composable
fun MoviesContent(viewModel: AuthViewModel, navController: NavController){
    Column() {

        if (viewModel.selectFavoritesState.searchText.value.isBlank()) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(if (viewModel.selectFavoritesState.favoriteMovies.isNotEmpty()) .75f else .93f)
                    .padding(6.dp),
                columns = GridCells.Fixed(count = 2)
            ) {
                items(viewModel.selectFavoritesState.top100Movies) { movie ->
                    AuthMovieItem(movie, viewModel, navController,true)
                }
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxHeight(if (viewModel.selectFavoritesState.favoriteMovies.isNotEmpty()) .75f else .93f)
                    .fillMaxWidth(),
                columns = GridCells.Fixed(count = 2)
            ) {
                items(viewModel.selectFavoritesState.searchedMovies) { movie ->
                    AuthMovieItem(movie, viewModel, navController,true)
                }
            }
        }
        if(viewModel.selectFavoritesState.favoriteMovies.isNotEmpty()) {
            LazyRow(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)) {
                items(viewModel.selectFavoritesState.favoriteMovies) { movie ->
                    AuthMovieItem(movie, viewModel, navController,false)
                }
            }
        }
    }

}

@Composable
fun SongsContent(viewModel: AuthViewModel, navController: NavController, ){
    Column {
        if (viewModel.selectFavoritesState.searchText.value.isBlank()) {
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(if (viewModel.selectFavoritesState.favoriteSongs.isNotEmpty()) .75f else .93f)
                    .padding(6.dp),
                columns = GridCells.Fixed(count = 2)
            ) {
                items(viewModel.selectFavoritesState.top200Songs) { song ->
                    AuthSongItem(song, viewModel, navController, true)
                }
            }
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxHeight(if (viewModel.selectFavoritesState.favoriteSongs.isNotEmpty()) .75f else .93f)
                    .fillMaxWidth(),
                columns = GridCells.Fixed(count = 2)
            ) {
                items(viewModel.selectFavoritesState.searchedSongs) { song ->
                    AuthSongItem(song, viewModel, navController, true)
                }
            }
        }
        if (viewModel.selectFavoritesState.favoriteSongs.isNotEmpty()) {
            LazyRow(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                items(viewModel.selectFavoritesState.favoriteSongs) { song ->
                    AuthSongItem(song, viewModel, navController, false)
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SuccessfulRegistrationAnimation(mContext: Context, viewModel: AuthViewModel){
    AnimatedVisibility(
        visibleState = viewModel.selectFavoritesState.registrationSuccessAnim1,
        enter = scaleIn(tween(700)),
    ) {
        when {
            viewModel.selectFavoritesState.registrationSuccessAnim1.isIdle &&
                    viewModel.selectFavoritesState.registrationSuccessAnim1.currentState -> {
                viewModel.selectFavoritesState.registrationSuccessAnim2.targetState = true
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
                visibleState = viewModel.selectFavoritesState.registrationSuccessAnim2,
                enter = scaleIn(tween(1000))
            ) {
                when {
                    viewModel.selectFavoritesState.registrationSuccessAnim2.isIdle &&
                            viewModel.selectFavoritesState.registrationSuccessAnim2.currentState -> {
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

fun handleRegistrationSuccess(viewModel: AuthViewModel){
    when (viewModel.selectFavoritesState.authResult.value) {
        null -> { }
        else -> {
            viewModel.selectFavoritesState.registrationSuccessAnim1.apply { targetState = true }
        }
    }
}

fun handleRegistrationErrors(mContext: Context, viewModel: AuthViewModel){

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
        else -> { }
    }
}


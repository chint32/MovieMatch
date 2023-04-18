package cotey.hinton.moviedate.feature_main.presentation.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.compose.AsyncImage
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.ui.theme.Pink
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardStack(
    modifier: Modifier = Modifier,
    items: MutableList<Pair<UserInfo, Int>>,
    thresholdConfig: (Float, Float) -> ThresholdConfig = { _, _ -> FractionalThreshold(0.2f) },
    velocityThreshold: Dp = 125.dp,
    onSwipeLeft: (item: Pair<UserInfo, Int>) -> Unit = {},
    onSwipeRight: (item: Pair<UserInfo, Int>) -> Unit = {},
    onEmptyStack: (lastItem: Pair<UserInfo, Int>) -> Unit = {},
    viewModel: MainViewModel,
    navController: NavController
) {
    var i by remember {
        mutableStateOf(items.size - 1)
    }

    if (i == -1) {
        onEmptyStack(items.last())
    }

    val cardStackController = rememberCardStackController()

    cardStackController.onSwipeLeft = {
        viewModel.sharedState.myUserInfo.value.dislikes.add(
            items[i].first.uid
        )
        viewModel.updateUserInfo(
            viewModel.sharedState.myUserInfo.value, null, viewModel.sharedState.otherUserInfo.value
        )
        onSwipeLeft(items[i])
        i--
    }

    cardStackController.onSwipeRight = {
        viewModel.sharedState.myUserInfo.value.likes.add(items[i].first.uid)
        viewModel.addLike(items[i].first)
        if (items[i].first.likes.contains(viewModel.sharedState.myUserInfo.value.uid))
            viewModel.addMatch(items[i].first)
        viewModel.updateUserInfo(
            viewModel.sharedState.myUserInfo.value, null, viewModel.sharedState.otherUserInfo.value
        )
        onSwipeRight(items[i])
        i--

    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        val stack = createRef()

        Box(modifier = modifier
            .constrainAs(stack) {
                top.linkTo(parent.top)
            }
            .draggableStack(
                controller = cardStackController,
                thresholdConfig = thresholdConfig,
                velocityThreshold = velocityThreshold
            )
            .fillMaxSize()


        ) {
            items.forEachIndexed { index, item ->
                Card(
                    modifier = Modifier
                        .moveTo(
                            x = if (index == i) cardStackController.offsetX.value else 0f,
                            y = if (index == i) cardStackController.offsetY.value else 0f
                        )
                        .padding(
                            ((items.size - index) * 3).dp,
                            (index * 3).dp,
                            ((items.size - index) * 3).dp,
                            0.dp
                        )
                        .visible(visible = index > i - 5 && index < i + 1)
                        .graphicsLayer(
                            rotationZ = if (index == i) cardStackController.rotation.value else 0f,
//                            scaleX = if (index < i) cardStackController.scale.value else 1f,
//                            scaleY = if (index < i) cardStackController.scale.value else 1f
                        ),
                    item,
                    cardStackController,
                    viewModel,
                    navController
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun Card(
    modifier: Modifier = Modifier,
    item: Pair<UserInfo, Int>,
    cardStackController: CardStackController,
    viewModel: MainViewModel,
    navController: NavController
) {
    val matchPercentage =
        calculateMatchPercentage(viewModel.sharedState.myUserInfo.value, item.first)
    Box(modifier = modifier) {
        if (item.first.images[0] != null) {
            AsyncImage(
                model = item.first.images[0],
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxSize()
                    .drawWithCache {
                        val gradient = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black),
                            startY = size.height / 3,
                            endY = size.height
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRect(gradient, blendMode = BlendMode.Multiply)
                        }
                    }
                    .clickable(
                        onClick = {
                            viewModel.sharedState.otherUserInfo.value = item.first
                            navController.navigate(Screens.ProfileDetailsScreen.route)
                        },
                    ),
            )
        }




        Column(
            modifier = modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Row() {
                Column(Modifier.fillMaxWidth(.7f)) {
                    Text(
                        text = item.first.screenName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    Text(text = "Age: ${item.first.age}", color = Color.LightGray, fontSize = 20.sp)
                    Text(
                        text = "Distance: ${item.second}mi",
                        color = Color.LightGray,
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Looking for: ${
                            item.first.lookingFor.mapNotNull { it }.joinToString()
                        }",
                        color = Color.LightGray,
                        fontSize = 20.sp
                    )
                }
                Column() {
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        text = "$matchPercentage%",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 20.dp),
                        textAlign = TextAlign.End
                    )
                }
            }


            Row {
                IconButton(
                    modifier = modifier.padding(40.dp, 0.dp, 0.dp, 0.dp),
                    onClick = { cardStackController.swipeLeft() },
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        tint = Color.DarkGray,
                        modifier = modifier
                            .height(50.dp)
                            .width(50.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(modifier = modifier.padding(0.dp, 0.dp, 40.dp, 0.dp),
                    onClick = { cardStackController.swipeRight() }) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = "",
                        tint = Pink,
                        modifier = modifier
                            .height(50.dp)
                            .width(50.dp)
                    )
                }
            }
        }
    }
}

private fun calculateMatchPercentage(myUserInfo: UserInfo, otherUserInfo: UserInfo): Int {

    var numFavoritesInCommon = 0
    for (i in myUserInfo.favoriteMovies.indices) {
        for (j in otherUserInfo.favoriteMovies.indices) {
            if (myUserInfo.favoriteMovies[i] == otherUserInfo.favoriteMovies[j]) {
                numFavoritesInCommon++
                break
            }
        }
    }
    for (i in myUserInfo.favoriteTracks.indices) {
        for (j in otherUserInfo.favoriteTracks.indices) {
            if (myUserInfo.favoriteTracks[i] == otherUserInfo.favoriteTracks[j]) {
                numFavoritesInCommon++
                break
            }
        }
    }
    return (numFavoritesInCommon.toFloat() /
            (myUserInfo.favoriteMovies.size.toFloat() + myUserInfo.favoriteTracks.size) * 100).toInt()
}

fun Modifier.moveTo(
    x: Float, y: Float
) = this.then(Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    layout(placeable.width, placeable.height) {
        placeable.placeRelative(x.roundToInt(), y.roundToInt())
    }
})

fun Modifier.visible(
    visible: Boolean = true
) = this.then(Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)

    if (visible) {
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    } else {
        layout(0, 0) {}
    }
})
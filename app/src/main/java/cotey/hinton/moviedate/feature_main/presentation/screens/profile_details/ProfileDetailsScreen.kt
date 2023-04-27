package cotey.hinton.moviedate.feature_main.presentation.screens.profile_details

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.pager.*
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.ui.theme.Pink
import cotey.hinton.moviedate.util.WindowSizeClass
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalPagerApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun ProfileDetailsScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: MainViewModel,
    isMyProfile: Boolean,
    isMatch: Boolean
) {

    // references to state
    val imageSize = if (windowSizeClass == WindowSizeClass.COMPACT) 100.dp else 170.dp
    val fontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 22.sp else 30.sp
    val fontSizeIsActive = if (windowSizeClass == WindowSizeClass.COMPACT) 18.sp else 28.sp
    val fontSizeInfo = if (windowSizeClass == WindowSizeClass.COMPACT) 18.sp else 32.sp
    val myUserInfo = viewModel.sharedState.myUserInfo
    val otherUserInfo = viewModel.sharedState.otherUserInfo
    if (!isMyProfile) viewModel.profileDetailsScreenState.isEditMode.value = false
    val pagerState = rememberPagerState(
        pageCount = if (isMyProfile) myUserInfo.value.images.size else otherUserInfo.value.images.size
    )
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val matchPercentage = 100f
//        calculateMatchPercentage(myUserInfo.value, otherUserInfo.value)
    val uriList = remember { mutableStateListOf<Uri?>(null, null, null) }
    val targetValue by remember { mutableStateOf(0f) }
    val progress = remember(targetValue) { Animatable(initialValue = 0f) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(true) {
        scope.launch {
            progress.animateTo(
                targetValue = matchPercentage.toFloat(),
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing,
                )
            )
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        uriList[pagerState.currentPage] =
            uri
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {



        TabsContent(
            pagerState,
            isMyProfile,
            viewModel.profileDetailsScreenState.isEditMode,
            myUserInfo.value,
            otherUserInfo.value,
            galleryLauncher,
            uriList,
            bitmap,
            LocalContext.current
        )

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = if (viewModel.profileDetailsScreenState.isEditMode.value) Modifier
                    .fillMaxWidth(.9f)
                    .fillMaxHeight(.7f)
                    .verticalScroll(rememberScrollState())
                else Modifier
                    .fillMaxWidth(.9f)
                    .fillMaxHeight(.7f),
                horizontalAlignment = if (!viewModel.profileDetailsScreenState.isEditMode.value) Alignment.Start
                else Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    GlideImage(
                        model = if (isMyProfile) myUserInfo.value.images[0]
                        else otherUserInfo.value.images[0],
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(imageSize)
                            .clip(CircleShape)
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillBounds
                    )
                }

                // screen name
                if (isMyProfile) {
                    if (viewModel.profileDetailsScreenState.isEditMode.value) {
                        TextField(
                            value = viewModel.sharedState.myUserInfo.value.screenName,
                            onValueChange = {
                                viewModel.sharedState.myUserInfo.value.screenName = it
                            },
                            Modifier
                                .fillMaxWidth(.9f)
                                .background(Color.Transparent)
                                .align(Alignment.CenterHorizontally),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                unfocusedIndicatorColor = Color.DarkGray,
                                backgroundColor = Color.Transparent
                            ),
                            textStyle = LocalTextStyle.current.copy(
                                textAlign = TextAlign.Center,
                                fontSize = fontSize
                            )
                        )
                    } else {
                        Text(
                            text = "${myUserInfo.value.screenName}, ${myUserInfo.value.age}",
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(.82f),
                            fontSize = fontSize,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                } else {
                    Text(
                        text = "${otherUserInfo.value.screenName}, ${otherUserInfo.value.age}",
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(.82f),
                        fontSize = fontSize,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                if (!viewModel.profileDetailsScreenState.isEditMode.value) {
                    Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier
                            .size(5.dp)
                            .drawBehind {
                                drawCircle(color = Color.Green)
                            })
                        Text(
                            modifier = Modifier.padding(10.dp, 0.dp),
                            text = "Active now",
                            color = Color.LightGray,
                            fontSize = fontSizeIsActive,
                        )
                    }
                    if (!isMyProfile) {
                        Row(
                            modifier = Modifier.fillMaxWidth(.8f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val distance = distance(
                                myUserInfo.value.location.latitude.toFloat(),
                                myUserInfo.value.location.longitude.toFloat(),
                                otherUserInfo.value.location.latitude.toFloat(),
                                otherUserInfo.value.location.longitude.toFloat()
                            ).toInt()
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null, tint = Color.LightGray
                            )
                            Text(
                                text = "$distance mi away",
                                modifier = Modifier
                                    .fillMaxWidth(.6f)
                                    .padding(10.dp, 0.dp),
                                color = Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = fontSizeInfo
                            )
                        }
                    }
                }

                val genderOptions =
                    listOf(
                        "Male",
                        "Female",
                        "Transgender",
                        "Gender nonconforming",
                        "Prefer not to say"
                    )
                val lookingForOptions =
                    listOf(
                        "Chatting",
                        "Friends",
                        "Relationship",
                        "Fun"
                    )
                if (!viewModel.profileDetailsScreenState.isEditMode.value)
                    Spacer(Modifier.height(6.dp))
                DropDownItem(
                    windowSizeClass, genderOptions, "Your gender", viewModel, isMyProfile, false
                )
                if (!viewModel.profileDetailsScreenState.isEditMode.value)
                    Spacer(Modifier.height(6.dp))
                DropDownItem(
                    windowSizeClass,
                    genderOptions,
                    "Gender interested in",
                    viewModel,
                    isMyProfile,
                    true
                )
                Spacer(Modifier.height(10.dp))
                DropDownItem(
                    windowSizeClass, lookingForOptions, "Looking for", viewModel, isMyProfile, true
                )
                val ages = ArrayList<String>()
                for (i in 15..100) ages.add(i.toString())
                if (viewModel.profileDetailsScreenState.isEditMode.value) {
                    DropDownItem(
                        windowSizeClass, ages,
                        "Your age",
                        viewModel,
                        isMyProfile,
                        false
                    )
                    DropDownItem(
                        windowSizeClass, ages, "Interested in ages starting at", viewModel,
                        isMyProfile, false
                    )
                    DropDownItem(
                        windowSizeClass, ages, "Interested in ages ending at", viewModel,
                        isMyProfile, false
                    )
                }
                if (isMyProfile && viewModel.profileDetailsScreenState.isEditMode.value) {
                    var sliderState by remember { mutableStateOf(myUserInfo.value.searchDistance.toFloat()) }
                    Spacer(modifier = Modifier.height(10.dp))
                    Slider(

                        value = sliderState, onValueChange = {
                            sliderState = it
                            viewModel.sharedState.myUserInfo.value.searchDistance = it.toInt()
                        },
                        valueRange = 0f..100f,
                        steps = 9,
                        modifier = Modifier.fillMaxWidth(.8f)
                    )
                    Text(
                        text = "Search Distance: ${sliderState.toInt()}",
                        color = Color.White,
                        fontSize = fontSizeInfo
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                } else Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = {
                        if (viewModel.profileDetailsScreenState.isEditMode.value) navController.navigate(
                            Screens.EditMoviesScreen.route
                        )
                        else
                            navController.navigate(
                                Screens.ViewMoviesScreen.route + "?isMyProfile=$isMyProfile"
                            )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp, 0.dp, 40.dp, if (isMatch) 5.dp else 80.dp),
                    shape = CircleShape
                ) {
                    Text(
                        text = if (viewModel.profileDetailsScreenState.isEditMode.value) "Edit Favorites" else "View Favorites",
                        color = Color.White,
                        fontSize = fontSizeInfo
                    )
                }
                if (isMatch) {
                    Button(
                        onClick = { navController.navigate(Screens.MessagesScreen.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp, 5.dp, 40.dp, 60.dp),

                        shape = CircleShape
                    ) {
                        Text(
                            text = "Message This User", color = Color.White,
                            fontSize = fontSizeInfo,
                        )
                    }
                }
            }
        }
    }

    // setting icon to enable/disable edit mode
    if (isMyProfile) {
        if (!viewModel.profileDetailsScreenState.isEditMode.value) {
            EditIcon(windowSizeClass, uriList, viewModel, null)
        } else {
            val infiniteTransition = rememberInfiniteTransition()
            val angle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            EditIcon(windowSizeClass, uriList, viewModel, angle)
        }
    } else {
        Box(
            modifier = if (windowSizeClass == WindowSizeClass.COMPACT)
                Modifier
                    .fillMaxSize()
                    .padding(20.dp, 0.dp, 0.dp, 100.dp)
            else Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                Modifier
                    .fillMaxSize(.5f)
                    .padding(0.dp, 30.dp, 30.dp, 0.dp)
            ) {
                Spacer(modifier = Modifier.height(if (!isMatch) 20.dp else 0.dp))
                MatchPercentGuage(windowSizeClass, progress.value.toInt())
                if (windowSizeClass != WindowSizeClass.COMPACT)
                    Spacer(modifier = Modifier.height(50.dp))

            }
            Text(
                "Match\nPercent",
                color = Color.White,
                fontSize = fontSizeInfo,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = if (windowSizeClass == WindowSizeClass.COMPACT) Modifier.padding(
                    0.dp,
                    0.dp,
                    80.dp,
                    55.dp
                )
                else Modifier.padding(0.dp, 0.dp, 180.dp, 176.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun EditIcon(windowSizeClass: WindowSizeClass, uriList: SnapshotStateList<Uri?>, viewModel: MainViewModel, angle: Float?) {
    val iconSize = if(windowSizeClass == WindowSizeClass.COMPACT) 24.dp else 40.dp
    IconButton(modifier = if (angle != null) Modifier.rotate(angle) else Modifier,
        onClick = {
            if (viewModel.profileDetailsScreenState.isEditMode.value) {
                viewModel.updateUserInfo(
                    viewModel.sharedState.myUserInfo.value,
                    uriList,
                    null
                )
            }
            viewModel.profileDetailsScreenState.isEditMode.value =
                !viewModel.profileDetailsScreenState.isEditMode.value
        }) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "",
            tint = Pink,
            modifier = Modifier.size(iconSize))
    }
}


private fun distance(lat_a: Float, lng_a: Float, lat_b: Float, lng_b: Float): Float {
    val earthRadius = 3958.75
    val latDiff = Math.toRadians((lat_b - lat_a).toDouble())
    val lngDiff = Math.toRadians((lng_b - lng_a).toDouble())
    val a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
            Math.cos(Math.toRadians(lat_a.toDouble())) * Math.cos(Math.toRadians(lat_b.toDouble())) *
            Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    val distance = earthRadius * c

    return distance.toFloat()
}


@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun DropDownItem(
    windowSizeClass: WindowSizeClass,
    items: List<String>,
    label: String,
    viewModel: MainViewModel,
    viewingOwnProfile: Boolean,
    isMultiSelect: Boolean
) {

    var mExpanded by remember { mutableStateOf(false) }
    var mSelectedText by remember { mutableStateOf(label) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
    val iconSize = if(windowSizeClass == WindowSizeClass.COMPACT) 24.dp else 40.dp
    val fontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 18.sp else 32.sp
    val fontSizeButton = if (windowSizeClass == WindowSizeClass.COMPACT) 22.sp else 32.sp

    if (label == "Your gender")
        mSelectedText = if (viewingOwnProfile) viewModel.sharedState.myUserInfo.value.gender
        else viewModel.sharedState.otherUserInfo.value.gender
    if (label == "Gender interested in") mSelectedText =
        if (viewingOwnProfile) viewModel.sharedState.myUserInfo.value.genderInterestedIn.mapNotNull { it }
            .joinToString()
        else viewModel.sharedState.otherUserInfo.value.genderInterestedIn.mapNotNull { it }
            .joinToString()
    if (label == "Your age") mSelectedText =
        if (viewingOwnProfile) viewModel.sharedState.myUserInfo.value.age.toString()
        else viewModel.sharedState.otherUserInfo.value.age.toString()
    if (label == "Interested in ages starting at") mSelectedText =
        if (viewingOwnProfile) viewModel.sharedState.myUserInfo.value.startAgeInterestedIn.toString()
        else viewModel.sharedState.otherUserInfo.value.startAgeInterestedIn.toString()
    if (label == "Interested in ages ending at") mSelectedText =
        if (viewingOwnProfile) viewModel.sharedState.myUserInfo.value.endAgeInterestedIn.toString()
        else viewModel.sharedState.otherUserInfo.value.endAgeInterestedIn.toString()
    if (label == "Looking for") mSelectedText =
        if (viewingOwnProfile) viewModel.sharedState.myUserInfo.value.lookingFor.mapNotNull { it }
            .joinToString()
        else viewModel.sharedState.otherUserInfo.value.lookingFor.mapNotNull { it }
            .joinToString()


    val icon = if (mExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Row(
        modifier = Modifier.fillMaxWidth(.9f),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (label == "Your gender") Icons.Outlined.Person
            else if (label == "Gender interested in") Icons.Outlined.PersonSearch
            else if (label == "Looking for") Icons.Outlined.Search
            else Icons.Outlined.CalendarMonth,
            contentDescription = null, tint = Color.LightGray,
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = mSelectedText,
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    mTextFieldSize = coordinates.size.toSize()
                }
                .fillMaxWidth(.93f)
                .padding(10.dp, 0.dp),
            color = Color.LightGray,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = if (viewModel.profileDetailsScreenState.isEditMode.value) TextAlign.Center else TextAlign.Start
        )

        if (viewingOwnProfile && viewModel.profileDetailsScreenState.isEditMode.value) {
            IconButton(onClick = { mExpanded = !mExpanded }) {
                Icon(imageVector = icon, contentDescription = "", tint = Pink,
                    modifier = Modifier.size(iconSize)
                )
            }

            DropdownMenu(
                expanded = mExpanded,
                onDismissRequest = { mExpanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() }),
            ) {
                val selectedGenders = ArrayList<String?>()
                val selectedLookingFor = ArrayList<String?>()
                items.forEach { value ->
                    var isChecked by remember { mutableStateOf(selectedGenders.contains(value)) }
                    DropdownMenuItem(onClick = {
                        if (!isMultiSelect) {
                            mSelectedText = value
                            mExpanded = false
                            if (label == "Your gender") viewModel.sharedState.myUserInfo.value.gender =
                                mSelectedText
                            else if (label == "Your age") viewModel.sharedState.myUserInfo.value.age =
                                mSelectedText.toInt()
                            else if (label == "Interested in ages starting at")
                                viewModel.sharedState.myUserInfo.value.startAgeInterestedIn =
                                    mSelectedText.toInt()
                            else if (label == "Interested in ages ending at")
                                viewModel.sharedState.myUserInfo.value.endAgeInterestedIn =
                                    mSelectedText.toInt()
                        } else {
                            isChecked = !isChecked
                            if (label == "Gender interested in") {
                                if (isChecked) selectedGenders.add(value)
                                else selectedGenders.remove(value)
                            } else {
                                if (isChecked) selectedLookingFor.add(value)
                                else selectedLookingFor.remove(value)
                            }
                        }
                    }) {
                        if (isMultiSelect) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = value,
                                    fontSize = fontSizeButton,
                                    modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)
                                )
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = {
                                        isChecked = !isChecked
                                        if (label == "Gender interested in") {
                                            if (isChecked) selectedGenders.add(value)
                                            else selectedGenders.remove(value)
                                        } else {
                                            if (isChecked) selectedLookingFor.add(value)
                                            else selectedLookingFor.remove(value)
                                        }
                                    })
                            }
                        } else {
                            Text(
                                text = value,
                                fontSize = fontSizeButton,
                            )
                        }
                    }
                }
                if (isMultiSelect) {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp, 0.dp),
                        onClick = {
                            mSelectedText = selectedGenders.mapNotNull { it }.joinToString()
                            if (label == "Gender interested in") {
                                viewModel.sharedState.myUserInfo.value.genderInterestedIn =
                                    selectedGenders
                            } else {
                                viewModel.sharedState.myUserInfo.value.lookingFor =
                                    selectedLookingFor
                            }
                            mExpanded = false
                        },
                        shape = CircleShape
                    ) {
                        Text(
                            text = "Accept",
                            color = Color.White,
                            fontSize = fontSizeButton,
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabsContent(
    pagerState: PagerState,
    isMyProfile: Boolean,
    editMode: MutableState<Boolean>,
    myUserInfo: UserInfo,
    otherUserInfo: UserInfo,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    images: SnapshotStateList<Uri?>,
    bitmap: MutableState<Bitmap?>,
    context: Context
) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> UserImage(
                isMyProfile, myUserInfo, otherUserInfo, 0, editMode,
                galleryLauncher, images[page], bitmap, context
            )

            1 -> UserImage(
                isMyProfile, myUserInfo, otherUserInfo, 1, editMode,
                galleryLauncher, images[page], bitmap, context
            )

            2 -> UserImage(
                isMyProfile, myUserInfo, otherUserInfo, 2, editMode,
                galleryLauncher, images[page], bitmap, context
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserImage(
    isMyProfile: Boolean,
    myUserInfo: UserInfo,
    otherUserInfo: UserInfo,
    page: Int,
    editMode: MutableState<Boolean>,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    imageUri: Uri?,
    bitmap: MutableState<Bitmap?>,
    context: Context
) {
    if (imageUri == null) {
        GlideImage(
            model = if (isMyProfile) {
                if (myUserInfo.images[page] != null) myUserInfo.images[page]
                else null
            } else {
                if (otherUserInfo.images[page] != null) otherUserInfo.images[page]
                else null
            },
            contentDescription = "Profile Image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.65f)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    if (editMode.value) galleryLauncher.launch("image/*")
                }
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
                },
            contentScale = ContentScale.FillBounds
        )
    } else {
        imageUri.let {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }

            bitmap.value?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = "",
                    Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(2.dp, Pink, CircleShape)
                        .clickable { galleryLauncher.launch("image/*") }
                )
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


@OptIn(ExperimentalTextApi::class)
@Composable
fun MatchPercentGuage(
    windowSizeClass: WindowSizeClass,
    progress: Int,
) {
    val fontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 28.sp else 58.sp
    val arcDegrees = 275
    val startArcAngle = 135f
    val startStepAngle = -45
    val numberOfMarkers = 89
    val degreesMarkerStep = arcDegrees / numberOfMarkers
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        onDraw = {
            drawIntoCanvas { canvas ->
                val w = drawContext.size.width
                val h = drawContext.size.height
                val textOffset = if (progress < 10) Offset((w / 2.2f), (h / 2.7f))
                else if (progress in 10..99) Offset(w / 2.4f,h / 2.6f)
                else Offset(
                    if(windowSizeClass == WindowSizeClass.COMPACT) w / 2.8f else w / 2.6f,
                    h / 2.6f
                )
                val quarterOffset =
                    if (windowSizeClass == WindowSizeClass.COMPACT) Offset(w / 4f, h / 4f)
                    else Offset(w / 4f, h / 4f)

                // Drawing Center Arc background
                val (mainColor, secondaryColor) = when {
                    progress < 33 -> // Blue
                        Color(0xFF0703ED) to Color(0xFFE3E2FE)

                    progress < 66 -> // Purple
                        Color(0xFF9300FF) to Color(0xFFF0CEFF)

                    else -> // Pink
                        Pink to Color(0xFFFEDAF0)
                }
                val paint = Paint().apply {
                    color = mainColor
                }
                val centerArcSize = Size(w / 2f, h / 2f)
                val centerArcStroke = Stroke(20f, 0f, StrokeCap.Round)
                drawArc(
                    secondaryColor,
                    startArcAngle,
                    arcDegrees.toFloat() - 5f,
                    false,
                    topLeft = quarterOffset,
                    size = centerArcSize,
                    style = centerArcStroke
                )
                // Drawing Center Arc progress
                drawArc(
                    mainColor,
                    startArcAngle,
                    (degreesMarkerStep * progress * .9f),
                    false,
                    topLeft = quarterOffset,
                    size = centerArcSize,
                    style = centerArcStroke
                )

                val measuredText =
                    textMeasurer.measure(
                        AnnotatedString(progress.toString()),
                        constraints = Constraints.fixed(
                            width = (size.width / 3f).toInt(),
                            height = (size.height / 3f).toInt()
                        ),
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = fontSize,
                            color = mainColor
                        )
                    )
                drawText(measuredText, topLeft = textOffset)

                // Drawing Line Markers
                for ((counter, degrees) in (startStepAngle..(startStepAngle + arcDegrees) step degreesMarkerStep).withIndex()) {
                    val lineEndX = 40f
                    paint.color = mainColor
                    val lineStartX = if (counter % 10 == 0) {
                        paint.strokeWidth = 3f
                        0f
                    } else {
                        paint.strokeWidth = 1f
                        lineEndX * .2f
                    }
                    canvas.save()
                    canvas.rotate(degrees.toFloat(), w / 2f, h / 2f)
                    if (counter % 10 == 0) {
                        canvas.drawLine(
                            Offset(lineStartX + 80, h / 2f),
                            Offset(lineEndX, h / 2f),
                            paint
                        )
                    }
                    canvas.restore()
                }
            }
        }
    )
}

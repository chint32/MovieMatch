package cotey.hinton.moviedate.feature_auth.presentation.screens.create_profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import cotey.hinton.moviedate.Screens
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.google.accompanist.pager.*
import com.google.firebase.firestore.GeoPoint
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.ui.theme.Pink
import cotey.hinton.moviedate.util.WindowSizeClass
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CreateProfileScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: AuthViewModel
) {

    // State
    val genders =
        listOf("Male", "Female", "Transgender", "Gender nonconforming", "Prefer not to say")
    val lookingFor =
        listOf("Chatting", "Friends", "Relationship", "Fun")
    val ages = ArrayList<String>()
    for (i in 15..100) ages.add(i.toString())
    val screenName = remember { mutableStateOf("") }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val sliderState = remember { mutableStateOf(0f) }
    val fontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 16.sp else 26.sp
    val imageSize = if (windowSizeClass == WindowSizeClass.COMPACT) 150.dp else 300.dp
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = 3)

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        viewModel.createProfileState.images[pagerState.currentPage] =
            uri
        viewModel.addImage(uri, pagerState.currentPage)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val mfusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            mfusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result!!
                viewModel.sharedState.userInfo.value.location =
                    GeoPoint(location.latitude, location.longitude)
            }
        } else {
            Toast.makeText(
                context,
                "Location permission denied. You wont be able to proceed " +
                        "until location permission is granted and location is updated",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Tabs(windowSizeClass, pagerState)
        TabsContent(
            imageSize,
            pagerState,
            viewModel.createProfileState.images[pagerState.currentPage],
            galleryLauncher,
            bitmap,
            context
        )
        TextFieldScreenName(fontSize, screenName)
        DropDownItem(fontSize, genders, "Your gender", viewModel, false)
        DropDownItem(fontSize, genders, "Genders interested in", viewModel, true)
        DropDownItem(fontSize, ages, "Your age", viewModel, false)
        DropDownItem(fontSize, ages, "Interested in ages starting at", viewModel, false)
        DropDownItem(fontSize, ages, "Interested in ages ending at", viewModel, false)
        DropDownItem(fontSize, lookingFor, "Looking for", viewModel, true)
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = { getUserLocationWithPermission(context, viewModel, permissionLauncher) },
            modifier = Modifier.fillMaxWidth(.85f),
            shape = CircleShape
        ) {
            Text(text = "Set My Location", color = Color.White, fontSize = fontSize)
        }
        Slider(
            value = sliderState.value,
            onValueChange = {
                sliderState.value = it
                viewModel.sharedState.userInfo.value.searchDistance = it.toInt()
            },
            valueRange = 0f..100f,
            steps = 9,
            modifier = Modifier.fillMaxWidth(.85f),

            )
        Text(
            text = "Search Distance: ${sliderState.value.toInt()}",
            color = if (sliderState.value.toInt() == 0) Color.Gray.copy(
                alpha = .5f
            ) else Color.White,
            fontSize = fontSize
        )
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = {
                viewModel.sharedState.userInfo.value.screenName =
                    screenName.value
                if (validateUserInput(viewModel, context))
                    navController.navigate(Screens.SelectFavoritesScreen.route)
            },
            modifier = Modifier.fillMaxWidth(.85f),
            shape = CircleShape
        ) { Text("Next", color = Color.White, fontSize = fontSize) }
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Composable
fun TextFieldScreenName(fontSize: TextUnit, screenName: MutableState<String>) {
    TextField(
        modifier = Modifier
            .fillMaxWidth(.9f),
        value = screenName.value,
        onValueChange = {
            screenName.value = it
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.White,
            focusedBorderColor = Color.Gray.copy(alpha = .8f),
            unfocusedBorderColor = Color.Gray.copy(alpha = .5f)
        ),
        placeholder = {
            Text(
                text = "Your screen name",
                color = Color.Gray.copy(alpha = .5f),
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                modifier = Modifier.fillMaxWidth()
            )
        },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = fontSize)
    )
}

@Composable
fun DropDownItem(
    fontSize: TextUnit,
    items: List<String>,
    label: String,
    viewModel: AuthViewModel,
    isMultiSelect: Boolean
) {

    var mExpanded by remember { mutableStateOf(false) }
    var mSelectedText by remember { mutableStateOf(label) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
    val dropDownIcon =
        if (mExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    val underlineColor =
        if (mExpanded) Color.Gray.copy(.8f) else Color.Gray.copy(.5f)
    val textColor = if (mSelectedText == label) Color.Gray.copy(.5f) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth(.9f)
            .drawBehind {
                val strokeWidth = 2F
                val y = size.height - strokeWidth / 2
                drawLine(
                    underlineColor,
                    Offset(0f, y),
                    Offset(size.width, y),
                    strokeWidth
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = mSelectedText,
            modifier = Modifier
                .fillMaxWidth(.9f)
                .onGloballyPositioned { coordinates ->
                    mTextFieldSize = coordinates.size.toSize()
                },
            color = textColor,
            fontSize = fontSize,
        )
        IconButton(onClick = { mExpanded = !mExpanded }) {
            Icon(
                imageVector = dropDownIcon,
                contentDescription = "",
                tint = Pink,
            )
        }
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { mTextFieldSize.width.toDp() }),
        ) {

            for (i in items.indices) {
                DropdownMenuItem(onClick = {
                    if (!isMultiSelect) {
                        mSelectedText = items[i]
                        mExpanded = false
                        if (label == "Your gender")
                            viewModel.sharedState.userInfo.value.gender = mSelectedText
                        else if (label == "Your age")
                            viewModel.sharedState.userInfo.value.age = mSelectedText.toInt()
                        else if (label == "Interested in ages starting at")
                            viewModel.sharedState.userInfo.value.startAgeInterestedIn = mSelectedText.toInt()
                        else if (label == "Interested in ages ending at")
                            viewModel.sharedState.userInfo.value.endAgeInterestedIn = mSelectedText.toInt()
                    }
                }) {
                    if (isMultiSelect) {
                        var isChecked by remember {
                            mutableStateOf(
                                if (label == "Genders interested in") {
                                    viewModel.sharedState.userInfo.value.genderInterestedIn.contains(
                                        items[i]
                                    )
                                } else {
                                    viewModel.sharedState.userInfo.value.lookingFor.contains(
                                        items[i]
                                    )
                                }
                            )
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = items[i],
                                modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp),
                                fontSize = fontSize
                            )
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = {
                                    if (label == "Genders interested in") {
                                        if (!viewModel.sharedState.userInfo.value
                                                .genderInterestedIn.contains(items[i])) {
                                                    viewModel.sharedState.userInfo.value
                                                        .genderInterestedIn.add(i, items[i])
                                        }
                                        else viewModel.sharedState.userInfo
                                            .value.genderInterestedIn.removeAt(i)
                                    } else {
                                        if (!viewModel.sharedState.userInfo
                                                .value.lookingFor.contains(items[i])
                                        )
                                            viewModel.sharedState.userInfo
                                                .value.lookingFor.add(i, items[i])
                                        else viewModel.sharedState.userInfo
                                            .value.lookingFor.removeAt(i)
                                    }
                                    isChecked = !isChecked
                                })
                        }
                    }
                    else {  Text(text = items[i], fontSize = fontSize)  }
                }
            }
            if (isMultiSelect) {
                Button(
                    modifier = Modifier.fillMaxWidth().padding(30.dp, 0.dp),
                    onClick = {
                        mSelectedText = ""
                        if (label == "Genders interested in") {
                            if (viewModel.sharedState.userInfo.value.genderInterestedIn.isEmpty()) return@Button
                            for (i in viewModel.sharedState.userInfo.value.genderInterestedIn.indices) {
                                if (viewModel.sharedState.userInfo.value.genderInterestedIn[i] != null) {
                                    mSelectedText += viewModel.sharedState.userInfo.value.genderInterestedIn[i]
                                    mSelectedText += ", "
                                }
                            }
                        } else {
                            if (viewModel.sharedState.userInfo.value.lookingFor.isEmpty()) return@Button
                            for (i in viewModel.sharedState.userInfo.value.lookingFor.indices) {
                                if (viewModel.sharedState.userInfo.value.lookingFor[i] != null) {
                                    mSelectedText += viewModel.sharedState.userInfo.value.lookingFor[i]
                                    mSelectedText += ", "
                                }
                            }
                        }
                        mSelectedText = mSelectedText.substring(0, mSelectedText.length - 2)
                        mExpanded = false
                    },
                    shape = CircleShape
                ) {
                    Text(text = "Accept", color = Color.White, fontSize = fontSize)
                }
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
fun Tabs(windowSizeClass: WindowSizeClass, pagerState: PagerState) {

    val list = listOf("Profile", "Image 2", "Image 3")
    val selectedTabFontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 26.sp else 46.sp
    val unselectedTabFontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 20.sp else 40.sp
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 2.dp,
                color = Color.Transparent
            )
        },
    ) {
        list.forEachIndexed { index, _ ->
            Tab(
                text = {
                    Column() {
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
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(index) }
                }
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabsContent(
    imageSize: Dp,
    pagerState: PagerState,
    imageUri: Uri?,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    bitmap: MutableState<Bitmap?>,
    context: Context
) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> UserImage(
                imageSize,
                imageUri = imageUri,
                galleryLauncher = galleryLauncher,
                bitmap = bitmap,
                context = context,
            )

            1 -> UserImage(
                imageSize,
                imageUri = imageUri,
                galleryLauncher = galleryLauncher,
                bitmap = bitmap,
                context = context,
            )

            2 -> UserImage(
                imageSize,
                imageUri = imageUri,
                galleryLauncher = galleryLauncher,
                bitmap = bitmap,
                context = context,
            )
        }
    }
}

@Composable
fun UserImage(
    imageSize: Dp,
    imageUri: Uri?,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    bitmap: MutableState<Bitmap?>,
    context: Context
) {
    if (imageUri == null) {
        Icon(
            imageVector = Icons.Default.Person,
            tint = Pink,
            contentDescription = "",
            modifier = Modifier
                .size(imageSize)
                .clip(CircleShape)
                .border(2.dp, Pink, CircleShape)
                .clickable {
                    galleryLauncher.launch("image/*")
                }
        )
    } else {
        imageUri.let {
            if (Build.VERSION.SDK_INT < 28)
                bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }
            bitmap.value?.let { btm ->
                Image(
                    bitmap = btm.asImageBitmap(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(imageSize)
                        .clip(CircleShape)
                        .border(2.dp, Pink, CircleShape)
                        .clickable {
                            galleryLauncher.launch("image/*")
                        }
                )
            }
        }
    }
}

fun getUserLocationWithPermission(
    context: Context,
    viewModel: AuthViewModel,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) -> {
            Toast.makeText(context, "Location updated", Toast.LENGTH_SHORT).show()
            val mfusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context)
            mfusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                viewModel.sharedState.userInfo.value.location =
                    GeoPoint(location.latitude, location.longitude)
            }
        }

        else -> {
            // Asking for permission
            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
}

fun validateUserInput(
    viewModel: AuthViewModel,
    context: Context
): Boolean {
    if (viewModel.createProfileState.images[0] == null) {
        Toast.makeText(context, "Profile picture must not be blank", Toast.LENGTH_SHORT).show()
        return false
    } else if (viewModel.sharedState.userInfo.value.screenName.isBlank()) {
        Toast.makeText(context, "Screen name must not be blank", Toast.LENGTH_SHORT).show()
        return false
    } else if (viewModel.sharedState.userInfo.value.gender.isBlank()) {
        Toast.makeText(context, "Your gender must not be blank", Toast.LENGTH_SHORT).show()
        return false
    } else if (viewModel.sharedState.userInfo.value.genderInterestedIn.isEmpty()) {
        Toast.makeText(
            context, "You must select which genders you're interested in", Toast.LENGTH_SHORT
        ).show()
        return false
    } else if (viewModel.sharedState.userInfo.value.age == -1) {
        Toast.makeText(context, "Your age must not be blank", Toast.LENGTH_SHORT).show()
        return false
    } else if (viewModel.sharedState.userInfo.value.startAgeInterestedIn == -1) {
        Toast.makeText(
            context, "Starting age you're interested in must not be blank", Toast.LENGTH_SHORT
        ).show()
        return false
    } else if (viewModel.sharedState.userInfo.value.endAgeInterestedIn == -1) {
        Toast.makeText(
            context, "Ending age you're interested in must not be blank", Toast.LENGTH_SHORT
        ).show()
        return false
    } else if (viewModel.sharedState.userInfo.value.location.latitude == 0.0 &&
        viewModel.sharedState.userInfo.value.location.longitude == 0.0
    ) {
        Toast.makeText(context, "Location must be set", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}





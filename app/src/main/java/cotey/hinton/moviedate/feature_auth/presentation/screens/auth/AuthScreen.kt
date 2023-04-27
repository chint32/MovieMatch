package cotey.hinton.moviedate.feature_auth.presentation.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.presentation.AuthActivity
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.feature_main.presentation.MainActivity
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.ui.theme.Pink
import cotey.hinton.moviedate.util.WindowSizeClass
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AuthScreen(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
    viewModel: AuthViewModel

) {
    // State
    val mContext = LocalContext.current
    val authActivity = (mContext as AuthActivity)
    val pagerState = rememberPagerState(pageCount = 2)
    val confirmExitAppDialog = remember { mutableStateOf(false) }
    val loginSuccessAnim1: MutableTransitionState<Boolean> =
        MutableTransitionState(false).apply {
            targetState = false
        }
    val loginSuccessAnim2: MutableTransitionState<Boolean> =
        MutableTransitionState(false).apply {
            targetState = false
        }
    val contentAlpha = if (viewModel.authState.isLoading.value) .5f else 1f

    BackHandler() { confirmExitAppDialog.value = true }
    handleLoginErrors(mContext, viewModel)
    handleLoginSuccess(loginSuccessAnim1, viewModel)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            Tabs(windowSizeClass, pagerState)
            TabsContent(
                windowSizeClass,
                viewModel,
                pagerState,
                navController,
            )
        }
        if (viewModel.authState.isLoading.value) {
            ProgressIndicatorClickDisabled()
        }
        LoginSuccessAnimation(loginSuccessAnim1, loginSuccessAnim2, mContext)
        ConfirmCloseAppDialog(windowSizeClass, authActivity, confirmExitAppDialog)
    }
}

@Composable
fun ConfirmCloseAppDialog(
    windowSizeClass: WindowSizeClass,
    authActivity: AuthActivity,
    confirmExitAppDialog: MutableState<Boolean>
) {
    val exitDialogTitleFontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 20.sp else 30.sp
    val exitDialogButtonFontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 16.sp else 26.sp

    MaterialTheme {
        Column() {
            if (confirmExitAppDialog.value) {
                AlertDialog(
                    onDismissRequest = { confirmExitAppDialog.value = false },
                    title = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Exit Movie Match?",
                            fontSize = exitDialogTitleFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    },
                    confirmButton = {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp, 20.dp, 30.dp, 20.dp),
                            onClick = {
                                confirmExitAppDialog.value = false
                                authActivity.finish()
                                exitProcess(0)
                            }) {
                            Text("Confirm", fontSize = exitDialogButtonFontSize)
                        }
                    }
                )
            }
        }
    }
}

private fun handleLoginSuccess(
    loginSuccessAnim1: MutableTransitionState<Boolean>,
    viewModel: AuthViewModel
) {
    when (viewModel.authState.authResult.value) {
        null -> {}
        else -> {
            loginSuccessAnim1.apply {
                targetState = true
            }
        }
    }
}

private fun handleLoginErrors(mContext: Context, viewModel: AuthViewModel) {
    when (viewModel.authState.error.value) {
        "ERROR_INVALID_EMAIL" -> {
            Toast.makeText(mContext, "Error: Invalid email address", Toast.LENGTH_SHORT).show()
            viewModel.authState.error.value = null
        }

        "ERROR_WRONG_PASSWORD" -> {
            Toast.makeText(mContext, "Error: Invalid password", Toast.LENGTH_SHORT).show()
            viewModel.authState.error.value = null
        }

        else -> {
            println(viewModel.authState.error.value)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalPagerApi
@Composable
fun Tabs(windowSizeClass: WindowSizeClass, pagerState: PagerState) {

    val tabLabels = listOf("Login", "Register")
    val selectedTabFontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 36.sp else 46.sp
    val unselectedTabFontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 30.sp else 40.sp
    val animationScope = rememberCoroutineScope()

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
        tabLabels.forEachIndexed { index, _ ->
            Tab(
                text = {
                    Column() {
                        if ((pagerState.currentPage == index)) {
                            Text(
                                tabLabels[index],
                                color = Color.White,
                                modifier = Modifier.alpha(1f),
                                fontSize = selectedTabFontSize,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        } else {
                            Text(
                                tabLabels[index],
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
                    animationScope.launch {
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
    viewModel: AuthViewModel,
    pagerState: PagerState,
    navController: NavController,
) {
    HorizontalPager(pagerState) { page ->
        when (page) {
            0 -> {
                AuthenticationContent(windowSizeClass, viewModel, navController, false)
            }

            1 -> {
                AuthenticationContent(windowSizeClass, viewModel, navController, true)
            }
        }
    }
}

@Composable
fun AuthenticationContent(
    windowSizeClass: WindowSizeClass,
    viewModel: AuthViewModel,
    navController: NavController,
    isNewUserRegister: Boolean,
) {
    val context = LocalContext.current
    val fontSize = if (windowSizeClass == WindowSizeClass.COMPACT) 22.sp else 36.sp
    val iconSize = if (windowSizeClass == WindowSizeClass.COMPACT) 24.dp else 36.dp
    val loginButtonSize = if (windowSizeClass == WindowSizeClass.COMPACT) 70.dp else 120.dp
    val loginButtonIconSize = if (windowSizeClass == WindowSizeClass.COMPACT) 50.dp else 90.dp
    val showPw = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = "Email",
            color = Pink,
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp, 0.dp),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp, 0.dp),
            value = viewModel.authState.email.value,
            onValueChange = { newText ->
                viewModel.authState.email.value = newText
            },

            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color.Gray.copy(alpha = .8f),
                unfocusedBorderColor = Color.Gray.copy(alpha = .5f)
            ),
            textStyle = TextStyle(fontSize = fontSize),
            placeholder = {
                Text(
                    text = "Example@test.com",
                    color = Color.Gray.copy(alpha = .5f),
                    fontSize = fontSize,
                )
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Password",
            color = Pink,
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp, 0.dp),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp, 0.dp),
            value = viewModel.authState.pw.value,
            onValueChange = { newText ->
                viewModel.authState.pw.value = newText
            },
            visualTransformation = if (showPw.value) VisualTransformation.None else PasswordVisualTransformation(),
            placeholder = {
                Text(
                    text = "Password",
                    color = Color.Gray.copy(alpha = .5f),
                    fontSize = fontSize,
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color.Gray.copy(alpha = .8f),
                unfocusedBorderColor = Color.Gray.copy(alpha = .5f)

            ),
            textStyle = TextStyle(fontSize = fontSize),
            trailingIcon = {
                IconButton(onClick = { showPw.value = !showPw.value }) {
                    if (showPw.value)
                        Icon(
                            Icons.Outlined.Visibility, contentDescription = null, tint = Pink,
                            modifier = Modifier.size(iconSize)
                        )
                    else
                        Icon(
                            Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = Pink.copy(.7f),
                            modifier = Modifier.size(iconSize)
                        )
                }
            }
        )
        Spacer(modifier = Modifier.height(100.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp, 0.dp),
            horizontalAlignment = Alignment.End
        ) {
            Button(
                modifier = Modifier.size(loginButtonSize),
                colors = ButtonDefaults.buttonColors(backgroundColor = Pink),
                onClick = {
                    if (!isUserInputValid(
                            context,
                            viewModel.authState.email.value,
                            viewModel.authState.email.value
                        )
                    )
                        return@Button
                    if (isNewUserRegister) navController.navigate(Screens.CreateProfileScreen.route)
                    else viewModel.login()

                },
                enabled = true,
                shape = CircleShape,
            )
            {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    modifier = Modifier.size(loginButtonIconSize),
                    tint = Color.White,
                    contentDescription = null
                )
            }
        }
    }
}

fun isUserInputValid(context: Context, email: String, pw: String): Boolean {
    if (email.isBlank()) {
        Toast.makeText(context, "Email must not be blank.", Toast.LENGTH_SHORT).show()
        return false
    } else if (pw.isBlank()) {
        Toast.makeText(context, "Password must not be blank.", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoginSuccessAnimation(
    loginSuccessAnim1: MutableTransitionState<Boolean>,
    loginSuccessAnim2: MutableTransitionState<Boolean>,
    mContext: Context,
) {
    AnimatedVisibility(
        visibleState = loginSuccessAnim1,
        enter = scaleIn(tween(700)),
    ) {
        when {
            loginSuccessAnim1.isIdle && loginSuccessAnim1.currentState -> {
                loginSuccessAnim2.targetState = true
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
                visibleState = loginSuccessAnim2,
                enter = scaleIn(tween(1000))
            ) {
                when {
                    loginSuccessAnim2.isIdle && loginSuccessAnim2.currentState -> {
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





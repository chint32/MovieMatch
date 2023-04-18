package cotey.hinton.moviedate.feature_auth.presentation.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.*
import cotey.hinton.moviedate.R
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_auth.presentation.AuthActivity
import cotey.hinton.moviedate.feature_auth.presentation.viewmodel.AuthViewModel
import cotey.hinton.moviedate.feature_main.presentation.MainActivity
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.ui.theme.Pink
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPagerApi::class)
@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel

) {
    val mContext = LocalContext.current
    val authActivity = (mContext as AuthActivity)
    val openDialog = remember { mutableStateOf(false)  }
    BackHandler(){
        openDialog.value = true
    }
    handleLoginErrors(mContext, viewModel)
    handleLoginSuccess(viewModel)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (viewModel.authState.isLoading.value) .5f else 1f)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            Tabs(pagerState = viewModel.authState.pagerState)
            TabsContent(
                viewModel,
                navController = navController,
            )
        }
        if (viewModel.authState.isLoading.value) {
            ProgressIndicatorClickDisabled()
        }
        LoginSuccessAnimation(mContext = mContext, viewModel)
        ConfirmCloseAppDialog(authActivity = authActivity, openDialog = openDialog)
    }
}

@Composable
fun ConfirmCloseAppDialog(authActivity: AuthActivity, openDialog: MutableState<Boolean>) {
    MaterialTheme {
        Column() {
            if (openDialog.value) {

                AlertDialog(
                    onDismissRequest = { openDialog.value = false },
                    title = { Text(modifier = Modifier.fillMaxWidth(), text = "Exit Movie Match?", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center) },
                    confirmButton = {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp, 20.dp, 30.dp, 20.dp),
                            onClick = {
                                openDialog.value = false
                                authActivity.finish()
                                exitProcess(0)
                            }) {
                            Text("Confirm")
                        }
                    }
                )
            }
        }
    }
}

private fun handleLoginSuccess(viewModel: AuthViewModel){
    when (viewModel.authState.authResult.value) {
        null -> {}
        else -> {
            viewModel.authState.loginSuccessAnim1.apply {
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
fun Tabs(pagerState: PagerState) {

    val list = listOf("Login", "Register")
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
    HorizontalPager(state = viewModel.authState.pagerState) { page ->
        when (page) {
            0 -> {
                AuthenticationContent(viewModel, navController, false)
            }
            1 -> {
                AuthenticationContent(viewModel, navController, true)
            }
        }
    }
}

@Composable
fun AuthenticationContent(
    viewModel: AuthViewModel,
    navController: NavController,
    isNewUserRegister: Boolean,
) {
    val context = LocalContext.current
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
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        TextField(
            value = viewModel.authState.email.value,
            onValueChange = { newText ->
                viewModel.authState.email.value = newText
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color.Gray.copy(alpha = .8f),
                unfocusedBorderColor = Color.Gray.copy(alpha = .5f)
            ),
            placeholder = { Text(text = "Example@test.com", color = Color.Gray.copy(alpha = .5f)) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Password",
            color = Pink,
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp, 0.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = viewModel.authState.pw.value,
            onValueChange = { newText ->
                viewModel.authState.pw.value = newText
            },
            visualTransformation = if (viewModel.authState.showPw.value) VisualTransformation.None else PasswordVisualTransformation(),
            placeholder = {
                Text(
                    text = "Password",
                    color = Color.Gray.copy(alpha = .5f)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color.Gray.copy(alpha = .8f),
                unfocusedBorderColor = Color.Gray.copy(alpha = .5f)

            ),
            trailingIcon = {
                IconButton(onClick = { viewModel.authState.showPw.value = !viewModel.authState.showPw.value }) {
                    if (viewModel.authState.showPw.value)
                        Icon(Icons.Outlined.Visibility, contentDescription = null, tint = Pink)
                    else
                        Icon(Icons.Filled.VisibilityOff, contentDescription = null, tint = Pink.copy(.7f))
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
                modifier = Modifier.size(70.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Pink
                ),
                onClick = {
                    if (!isUserInputValid(context, viewModel.authState.email.value, viewModel.authState.email.value))
                        return@Button
                    if (isNewUserRegister)
                        navController.navigate(Screens.CreateProfileScreen.route)
                    else
                        viewModel.login()

                },
                enabled = true,
                shape = CircleShape,
            )
            {
                Icon(imageVector = Icons.Filled.ArrowForward,  modifier = Modifier.size(50.dp), tint = Color.White, contentDescription = null)
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
fun LoginSuccessAnimation(mContext: Context, viewModel: AuthViewModel){
    AnimatedVisibility(
        visibleState = viewModel.authState.loginSuccessAnim1,
        enter = scaleIn(tween(700)),
    ) {
        when {
            viewModel.authState.loginSuccessAnim1.isIdle && viewModel.authState.loginSuccessAnim1.currentState -> {
                viewModel.authState.loginSuccessAnim2.targetState = true
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
                visibleState = viewModel.authState.loginSuccessAnim2,
                enter = scaleIn(tween(1000))
            ) {
                when {
                    viewModel.authState.loginSuccessAnim2.isIdle && viewModel.authState.loginSuccessAnim2.currentState -> {
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





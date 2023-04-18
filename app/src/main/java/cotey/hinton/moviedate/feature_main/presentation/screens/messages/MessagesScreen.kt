package cotey.hinton.moviedate.feature_main.presentation.screens.messages

import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import cotey.hinton.moviedate.feature_main.domain.models.ImageMessage
import cotey.hinton.moviedate.feature_main.domain.models.TextMessage
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.ui.theme.Pink
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MessagesScreen(viewModel: MainViewModel) {

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        sendImageMessage(viewModel, uri)
    }

    val listState = rememberLazyListState()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.85f)
                .alpha(if (viewModel.mainScreenState.isLoading.value) .5f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LaunchedEffect(viewModel.messagesScreenState.messages.size) {
                listState.animateScrollToItem(viewModel.messagesScreenState.messages.size)
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.9f),
                state = listState
            ) {
                items(viewModel.messagesScreenState.messages) { message ->
                    MessageItem(viewModel, message)
                }
            }

            MessageTextField(viewModel, galleryLauncher)
        }
        if(viewModel.messagesScreenState.isLoading.value)
            ProgressIndicatorClickDisabled()
    }
}

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalTextApi::class)
@Composable
fun MessageTextField(
    viewModel: MainViewModel,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>
) {

    val gradientColors = listOf(Color.Cyan, Color.Blue, Color.Magenta)
    val brush = Brush.linearGradient(colors = gradientColors)
    TextField(
        value = viewModel.messagesScreenState.messageText.value,
        onValueChange = { viewModel.messagesScreenState.messageText.value = it },
        textStyle = TextStyle(brush = brush),
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = Pink,
            focusedIndicatorColor = Pink,
            unfocusedIndicatorColor = Pink
        ),
        leadingIcon = {
            IconButton(modifier = Modifier.testTag("send_image_icon"), onClick = {
                galleryLauncher.launch("image/*")
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    tint = Pink,
                    contentDescription = "Send Message"
                )
            }
        },
        trailingIcon = {
            IconButton(modifier = Modifier.testTag("send_text_icon"), onClick = {
                sendTextMessage(viewModel)
            }) {
                Icon(
                    imageVector = Icons.Default.Send,
                    tint = Pink,
                    contentDescription = "Send Message"
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@RequiresApi(Build.VERSION_CODES.N)
private fun sendTextMessage(
    viewModel: MainViewModel
) {
    val message = TextMessage(
        UUID.randomUUID().toString(),
        viewModel.sharedState.myUserInfo.value.uid,
        viewModel.sharedState.myUserInfo.value.screenName,
        viewModel.sharedState.myUserInfo.value.images[0]!!,
        viewModel.sharedState.otherUserInfo.value.uid,
        viewModel.sharedState.otherUserInfo.value.screenName,
        viewModel.sharedState.otherUserInfo.value.images[0]!!,
        Calendar.getInstance().time.toString(),
        "TEXT",
        viewModel.messagesScreenState.messageText.value
    )
    viewModel.sendTextMessage(message)
    if (!viewModel.messagesScreenState.messages.contains(message))
        viewModel.messagesScreenState.messages.add(message)
    viewModel.messagesScreenState.messageText.value = ""
}

@RequiresApi(Build.VERSION_CODES.N)
private fun sendImageMessage(
    viewModel: MainViewModel,
    uri: Uri?
) {
    viewModel.messagesScreenState.imageUri.value = uri
    val message = ImageMessage(
        UUID.randomUUID().toString(),
        viewModel.sharedState.myUserInfo.value.uid,
        viewModel.sharedState.myUserInfo.value.screenName,
        viewModel.sharedState.myUserInfo.value.images[0]!!,
        viewModel.sharedState.otherUserInfo.value.uid,
        viewModel.sharedState.otherUserInfo.value.screenName,
        viewModel.sharedState.otherUserInfo.value.images[0]!!,
        Calendar.getInstance().time.toString(),
        "IMAGE",
        ""
    )
    viewModel.sendImageMessage(message, uri!!)
}
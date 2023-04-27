package cotey.hinton.moviedate.feature_main.presentation.screens.conversations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import cotey.hinton.moviedate.Screens
import cotey.hinton.moviedate.feature_main.domain.models.Conversation
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel
import cotey.hinton.moviedate.util.WindowSizeClass

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ConversationItem(
    windowSizeClass: WindowSizeClass,
    conversation: Conversation,
    navController: NavController,
    viewModel: MainViewModel
) {

    val fontSize = if(windowSizeClass == WindowSizeClass.COMPACT) 16.sp else 26.sp
    val imageSize = if(windowSizeClass == WindowSizeClass.COMPACT) 80.dp else 140.dp

    Box(
        Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))) {
        Row(modifier = Modifier
            .background(Color.DarkGray.copy(.7f))
            .clickable {
                viewModel.sharedState.otherUserInfo.value = conversation.otherUserInfo
                viewModel.listenForMessages(conversation.otherUserInfo.uid)
                navController.navigate(Screens.MessagesScreen.route)
            }
            .fillMaxSize()
            .padding(10.dp)) {
            GlideImage(
                model = conversation.otherUserInfo.images[0],
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(imageSize)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(16.dp, 0.dp, 10.dp, 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = conversation.otherUserInfo.screenName,
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        fontSize = fontSize
                    )
                    Text(
                        text = conversation.time.substring(11, 16),
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        color = Color.White,
                        textAlign = TextAlign.End,
                        fontSize = fontSize
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = conversation.lastMessage,
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    fontSize = fontSize
                )
            }
        }
    }
}

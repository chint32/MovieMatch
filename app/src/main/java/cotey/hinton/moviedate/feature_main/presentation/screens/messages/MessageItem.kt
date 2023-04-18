package cotey.hinton.moviedate.feature_main.presentation.screens.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import cotey.hinton.moviedate.feature_main.domain.models.ImageMessage
import cotey.hinton.moviedate.feature_main.domain.models.Message
import cotey.hinton.moviedate.feature_main.domain.models.TextMessage
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MessageItem(viewModel: MainViewModel, message: Message) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment =
        if (viewModel.sharedState.myUserInfo.value.uid == message.senderId)
            Alignment.CenterEnd
        else Alignment.CenterStart
    ) {
        Card(
            backgroundColor = Color.DarkGray.copy(.9f),
            modifier = Modifier
                .fillMaxWidth(.8f)
                .clip(RoundedCornerShape(6.dp))
                .padding(8.dp)
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                if (message.type == "TEXT") {
                    Text(
                        text = (message as TextMessage).text,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = if (viewModel.sharedState.myUserInfo.value.uid == message.senderId)
                            TextAlign.End
                        else TextAlign.Start
                    )
                } else {
                    GlideImage(
                        modifier = Modifier.fillMaxWidth(),
                        model = (message as ImageMessage).imageUrl,
                        contentDescription = "Profile Image",
                    )
                }
                Text(
                    text = message.time.substring(11,16),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Gray,
                    textAlign = if (viewModel.sharedState.myUserInfo.value.uid == message.senderId)
                        TextAlign.End
                    else TextAlign.Start
                )
            }
        }
    }
}
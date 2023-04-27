package cotey.hinton.moviedate.feature_main.presentation.screens.messages

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cotey.hinton.moviedate.feature_main.domain.models.Message

data class MessagesScreenState(
    val messages: SnapshotStateList<Message> = mutableStateListOf(),
    val isLoading: MutableState<Boolean> = mutableStateOf(false)
)

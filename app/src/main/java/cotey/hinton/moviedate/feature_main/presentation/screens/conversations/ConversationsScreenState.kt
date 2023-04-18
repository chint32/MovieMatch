package cotey.hinton.moviedate.feature_main.presentation.screens.conversations

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import cotey.hinton.moviedate.feature_main.domain.models.Conversation

data class ConversationsScreenState(
    val conversations: SnapshotStateList<Conversation> = mutableStateListOf<Conversation>(),
    var isLoading : MutableState<Boolean> = mutableStateOf(false)
)

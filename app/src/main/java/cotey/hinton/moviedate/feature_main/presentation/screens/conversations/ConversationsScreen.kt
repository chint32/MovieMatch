package cotey.hinton.moviedate.feature_main.presentation.screens.conversations

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.navigation.NavController
import cotey.hinton.moviedate.feature_main.presentation.screens.shared.components.ProgressIndicatorClickDisabled
import cotey.hinton.moviedate.feature_main.presentation.viewmodel.MainViewModel

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ConversationsScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val hasBeenCalled = remember { mutableStateOf(false) }
    if (!hasBeenCalled.value) {
        viewModel.getConversations()
        hasBeenCalled.value = true
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxSize()
                .alpha(if (viewModel.conversationsScreenState.isLoading.value) .5f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(viewModel.conversationsScreenState.conversations) { conversation ->
                    ConversationItem(conversation, navController, viewModel)
                }
            }
        }
        if (viewModel.conversationsScreenState.isLoading.value)
            ProgressIndicatorClickDisabled()
    }
}

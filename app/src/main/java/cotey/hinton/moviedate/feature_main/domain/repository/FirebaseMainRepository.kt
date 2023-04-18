package cotey.hinton.moviedate.feature_main.domain.repository

import android.net.Uri
import androidx.compose.runtime.snapshots.SnapshotStateList
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo
import cotey.hinton.moviedate.feature_main.domain.models.Conversation
import cotey.hinton.moviedate.feature_main.domain.models.ImageMessage
import cotey.hinton.moviedate.feature_main.domain.models.Message
import cotey.hinton.moviedate.feature_main.domain.models.TextMessage
import kotlinx.coroutines.flow.Flow

interface FirebaseMainRepository {

    fun getUsersNearby(myUserInfo: UserInfo) : Flow<List<Pair<UserInfo, Int>>>

    fun getUserInfo(uid: String): Flow<UserInfo>

    fun getUsersInfo(listOfIds: List<String>): Flow<List<UserInfo>>

    fun updateUserInfo(userInfo: UserInfo, images: SnapshotStateList<Uri?>?) : Flow<Boolean>

    fun getConversations(): Flow<List<Conversation>>

    fun listenForMessages(uid: String, otherUid: String) : Flow<List<Message>>

    fun sendTextMessage(myUserInfo: UserInfo, otherUserInfo: UserInfo, message: TextMessage): Flow<Boolean>

    fun sendImageMessage(myUserInfo: UserInfo, otherUserInfo: UserInfo, message: ImageMessage, image: Uri) : Flow<Boolean>

    fun updateLikeToMatch(myUid: String, otherUid: String): Flow<Boolean>

    fun getAffections(myUid: String) : Flow<Pair<List<Pair<String, Boolean>>, List<Pair<String, Boolean>>>?>

    fun addLike(myUserInfo: UserInfo, otherUserInfo: UserInfo) : Flow<Boolean>

    fun acknowledgeNewMatches(myUid: String) : Flow<Boolean>

    fun acknowledgeNewLikes(myUid: String) : Flow<Boolean>


}
package cotey.hinton.moviedate.feature_main.domain.models

import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo

data class Conversation(
    val otherUserInfo: UserInfo = UserInfo(),
    val lastMessage: String = "",
    val time: String = ""
)

package cotey.hinton.moviedate.feature_main.domain.models


data class TextMessage(
    override val messageId: String = "",
    override val senderId: String = "",
    override val senderScreenName: String = "",
    override val senderProfilePic: String = "",
    override val recipientId: String = "",
    override val recipientScreenName: String = "",
    override val receiverProfilePic: String = "",
    override val time: String = "",
    override val type: String = "",
    val text: String = ""
) : Message(messageId, senderId, recipientId, time, type)

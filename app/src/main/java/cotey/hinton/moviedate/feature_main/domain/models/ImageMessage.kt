package cotey.hinton.moviedate.feature_main.domain.models

data class ImageMessage(
    override val messageId: String = "",
    override val senderId: String = "",
    override val senderScreenName: String = "",
    override val senderProfilePic: String = "",
    override val recipientId: String = "",
    override val recipientScreenName: String = "",
    override val receiverProfilePic: String = "",
    override val time: String = "",
    override val type: String = "",
    var imageUrl: String = ""
) : Message(messageId, senderId, recipientId, time, type)

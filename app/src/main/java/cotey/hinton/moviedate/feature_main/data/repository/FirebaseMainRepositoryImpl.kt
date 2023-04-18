package cotey.hinton.moviedate.feature_main.data.repository

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo
import cotey.hinton.moviedate.feature_main.domain.models.Conversation
import cotey.hinton.moviedate.feature_main.domain.models.ImageMessage
import cotey.hinton.moviedate.feature_main.domain.models.Message
import cotey.hinton.moviedate.feature_main.domain.models.TextMessage
import cotey.hinton.moviedate.feature_main.domain.repository.FirebaseMainRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseMainRepositoryImpl : FirebaseMainRepository {
    override fun getUsersNearby(myUserInfo: UserInfo): Flow<List<Pair<UserInfo, Int>>> = flow {
        val milesPerLat = 0.0144927536231884
        val milesPerLon = 0.0181818181818182

        val lowerLatBound = myUserInfo.location.latitude - (milesPerLat * myUserInfo.searchDistance)
        val lowerLonBound = myUserInfo.location.longitude - (milesPerLon * myUserInfo.searchDistance)
        val upperLatBound = myUserInfo.location.latitude + (milesPerLat * myUserInfo.searchDistance)
        val upperLonBound = myUserInfo.location.longitude + (milesPerLon * myUserInfo.searchDistance)

        val lowerGeoBound = GeoPoint(lowerLatBound, lowerLonBound)
        val upperGeoBound = GeoPoint(upperLatBound, upperLonBound)

        try {
            val documents = Firebase.firestore.collection("users")
                .whereGreaterThanOrEqualTo("location", lowerGeoBound)
                .whereLessThanOrEqualTo("location", upperGeoBound)
                .get().await().documents

            val users = ArrayList<Pair<UserInfo, Int>>()
            for (doc in documents) {
                val otherUserInfo = doc.toObject(UserInfo::class.java) ?: continue
                if (!myUserInfo.genderInterestedIn.contains(otherUserInfo.gender)) continue
                if (otherUserInfo.age > myUserInfo.endAgeInterestedIn) continue
                if (otherUserInfo.age < myUserInfo.startAgeInterestedIn) continue
                if (myUserInfo.dislikes.contains(otherUserInfo.uid)) continue
                if (myUserInfo.likes.contains(otherUserInfo.uid)) continue
                if (otherUserInfo.uid == myUserInfo.uid) continue
                val distance = distance(
                    myUserInfo.location.latitude.toFloat(),
                    myUserInfo.location.longitude.toFloat(),
                    otherUserInfo.location.latitude.toFloat(),
                    otherUserInfo.location.longitude.toFloat()
                )
                if (distance > myUserInfo.searchDistance) continue
                users.add(Pair(otherUserInfo, distance.toInt()))
            }
            emit(users)
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
        }
    }

    private fun distance(lat_a: Float, lng_a: Float, lat_b: Float, lng_b: Float): Float {
        val earthRadius = 3958.75
        val latDiff = Math.toRadians((lat_b - lat_a).toDouble())
        val lngDiff = Math.toRadians((lng_b - lng_a).toDouble())
        val a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a.toDouble())) * Math.cos(Math.toRadians(lat_b.toDouble())) *
                Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = earthRadius * c

        return distance.toFloat()
    }

    override fun getUserInfo(uid: String): Flow<UserInfo> = flow {
        val user = Firebase.firestore.collection("users").document(uid).get().await()
            .toObject<UserInfo>()!!
        emit(user)
    }

    override fun getUsersInfo(listOfIds: List<String>): Flow<List<UserInfo>> = flow {
        emit(
            listOfIds.map {
                Firebase.firestore.collection("users").document(it).get().await()
                    .toObject<UserInfo>()!!
            }
        )
    }

    override fun updateUserInfo(
        userInfo: UserInfo,
        images: SnapshotStateList<Uri?>?
    ): Flow<Boolean> = flow {
        val uid = if(userInfo.uid == "")
            FirebaseAuth.getInstance().currentUser!!.uid
        else userInfo.uid
        try {
            if (images != null) {
                for (i in images.indices) {
                    if (images[i] != null) {
                        val upload = FirebaseStorage.getInstance().reference.child("users")
                            .child(uid).child("images")
                            .child("image $i").putFile(images[i]!!).await()
                        val downloadUrl = upload.storage.downloadUrl.await().toString()
                        userInfo.images[i] = downloadUrl
                    }
                }
            }
            Firebase.firestore.collection("users").document(uid)
                .set(userInfo)
            emit(true)
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
            emit(false)
        }
    }

    override fun getConversations(): Flow<List<Conversation>> = flow {
        try {
            val documents = Firebase.firestore.collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("conversations")
                .get().await().documents
            emit(
                documents.map {
                    it.toObject<Conversation>()!!
                }
            )
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
        }
    }

    override fun listenForMessages(uid: String, otherUid: String): Flow<List<Message>> =
        callbackFlow {
            try {
                val messagesCollection = Firebase.firestore.collection("users")
                    .document(uid).collection("conversations")
                    .document(otherUid).collection("messages")
                    .orderBy("time", Query.Direction.ASCENDING)
                val subscription = messagesCollection.addSnapshotListener { snapshot, _ ->
                    if (snapshot == null || snapshot.documents.isEmpty()) return@addSnapshotListener
                    trySend(snapshot.documents.map {
                        if (it["type"].toString() == "TEXT")
                            it.toObject<TextMessage>()!!
                        else
                            it.toObject<ImageMessage>()!!
                    })
                }
                awaitClose { subscription.remove() }
            } catch (e: FirebaseFirestoreException) {
                e.printStackTrace()
            }
        }

    override fun sendTextMessage(
        myUserInfo: UserInfo,
        otherUserInfo: UserInfo,
        message: TextMessage
    ): Flow<Boolean> = flow {

        var conversation = Conversation(otherUserInfo, message.text, message.time)
        try {
            Firebase.firestore.collection("users")
                .document(myUserInfo.uid).collection("conversations")
                .document(otherUserInfo.uid).set(conversation)

            Firebase.firestore.collection("users")
                .document(myUserInfo.uid).collection("conversations")
                .document(otherUserInfo.uid).collection("messages")
                .document(message.messageId).set(message)

            conversation = Conversation(
                myUserInfo,
                message.text,
                message.time
            )

            Firebase.firestore.collection("users")
                .document(otherUserInfo.uid).collection("conversations")
                .document(myUserInfo.uid).set(conversation)

            Firebase.firestore.collection("users")
                .document(otherUserInfo.uid).collection("conversations")
                .document(myUserInfo.uid).collection("messages")
                .document(message.messageId).set(message)

            emit(true)
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
            emit(false)
        }
    }

    override fun sendImageMessage(
        myUserInfo: UserInfo,
        otherUserInfo: UserInfo,
        message: ImageMessage,
        image: Uri
    ): Flow<Boolean> = flow {

        var conversation = Conversation(otherUserInfo, "Image", message.time)
        try {
            Firebase.firestore.collection("users")
                .document(myUserInfo.uid).collection("conversations")
                .document(otherUserInfo.uid).set(conversation)

            var upload = FirebaseStorage.getInstance().reference.child("users")
                .child(otherUserInfo.uid)
                .child("conversations").child(otherUserInfo.uid)
                .child("messages").child(message.messageId)
                .putFile(image).await()

            message.imageUrl = upload.storage.downloadUrl.await().toString()

            Firebase.firestore.collection("users")
                .document(myUserInfo.uid).collection("conversations")
                .document(otherUserInfo.uid).collection("messages")
                .document(message.messageId).set(message)

            conversation = Conversation(myUserInfo, "Image", message.time)

            Firebase.firestore.collection("users")
                .document(otherUserInfo.uid).collection("conversations")
                .document(myUserInfo.uid).set(conversation)

            upload = FirebaseStorage.getInstance().reference.child("users")
                .child(otherUserInfo.uid)
                .child("conversations").child(myUserInfo.uid)
                .child("messages").child(message.messageId)
                .putFile(image).await()

            message.imageUrl = upload.storage.downloadUrl.await().toString()

            Firebase.firestore.collection("users")
                .document(otherUserInfo.uid).collection("conversations")
                .document(myUserInfo.uid).collection("messages")
                .document(message.messageId).set(message)

            emit(true)
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
            emit(false)
        }
    }

    override fun updateLikeToMatch(myUid: String, otherUid: String): Flow<Boolean> = flow {
        try {
            Firebase.firestore.collection("users").document(myUid)
                .collection("affections").document(otherUid)
                .update(hashMapOf("isMatch" to true, "isNewMatch" to true) as Map<String, Any>)
            Firebase.firestore.collection("users").document(otherUid)
                .collection("affections").document(myUid)
                .update(hashMapOf("isMatch" to true, "isNewMatch" to true) as Map<String, Any>)
            emit(true)
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
            emit(false)
        }
    }


    override fun acknowledgeNewMatches(myUid: String): Flow<Boolean> = flow {
        try {
            val matchDocuments = Firebase.firestore.collection("users").document(myUid)
                .collection("affections").whereEqualTo("isNewMatch", true)
                .get().await().documents

            for (doc in matchDocuments) {
                Firebase.firestore.collection("users").document(myUid)
                    .collection("affections").document(doc.id)
                    .update(hashMapOf("isNewMatch" to false) as Map<String, Any>)
            }
            emit(true)
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
            emit(false)
        }
    }

    override fun getAffections(myUid: String): Flow<Pair<List<Pair<String, Boolean>>, List<Pair<String, Boolean>>>?> =
        flow {
            val matchDocuments = Firebase.firestore.collection("users")
                .document(myUid).collection("affections")
                .get().await().documents

            val likes = matchDocuments.filter { it.get("isMatch") == false }
            val matches = matchDocuments.filter { it.get("isMatch") == true }

            emit(
                Pair(
                    likes.map { Pair(it.id, it.get("isNewLike") as Boolean) },
                    matches.map { Pair(it.id, it.get("isNewMatch") as Boolean) }
                )
            )
        }

    override fun addLike(myUserInfo: UserInfo, otherUserInfo: UserInfo): Flow<Boolean> = flow {
        try {
            Firebase.firestore.collection("users").document(otherUserInfo.uid)
                .collection("affections").document(myUserInfo.uid)
                .set(
                    hashMapOf(
                        "isNewLike" to true,
                        "screenName" to myUserInfo.screenName,
                        "profilePic" to myUserInfo.images[0],
                        "isMatch" to false,
                        "isNewMatch" to false
                    )
                )
            emit(true)
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
            emit(false)
        }
    }

    override fun acknowledgeNewLikes(myUid: String): Flow<Boolean> = flow {
        try {
            val newLikesDocuments = Firebase.firestore.collection("users").document(myUid)
                .collection("affections").whereEqualTo("isNewLike", true)
                .get().await().documents

            for (doc in newLikesDocuments) {
                Firebase.firestore.collection("users").document(myUid)
                    .collection("affections").document(doc.id)
                    .update(hashMapOf("isNewLike" to false) as Map<String, Any>)
            }
            emit(true)
        } catch (e: FirebaseFirestoreException) {
            e.printStackTrace()
            emit(false)
        }
    }
}
package cotey.hinton.moviedate.feature_auth.data.repository

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo
import cotey.hinton.moviedate.feature_auth.domain.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl : FirebaseAuthRepository {
    override fun login(email: String, pw: String): Flow<Pair<AuthResult?, FirebaseAuthException?>> =
        flow {
            try {
                val authResult = FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, pw)
                    .await()

                emit(Pair(authResult, null))
            } catch (e: FirebaseAuthException) {
                emit(Pair(null, e))
            }
        }

    override fun register(
        email: String,
        pw: String,
        images: List<Uri?>,
        userInfo: UserInfo
    ): Flow<Pair<AuthResult?, FirebaseAuthException?>> {
        return flow {
            try {
                val authResult = FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, pw)
                    .await()
                for (i in images.indices) {
                    if (images[i] == null) continue
                    val upload = FirebaseStorage.getInstance().reference.child("users")
                        .child(authResult.user!!.uid)
                        .child("images")
                        .child("image $i").putFile(images[i]!!).await()
                    val imgUrl = upload.storage.downloadUrl.await().toString()
                    userInfo.images.add(imgUrl)
                }

                userInfo.uid = authResult.user!!.uid
                println("user info = $userInfo")
                Firebase.firestore.collection("users").document(userInfo.uid).set(userInfo).await()
                emit(Pair(authResult, null))
            } catch (e: FirebaseAuthException) {
                emit(Pair(null, e))
            }
        }
    }

    override fun logout(): Flow<Boolean> = flow {
        try {
            FirebaseAuth.getInstance().signOut()
            emit(true)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(false)
        }
    }
}
package cotey.hinton.moviedate.feature_auth.domain.repository

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import cotey.hinton.moviedate.feature_auth.domain.models.UserInfo
import kotlinx.coroutines.flow.Flow

interface FirebaseAuthRepository {

    fun login(email: String, pw: String) : Flow<Pair<AuthResult?, FirebaseAuthException?>>
    fun register(email: String, pw: String, images: List<Uri?>, userInfo: UserInfo) : Flow<Pair<AuthResult?, FirebaseAuthException?>>
    fun logout(): Flow<Boolean>
}
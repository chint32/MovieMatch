package cotey.hinton.moviedate.feature_auth.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.util.*
import kotlin.collections.ArrayList

data class UserInfo(
    var uid: String = "",
    var screenName: String = "",
    var gender: String = "",
    var genderInterestedIn: ArrayList<String?> = ArrayList(listOf(null, null, null, null, null)),
    var lookingFor: ArrayList<String?> = ArrayList(listOf(null, null, null, null)),
    var age: Int = -1,
    var startAgeInterestedIn: Int = -1,
    var endAgeInterestedIn: Int = -1,
    var location: GeoPoint = GeoPoint(0.0, 0.0),
    var favoriteMovies: ArrayList<Movie> = ArrayList(),
    var favoriteTracks: ArrayList<TrackMetaData> = ArrayList(),
    var images: ArrayList<String?> = ArrayList(),
    var likes: ArrayList<String> = ArrayList(),
    var dislikes: ArrayList<String> = ArrayList(),
    var searchDistance: Int = 20,
    var fcmToken: String = "",
    var tokenTimestamp: Timestamp = Timestamp(Date().time / 1000000, 0),
    var isFirstLogin: Boolean = true
)

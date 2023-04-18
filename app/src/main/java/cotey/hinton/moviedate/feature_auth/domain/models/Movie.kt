package cotey.hinton.moviedate.feature_auth.domain.models

data class Movie(
    val rank: Int = -1,
    val title: String = "",
    val thumbnail: String = "",
    val rating: String = "",
    val id: String = "",
    val year: Int = -1,
    val image: String = "",
    val description: String = "",
    val trailer:String = "",
    val genre: List<String> = emptyList(),
    val director: List<String> = emptyList(),
    val writers: List<String> = emptyList(),
    val imdbid: String = ""
)

package cotey.hinton.moviedate.feature_main.domain.models

data class GetMovieIdResponse(
    val results: List<GetMovieIdResult>,
)

data class GetMovieIdResult(
    var imdb_id: String? = null,
    var title: String? = null
)

data class MovieDetailsResponse(
    val results: MovieDetailsResult,
)

data class MovieDetailsResult(
    var imdb_id: String? = null,
    var title: String? = null,
    var year: Int? = null,
    var popularity: Int? = null,
    var description: String? = null,
    var content_rating: String? = null,
    var movie_length: Int? = null,
    var rating: Float? = null,
    var created_at: String? = null,
    var trailer: String? = null,
    var image_url: String? = null,
    var release: String? = null,
    var plot: String? = null,
    var banner: String? = null,
    var type: String? = null,
    var more_like_this: Any? = null,
    var genreList: ArrayList<Genre> = ArrayList(),
    var keywords: ArrayList<Keyword> = ArrayList()

)

data class Genre(
    var id: String? = null,
    var genre: String? = null
)

data class Keyword(
    var id: String? = null,
    var keyword: String? = null
)

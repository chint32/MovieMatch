package cotey.hinton.moviedate.feature_auth.domain.models



data class SpotifyTop200Response( val trackMetadata: TrackMetaData = TrackMetaData() )
data class TrackMetaData(
    val trackName: String = "",
    val trackUri: String = "",
    var id: String = trackUri.substringAfterLast(":"),
    val displayImageUri: String = "",
    val artists: List<Artist> = emptyList(),
)
data class Artist(val name: String = "")



data class SpotifySearchResponse( val tracks: List<TrackData> = emptyList() )
data class TrackData( val data: TrackDetailsData = TrackDetailsData() )
data class TrackDetailsData(
    val id: String = "",
    val albumOfTrack: AlbumOfTrack = AlbumOfTrack(),
    val artists: SearchArtist = SearchArtist(),
)
data class AlbumOfTrack(
    val uri: String = "",
    val name: String = "",
    val coverArt: CoverArt = CoverArt()
)
data class CoverArt( val sources: List<Image> = emptyList() )
data class Image( val url: String = "" )


data class SearchArtist( val items: List<ArtistItem> = emptyList() )
data class ArtistItem( val profile: Profile = Profile() )
data class Profile( val name: String = "" )



data class SpotifySearchDetailsResponse( val tracks: List<TrackDetails> = emptyList() )
data class TrackDetails(
   val album: Album = Album(),
   val preview_url: String = ""
)
data class Album(
    val name: String = "",
    val artists: List<Artist> = emptyList(),
    val images: List<Image> = emptyList(),
    val release_date: String = ""
)
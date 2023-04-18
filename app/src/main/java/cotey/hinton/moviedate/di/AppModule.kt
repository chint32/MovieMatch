package cotey.hinton.moviedate.di

import cotey.hinton.moviedate.feature_auth.data.remote.ImdbTop100Api
import cotey.hinton.moviedate.feature_auth.data.repository.FirebaseAuthRepositoryImpl
import cotey.hinton.moviedate.feature_auth.data.repository.ImdbTop100RepositoryImpl
import cotey.hinton.moviedate.feature_main.data.remote.MoviesMiniApi
import cotey.hinton.moviedate.feature_main.data.remote.SpotifyApi
import cotey.hinton.moviedate.feature_main.data.repository.FirebaseMainRepositoryImpl
import cotey.hinton.moviedate.feature_main.data.repository.MoviesMiniRepositoryImpl
import cotey.hinton.moviedate.feature_main.data.repository.SpotifyRepositoryImpl
import cotey.hinton.moviedate.feature_main.domain.repository.MoviesMiniRepository
import cotey.hinton.moviedate.feature_main.domain.repository.SpotifyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideImdbTop100Api() : ImdbTop100Api {

        val httpClient = OkHttpClient.Builder()
        httpClient.interceptors().add(Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("X-RapidAPI-Key", "5db8e5f3bemsha35561b2b308f11p1528f1jsn0a794a82d5d8")
                .header("X-RapidAPI-Host", "imdb-top-100-movies.p.rapidapi.com")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        })
        val client = httpClient.build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://imdb-top-100-movies.p.rapidapi.com")
            .client(client)
            .build()
            .create(ImdbTop100Api::class.java)
    }

    @Singleton
    @Provides
    fun provideMovieMiniApi() : MoviesMiniApi {

        val httpClient = OkHttpClient.Builder()
        httpClient.interceptors().add(Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("X-RapidAPI-Key", "5db8e5f3bemsha35561b2b308f11p1528f1jsn0a794a82d5d8")
                .header("X-RapidAPI-Host", "moviesminidatabase.p.rapidapi.com")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        })
        val client = httpClient.build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://moviesminidatabase.p.rapidapi.com")
            .client(client)
            .build()
            .create(MoviesMiniApi::class.java)
    }

    @Singleton
    @Provides
    fun provideSpotifyApi() : SpotifyApi {

        val httpClient = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
        try {
            httpClient.interceptors().add(Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("X-RapidAPI-Key", "5db8e5f3bemsha35561b2b308f11p1528f1jsn0a794a82d5d8")
                    .header("X-RapidAPI-Host", "spotify81.p.rapidapi.com")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            })
        } catch (e: Exception){e.printStackTrace()}
        val client = httpClient.build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://spotify81.p.rapidapi.com")
            .client(client)
            .build()
            .create(SpotifyApi::class.java)
    }

    @Singleton
    @Provides
    fun provideSpotifyRepository(api: SpotifyApi) : SpotifyRepository {
        return SpotifyRepositoryImpl(api)
    }

    @Singleton
    @Provides
    fun provideRepository(api: MoviesMiniApi) : MoviesMiniRepository {
        return MoviesMiniRepositoryImpl(api)
    }

    @Singleton
    @Provides
    fun provideFirebaseAuthRepository() : FirebaseAuthRepositoryImpl {
        return FirebaseAuthRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideFirebaseMainRepository() : FirebaseMainRepositoryImpl {
        return FirebaseMainRepositoryImpl()
    }

    @Singleton
    @Provides
    fun provideTop100Repository(api: ImdbTop100Api) : ImdbTop100RepositoryImpl {
        return ImdbTop100RepositoryImpl(api)
    }

}
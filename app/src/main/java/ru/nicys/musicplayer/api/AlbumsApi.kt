package ru.nicys.musicplayer.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import ru.nicys.musicplayer.BuildConfig.BASE_URL
import ru.nicys.musicplayer.dto.Album

interface AlbumApi {
    @GET("album.json")
    suspend fun getAlbum(): Response<Album>

    companion object {

        private val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        val service: AlbumApi by lazy {
            retrofit.create(AlbumApi::class.java)
        }
    }
}
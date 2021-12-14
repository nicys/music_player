package ru.nicys.musicplayer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.nicys.musicplayer.api.AlbumApi
import ru.nicys.musicplayer.dao.AlbumDao
import ru.nicys.musicplayer.dao.TrackDao
import ru.nicys.musicplayer.dto.Album
import ru.nicys.musicplayer.entity.TrackEntity
import ru.nicys.musicplayer.entity.toEntity
import ru.nicys.musicplayer.entity.toTrack
import ru.nicys.musicplayer.exceptions.ApiException
import ru.nicys.musicplayer.exceptions.NetworkException
import ru.nicys.musicplayer.exceptions.UnknownException
import java.io.IOException
import java.lang.Exception

class TrackRepositoryImpl(
    private val tracksDao: TrackDao,
) : TrackRepository {

    override val data = tracksDao.getTrack().map(List<TrackEntity>::toTrack)

    override suspend fun getAlbum() = flow {
        try {
            val response = AlbumApi.service.getAlbum()

            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiException(response.code(), response.message())
            tracksDao.insertTracks(body.tracks.toEntity())
            emit(body)
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw  UnknownException
        }
    }
}
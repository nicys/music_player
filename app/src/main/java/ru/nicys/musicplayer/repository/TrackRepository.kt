package ru.nicys.musicplayer.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.nicys.musicplayer.dto.Album
import ru.nicys.musicplayer.dto.Track

interface TrackRepository {
    val data: LiveData<List<Track>>
    suspend fun getTracks()
    suspend fun getAlbum(): Flow<Album>
    suspend fun isPlaying(id: Int)
}
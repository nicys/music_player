package ru.nicys.musicplayer.repository

import kotlinx.coroutines.flow.Flow
import ru.nicys.musicplayer.dto.Album
import ru.nicys.musicplayer.dto.Track

interface TrackRepository {
    val data: Flow<List<Track>>
    suspend fun getAlbum(): Flow<Album>
}
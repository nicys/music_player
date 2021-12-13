package ru.nicys.musicplayer.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ru.nicys.musicplayer.db.AppDb
import ru.nicys.musicplayer.dto.Album
import ru.nicys.musicplayer.dto.Track
import ru.nicys.musicplayer.repository.TrackRepository
import ru.nicys.musicplayer.repository.TrackRepositoryImpl
import ru.nicys.musicplayer.utils.SingleLiveEvent

private var emptyAlbum = Album(0, "", "", "", "", "", emptyList())
private var emptyTrack = Track(0, "", false, 0)

class TracksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TrackRepository = TrackRepositoryImpl(
        AppDb.getInstance(context = application).trackDao(),
    )

    val dataTracks = repository.data

    private val _tracks = MutableLiveData(emptyTrack)
    val tracks: LiveData<Track>
        get() = _tracks

    private val _loadTracksExceptionEvent = SingleLiveEvent<Unit>()
    val loadTracksExceptionEvent: LiveData<Unit>
        get() = _loadTracksExceptionEvent

    suspend fun getAlbum(): Flow<Album> =
        repository.getAlbum()
            .catch { e ->
                e.printStackTrace()
                _loadTracksExceptionEvent.call()
            }

    private val _album = MutableLiveData(emptyAlbum)
    val album: LiveData<Album>
        get() = _album

    fun isPlaying(id: Int) = viewModelScope.launch {
        try {
            repository.isPlaying(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
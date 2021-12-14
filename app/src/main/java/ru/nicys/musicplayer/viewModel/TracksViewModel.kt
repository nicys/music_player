package ru.nicys.musicplayer.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import ru.nicys.musicplayer.db.AppDb
import ru.nicys.musicplayer.dto.Album
import ru.nicys.musicplayer.repository.TrackRepository
import ru.nicys.musicplayer.repository.TrackRepositoryImpl
import ru.nicys.musicplayer.utils.SingleLiveEvent

class TracksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TrackRepository = TrackRepositoryImpl(
        AppDb.getInstance(context = application).trackDao(),
    )

    val dataTracks = repository.data.asLiveData()

    private val _loadTracksExceptionEvent = SingleLiveEvent<Unit>()
    val loadTracksExceptionEvent: LiveData<Unit>
        get() = _loadTracksExceptionEvent

    suspend fun getAlbum(): Flow<Album> =
        repository.getAlbum()
            .catch { e ->
                e.printStackTrace()
                _loadTracksExceptionEvent.call()
            }
}
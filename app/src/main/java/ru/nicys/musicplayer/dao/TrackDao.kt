package ru.nicys.musicplayer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.nicys.musicplayer.entity.TrackEntity

@Dao
interface TrackDao {
    @Query("SELECT * FROM TrackEntity ORDER BY id")
    fun getTrack(): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Query(
        """
        UPDATE TrackEntity SET
        isPlaying = CASE WHEN isPlaying THEN 0 ELSE 1 END
        WHERE id = :id
    """
    )
    suspend fun isPlaying(id: Int)

    @Query(
        """
        UPDATE TrackEntity SET
        isPlaying = CASE WHEN isPlaying THEN 1 ELSE 0 END
        WHERE id = :id
    """
    )
    suspend fun isNotPlaying(id: Int)
}
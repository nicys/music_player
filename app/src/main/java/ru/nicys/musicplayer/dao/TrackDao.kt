package ru.nicys.musicplayer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.nicys.musicplayer.entity.TrackEntity

@Dao
interface TrackDao {
    @Query("SELECT * FROM TrackEntity ORDER BY id DESC")
    fun getTrack(): LiveData<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)
}
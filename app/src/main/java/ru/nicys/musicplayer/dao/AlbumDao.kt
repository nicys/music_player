package ru.nicys.musicplayer.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.nicys.musicplayer.entity.AlbumEntity

@Dao
interface AlbumDao {
    @Query("SELECT * FROM AlbumEntity ORDER BY idAlbum DESC")
    fun getAlbum(): LiveData<List<AlbumEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(tracks: List<AlbumEntity>)
}
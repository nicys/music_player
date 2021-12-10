package ru.nicys.musicplayer.db

import android.content.Context
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.nicys.musicplayer.dao.AlbumDao
import ru.nicys.musicplayer.dao.TrackDao
import ru.nicys.musicplayer.dto.Track
import ru.nicys.musicplayer.entity.AlbumEntity
import ru.nicys.musicplayer.entity.TrackEntity

@Database(entities = [TrackEntity::class, AlbumEntity::class], version = 1, exportSchema = false)

@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun albumDao(): AlbumDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .build()
    }
}

class Converters {
    @TypeConverter
    fun fromListTrackToString(value: List<Track>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromStringToListTrack(value: String): List<Track> {
        val type = object : TypeToken<Track>() {}.type
        val list = listOf(Gson().fromJson<Track>(value, type))
        return list
    }
}
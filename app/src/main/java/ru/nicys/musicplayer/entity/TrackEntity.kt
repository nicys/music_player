package ru.nicys.musicplayer.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.nicys.musicplayer.dto.Track

@Entity
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val file: String,
    var isPlaying: Boolean,
    val valalbumId: Int,
) {
    fun toDto(): Track = Track(id, file, isPlaying, valalbumId)

    companion object {
        fun fromDto(dto: Track) = with(dto) {
            TrackEntity(id, file, isPlaying, albumId)
        }
    }
}

fun List<TrackEntity>.toTrack(): List<Track> = map(TrackEntity::toDto)
fun List<Track>.toEntity(): List<TrackEntity> = map(TrackEntity::fromDto)
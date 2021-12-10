package ru.nicys.musicplayer.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.nicys.musicplayer.dto.Album
import ru.nicys.musicplayer.dto.Track

@Entity
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true)
    val idAlbum: Int,
    val title: String,
    val subtitle: String,
    val artist: String,
    val published: String,
    val genre: String,
    @TypeConverters
    val tracks: List<Track>,
) {
    fun toDto(): Album = Album(idAlbum, title, subtitle, artist, published, genre, tracks)

    companion object {
        fun fromDto(dto: Album) = with(dto) {
            AlbumEntity(idAlbum, title, subtitle, artist, published, genre, tracks)
        }
    }
}

fun List<AlbumEntity>.toAlbum(): List<Album> = map(AlbumEntity::toDto)
fun List<Album>.toEntity(): List<AlbumEntity> = map(AlbumEntity::fromDto)

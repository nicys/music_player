package ru.nicys.musicplayer.dto

data class Track(
    val id: Int,
    val file: String,
    val isPlaying: Boolean = false,
    val valalbumId: Int,
)

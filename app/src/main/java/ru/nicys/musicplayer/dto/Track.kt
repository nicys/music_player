package ru.nicys.musicplayer.dto

data class Track(
    val id: Int,
    val file: String,
    var isPlaying: Boolean = false,
    val valalbumId: Int,
)

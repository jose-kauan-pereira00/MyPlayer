package com.music.myplayer.data.model

import android.net.Uri

/**
 * Represents a music track with its basic information.
 *
 * @property id Unique identifier of the music on the device
 * @property title Title or name of the music
 * @property artist Name of the artist or composer
 * @property contentUri URI of the music audio content
 * @property duration Duration of the music in milliseconds
 */
data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val contentUri: Uri,
    val duration: Int
)
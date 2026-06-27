package com.music.myplayer.data.model

import android.net.Uri

data class Music(
    val id: Long,
    val title: String,
    val artist: String,
    val contentUri: Uri,
    val duration: Int
)
package com.music.myplayer.player

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

/**
 * A media session service implementation for the music player.
 *
 * This service creates and manages an [ExoPlayer] instance and a corresponding
 * [MediaSession] to provide playback controls and communication with external
 * media controllers.
 */
class MusicPlayerService : MediaSessionService() {

    /**
     * The [ExoPlayer] instance used to play audio content.
     *
     * It is initialized in [onCreate] and released when the service is destroyed.
     */
    private var exoPlayer: ExoPlayer? = null

    /**
     * The [MediaSession] that exposes player controls to the system.
     *
     * It is created after the player is initialized and returned to requesting
     * controllers in [onGetSession].
     */
    private var mediaSession: MediaSession? = null

    /**
     * Called when the service is created.
     *
     * Initializes the media player and the media session. The player is configured
     * to start playback automatically when it is ready.
     */
    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(this).build().apply {
            playWhenReady = true
        }

        exoPlayer?.let { player ->
            mediaSession = MediaSession.Builder(this, player).build()
        }
    }

    /**
     * Returns the active [MediaSession] for the given controller.
     *
     * @param controllerInfo information about the controller requesting the session.
     * @return the current [MediaSession], or null if it is not available.
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    /**
     * Called when the service is destroyed.
     *
     * Releases the media session and the associated player resources to avoid
     * leaking system resources.
     */
    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
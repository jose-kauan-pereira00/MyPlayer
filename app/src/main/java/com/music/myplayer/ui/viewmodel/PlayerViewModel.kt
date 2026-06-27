package com.music.myplayer.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.music.myplayer.data.model.Music
import com.music.myplayer.data.repository.MusicRepository
import com.music.myplayer.player.MusicPlayerService
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing music playback and exposing playback state to the UI.
 *
 * @param context application context used to initialize the music repository and media controller.
 */
@OptIn(UnstableApi::class)
class PlayerViewModel(context: Context) : ViewModel() {

    /** Repository responsible for loading music tracks from the device. */
    private val repository = MusicRepository(context)

    /** Backing state flow for the list of available music tracks. */
    private val _musicas = MutableStateFlow<List<Music>>(emptyList())

    /** Read-only state flow exposing the available music tracks. */
    val musicas: StateFlow<List<Music>> = _musicas.asStateFlow()

    /** Backing state flow for the currently selected music track. */
    private val _musicaAtual = MutableStateFlow<Music?>(null)

    /** Read-only state flow exposing the current music track. */
    val musicaAtual: StateFlow<Music?> = _musicaAtual.asStateFlow()

    /** Backing state flow for the current playback status. */
    private val _estaTocando = MutableStateFlow(false)

    /** Read-only state flow exposing whether playback is active. */
    val estaTocando: StateFlow<Boolean> = _estaTocando.asStateFlow()

    /** Controller used to manage playback through the media session. */
    private var mediaController: MediaController? = null

    init {
        carregarMusicas()
        inicializarControleRemoto(context)
    }

    /**
     * Loads the list of music tracks from the repository and updates the state flow.
     */
    private fun carregarMusicas() {
        viewModelScope.launch {
            val lista = repository.buscarMusicasDoCelular()
            _musicas.value = lista
        }
    }

    /**
     * Initializes the remote media controller and registers listeners for playback events.
     *
     * @param context application context used to build the media session token.
     */
    private fun inicializarControleRemoto(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, MusicPlayerService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            val controller = controllerFuture.get()
            mediaController = controller

            controller.addListener(object : Player.Listener {

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    val uriAtual = mediaItem?.localConfiguration?.uri
                    val musicaEncontrada = _musicas.value.find { it.contentUri == uriAtual }
                    _musicaAtual.value = musicaEncontrada
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    _estaTocando.value = isPlaying
                }
            })
        }, MoreExecutors.directExecutor())
    }

    /**
     * Selects the specified track, prepares the playlist, and starts playback.
     *
     * @param musica the track to play.
     */
    fun selecionarMusica(musica: Music) {
        mediaController?.let { controller ->
            val listaDeMusicas = _musicas.value
            val indexDaMusica = listaDeMusicas.indexOf(musica)

            if (indexDaMusica != -1) {
                val todosMediaItems = listaDeMusicas.map { item ->
                    MediaItem.Builder()
                        .setUri(item.contentUri)
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(item.title)
                                .setArtist(item.artist)
                                .build()
                        )
                        .build()
                }

                controller.setMediaItems(todosMediaItems, indexDaMusica, 0L)
                controller.prepare()
                controller.play()

                _musicaAtual.value = musica
                _estaTocando.value = true
            }
        }
    }

    /**
     * Toggles playback between play and pause.
     */
    fun alternarPlayPause() {
        mediaController?.let { controller ->
            if (controller.isPlaying) {
                controller.pause()
            } else {
                controller.play()
            }
        }
    }

    /**
     * Skips playback to the next media item if available.
     */
    fun proximaMusica() {
        mediaController?.let { controller ->
            if (controller.hasNextMediaItem()) {
                controller.seekToNext()
            }
        }
    }

    /**
     * Returns playback to the previous media item or restarts the current item when no previous item exists.
     */
    fun voltarMusica() {
        mediaController?.let { controller ->
            if (controller.hasPreviousMediaItem()) {
                controller.seekToPrevious()
            } else {
                controller.seekTo(0)
            }
        }
    }

    /**
     * Releases media controller resources when the ViewModel is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        mediaController?.release()
    }
}
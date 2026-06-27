package com.music.myplayer.ui.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
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
import kotlinx.coroutines.launch // ADICIONADO: Import essencial para o 'launch' funcionar!

@OptIn(UnstableApi::class)
class PlayerViewModel(context: Context) : ViewModel() {

    private val repository = MusicRepository(context)

    // AJUSTADO: Nome mudado para '_musicas' e 'musicas' para combinar perfeitamente com a sua Screen
    private val _musicas = MutableStateFlow<List<Music>>(emptyList())
    val musicas: StateFlow<List<Music>> = _musicas.asStateFlow()

    private val _musicaAtual = MutableStateFlow<Music?>(null)
    val musicaAtual: StateFlow<Music?> = _musicaAtual.asStateFlow()

    private val _estaTocando = MutableStateFlow(false)
    val estaTocando: StateFlow<Boolean> = _estaTocando.asStateFlow()

    private var mediaController: MediaController? = null

    init {
        carregarMusicas()
        inicializarControleRemoto(context)
    }

    private fun carregarMusicas() {
        viewModelScope.launch {
            val lista = repository.buscarMusicasDoCelular()
            _musicas.value = lista
        }
    }

    private fun inicializarControleRemoto(context: Context) {
        val sessionToken = SessionToken(context, ComponentName(context, MusicPlayerService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            mediaController = controllerFuture.get()
        }, MoreExecutors.directExecutor())
    }

    fun selecionarMusica(musica: Music) {
        _musicaAtual.value = musica
        _estaTocando.value = true

        val mediaItem = MediaItem.fromUri(musica.contentUri)

        mediaController?.let { controller ->
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        }
    }

    fun alternarPlayPause() {
        mediaController?.let { controller ->
            if (controller.isPlaying) {
                controller.pause()
                _estaTocando.value = false
            } else {
                controller.play()
                _estaTocando.value = true
            }
        }
    }

    fun proximaMusica() {
        mediaController?.let { controller ->
            if (controller.hasNextMediaItem()) {
                controller.seekToNext()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaController?.release()
    }
}
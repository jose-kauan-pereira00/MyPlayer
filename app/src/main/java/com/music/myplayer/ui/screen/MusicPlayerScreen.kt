/**
 * Screen composables for the music player UI.
 *
 * This file contains the main screen of the music player, header, track item component and
 * miniature player component. It also defines theme colors used in the screen.
 */
package com.music.myplayer.ui.screen

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.myplayer.data.model.Music
import com.music.myplayer.ui.viewmodel.PlayerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

/** Primary background color used throughout the player UI. */
val FundoEscuro = Color(0xFF121212)

/** Card background color used for panels and list items. */
val CardEscuro = Color(0xFF1E1E1E)

/** Neon purple accent used for highlight actions and gradients. */
val RoxoNeon = Color(0xFF8E24AA)

/** Cyan accent color used for secondary highlights. */
val AzulCiano = Color(0xFF00ACC1)

/** White color used for primary text content. */
val TextoBranco = Color(0xFFFFFFFF)

/** Gray color used for secondary text and disabled states. */
val TextoCinza = Color(0xFFB3B3B3)

/**
 * Composable that displays the main music player screen.
 *
 * Requests audio permissions, observes playback state from the provided [PlayerViewModel],
 * and renders the list of tracks and the miniature player.
 *
 * @param viewModel The [PlayerViewModel] instance providing music data and playback controls.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusicPlayerScreen(viewModel: PlayerViewModel) {
    val permissaoAudio = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val estadoPermissao = rememberPermissionState(permission = permissaoAudio)

    LaunchedEffect(Unit) {
        estadoPermissao.launchPermissionRequest()
    }

    val musicas by viewModel.musicas.collectAsState()
    val musicaAtual by viewModel.musicaAtual.collectAsState()
    val estaTocando by viewModel.estaTocando.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FundoEscuro
    ) {
        if (estadoPermissao.status.isGranted) {
            Box(modifier = Modifier.fillMaxSize()) {

                Column(modifier = Modifier.fillMaxSize()) {
                    HeaderDoApp()

                    if (musicas.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(text = "Nenhuma música encontrada.", color = TextoCinza, fontSize = 16.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp, start = 16.dp, end = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(musicas) { musica ->
                                val selecionada = musica == musicaAtual
                                ComponenteItemMusica(musica = musica, estaSelecionada = selecionada) {
                                    viewModel.selecionarMusica(musica)
                                }
                            }
                        }
                    }
                }

                musicaAtual?.let { musica ->
                    ComponenteMiniPlayer(
                        musica = musica,
                        estaTocando = estaTocando,
                        onVoltarClick = { viewModel.voltarMusica() },      // Conectado!
                        onPlayPauseClick = { viewModel.alternarPlayPause() },
                        onProximaClick = { viewModel.proximaMusica() },    // Conectado!
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 42.dp)
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Precisamos do acesso aos seus áudios.", color = TextoBranco, modifier = Modifier.padding(bottom = 16.dp))
                    Button(
                        onClick = { estadoPermissao.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = RoxoNeon)
                    ) {
                        Text(text = "Conceder Permissão", color = TextoBranco)
                    }
                }
            }
        }
    }
}

/**
 * Composable that renders the application header.
 *
 * Displays the app title and subtitle over a horizontal gradient background.
 */
@Composable
fun HeaderDoApp() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(colors = listOf(RoxoNeon, AzulCiano)))
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Column {
            Text("Meu Player", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextoBranco)
            Text("Sua biblioteca local", fontSize = 14.sp, color = TextoBranco.copy(alpha = 0.8f))
        }
    }
}

/**
 * Displays a single music item in the list.
 *
 * @param musica The [Music] object containing title and artist details.
 * @param estaSelecionada Whether this item is currently selected.
 * @param onClick Callback invoked when the item is tapped.
 */
@Composable
fun ComponenteItemMusica(musica: Music, estaSelecionada: Boolean, onClick: () -> Unit) {
    val corBordaOuFundo = if (estaSelecionada) RoxoNeon.copy(alpha = 0.2f) else CardEscuro
    val corTextoTitulo = if (estaSelecionada) AzulCiano else TextoBranco

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = corBordaOuFundo)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.linearGradient(colors = listOf(RoxoNeon, AzulCiano))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎵", color = TextoBranco, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = musica.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = corTextoTitulo,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = musica.artist,
                    color = TextoCinza,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (estaSelecionada) {
                Text(text = "🔊", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

/**
 * Renders the miniature player control panel.
 *
 * Displays the current track information and playback controls for previous, play/pause,
 * and next actions.
 *
 * @param musica The current [Music] track displayed in the mini player.
 * @param estaTocando Whether playback is currently active.
 * @param onVoltarClick Callback invoked for the previous track action.
 * @param onPlayPauseClick Callback invoked to toggle play or pause state.
 * @param onProximaClick Callback invoked for the next track action.
 * @param modifier Optional [Modifier] for layout adjustments.
 */
@Composable
fun ComponenteMiniPlayer(
    musica: Music,
    estaTocando: Boolean,
    onVoltarClick: () -> Unit,      // Adicionado
    onPlayPauseClick: () -> Unit,
    onProximaClick: () -> Unit,     // Adicionado
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardEscuro),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Detalhes da música atual
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = musica.title,
                    fontWeight = FontWeight.Bold,
                    color = TextoBranco,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = musica.artist,
                    color = TextoCinza,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Barra de Controle: Voltar, Play/Pause, Avançar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Botão VOLTAR (⏮)
                IconButton(onClick = onVoltarClick) {
                    Text(text = "⏮", color = AzulCiano, fontSize = 20.sp)
                }

                // Botão PLAY / PAUSE (Estilizado em círculo ou pílula)
                Button(
                    onClick = onPlayPauseClick,
                    colors = ButtonDefaults.buttonColors(containerColor = RoxoNeon),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (estaTocando) "Pause" else "Play",
                        color = TextoBranco,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Botão AVANÇAR (⏭)
                IconButton(onClick = onProximaClick) {
                    Text(text = "⏭", color = AzulCiano, fontSize = 20.sp)
                }
            }
        }
    }
}
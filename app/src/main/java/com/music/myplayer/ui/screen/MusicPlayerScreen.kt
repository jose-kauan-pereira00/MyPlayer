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

val FundoEscuro = Color(0xFF121212)
val CardEscuro = Color(0xFF1E1E1E)
val RoxoNeon = Color(0xFF8E24AA)
val AzulCiano = Color(0xFF00ACC1)
val TextoBranco = Color(0xFFFFFFFF)
val TextoCinza = Color(0xFFB3B3B3)

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
                        onPlayPauseClick = { viewModel.alternarPlayPause() },
                        onProximaClick = { viewModel.proximaMusica() },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp, vertical = 16.dp)
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

@Composable
fun ComponenteMiniPlayer(
    musica: Music,
    estaTocando: Boolean,
    onPlayPauseClick: () -> Unit,
    onProximaClick: () -> Unit,
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = onPlayPauseClick,
                    colors = ButtonDefaults.buttonColors(containerColor = RoxoNeon),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(text = if (estaTocando) "Pause" else "Play", color = TextoBranco, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onProximaClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CardEscuro),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = "➔", color = AzulCiano, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}
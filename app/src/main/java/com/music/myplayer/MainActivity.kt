package com.music.myplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.music.myplayer.ui.screen.MusicPlayerScreen
import com.music.myplayer.ui.viewmodel.PlayerViewModel
import com.music.myplayer.ui.theme.MyPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = PlayerViewModel(applicationContext)

        setContent {
            MyPlayerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MusicPlayerScreen(viewModel = viewModel)
                }
            }
        }
    }
}
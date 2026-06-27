package com.music.myplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.music.myplayer.data.model.Music

class MusicRepository(private val context: Context) {

    fun buscarMusicasDoCelular(): List<Music> {
        val listaMusicas = mutableListOf<Music>()

        val uriTabela = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        // MODIFICADO: Adicionamos o DATE_MODIFIED na projeção para pegar a data do arquivo
        val projecao = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_MODIFIED
        )

        val selecao = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND (" +
                "${MediaStore.Audio.Media.MIME_TYPE} = ? OR " +
                "${MediaStore.Audio.Media.MIME_TYPE} = ? OR " +
                "${MediaStore.Audio.Media.MIME_TYPE} = ?)"

        val argumentosSelecao = arrayOf("audio/mpeg", "audio/mp4", "audio/x-m4a")

        val cursor = context.contentResolver.query(
            uriTabela,
            projecao,
            selecao,
            argumentosSelecao,
            "${MediaStore.Audio.Media.DATE_MODIFIED} DESC" // MODIFICADO: Ordena da mais recente para a mais antiga!
        )

        cursor?.use { c ->
            val idColuna = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val tituloColuna = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistaColuna = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val duracaoColuna = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (c.moveToNext()) {
                val id = c.getLong(idColuna)
                val titulo = c.getString(tituloColuna)
                val artista = c.getString(artistaColuna)
                val duracao = c.getInt(duracaoColuna)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val artistaFinal = if (artista == MediaStore.UNKNOWN_STRING) "Artista Desconhecido" else artista

                listaMusicas.add(
                    Music(
                        id = id,
                        title = titulo,
                        artist = artistaFinal,
                        contentUri = contentUri,
                        duration = duracao
                    )
                )
            }
        }

        return listaMusicas
    }
}
# MyPlayer

MyPlayer é um reprodutor de mídia Android simples e leve desenvolvido em Kotlin (ou Java) para reprodução local e streaming de áudio e vídeo.

## Funcionalidades
 - Reproduzir arquivos de áudio e vídeo locais(mp3 / m4a)
 - Lista de reprodução simples
 - Controles básicos: play, pause, stop, próxima/anterior, seek
 - Informações básicas do arquivo (duração, posição)

## Tecnologias
 - Android SDK
 - Exoplayer (recomendado) ou MediaPlayer
 - Kotlin (padrão) / Java

## Como usar
 1. Clone o repositório:

	 git clone [Repository](https://github.com/jose-kauan-pereira00/MyPlayer.git)

 2. Abra o projeto no Android Studio.
 3. Configure o SDK e as dependências (ExoPlayer, permissões de armazenamento/Internet) no Gradle.
 4. Construa e execute em um dispositivo ou emulador.
 5. Diponibilazei um arquivo apk em Releases

## Permissões
 No AndroidManifest.xml certifique-se de incluir, quando necessário:

 - INTERNET
 - READ_EXTERNAL_STORAGE (ou uso de Storage Access Framework para Android 10+)

## Integração rápida (ExoPlayer)
 Exemplo mínimo de inicialização do ExoPlayer:

 ```kotlin
 val player = ExoPlayer.Builder(context).build()
 val mediaItem = MediaItem.fromUri(uri)
 player.setMediaItem(mediaItem)
 player.prepare()
 player.play()
 ```

## Estrutura sugerida
 - app/ - módulo Android principal
 - app/src/main/java/ - código-fonte
 - app/src/main/res/ - recursos (layouts, drawables)
 - README.md - este arquivo

## Contribuição
 Pull requests são bem-vindos. Abra issues para bugs e sugestões.

---

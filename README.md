# MyPlayer

MyPlayer is a simple and lightweight Android media player developed in Kotlin (or Java) for local playback and audio/video streaming.

## Features
 - Play local audio and video files (mp3 / m4a)
 - Simple playlist
 - Basic controls: play, pause, stop, next/previous, seek
 - Basic file info (duration, position)

## Technologies
 - Android SDK
 - ExoPlayer (recommended) or MediaPlayer
 - Kotlin (default) / Java

## How to use
 1. Clone the repository:

	 git clone [Repository](https://github.com/jose-kauan-pereira00/MyPlayer.git)

 2. Open the project in Android Studio.
 3. Configure the SDK and dependencies (ExoPlayer, storage/Internet permissions) in Gradle.
 4. Build and run on a device or emulator.
 5. Upload an APK in Releases.

## Permissions
 In AndroidManifest.xml make sure to include, when needed:

 - INTERNET
 - READ_EXTERNAL_STORAGE (or use Storage Access Framework for Android 10+)

## Quick integration (ExoPlayer)
 Minimal example of ExoPlayer initialization:

 ```kotlin
 val player = ExoPlayer.Builder(context).build()
 val mediaItem = MediaItem.fromUri(uri)
 player.setMediaItem(mediaItem)
 player.prepare()
 player.play()
 ```

## Suggested structure
 - app/ - main Android module
 - app/src/main/java/ - source code
 - app/src/main/res/ - resources (layouts, drawables)
 - README.md - this file

## Contribution
 Pull requests are welcome. Open issues for bugs and suggestions.

---

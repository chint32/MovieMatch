package cotey.hinton.moviedate.feature_main.presentation.screens.song_details.audio_visualizer

import android.media.MediaPlayer
import androidx.compose.runtime.MutableState

class AudioPlayer {

    private var player: MediaPlayer? = null

    private val audioComputer = VisualizerComputer()

    fun play(url: String, visualizerData: MutableState<VisualizerData>) {
        player = MediaPlayer().apply {
            setDataSource(url)
            setVolume(0.5f, 0.5f)
            prepare()
            start()
        }
        audioComputer.start(audioSessionId = player!!.audioSessionId, onData = { data ->
            visualizerData.value = data
        })
    }

    fun stop() {
        audioComputer.stop()
        player?.stop()
        player?.release()
        player = null
    }
}

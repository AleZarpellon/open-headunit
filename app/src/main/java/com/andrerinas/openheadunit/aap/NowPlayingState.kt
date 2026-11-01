package com.andrerinas.openheadunit.aap

import android.graphics.Bitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NowPlaying(
    val title: String,
    val artist: String,
    val isPlaying: Boolean,
    val durationSeconds: Int = 0,
    val positionSeconds: Int = 0,
    val art: Bitmap? = null,
)

/** What the phone says is playing, for the music bar. Null when there is nothing to show. */
object NowPlayingState {
    private val state = MutableStateFlow<NowPlaying?>(null)
    val current: StateFlow<NowPlaying?> = state

    fun onMetadata(title: String, artist: String, durationSeconds: Int = 0) {
        val previous = state.value
        val sameTrack = previous?.title == title
        state.value = if (title.isBlank()) null else NowPlaying(
            title = title,
            artist = artist,
            isPlaying = previous?.isPlaying ?: true,
            durationSeconds = durationSeconds,
            positionSeconds = if (sameTrack) previous?.positionSeconds ?: 0 else 0,
            art = if (sameTrack) previous?.art else null,
        )
    }

    fun onPlaying(isPlaying: Boolean) {
        state.value = state.value?.copy(isPlaying = isPlaying)
    }

    fun onPosition(seconds: Int) {
        state.value = state.value?.copy(positionSeconds = seconds)
    }

    /** Dropped if the track moved on while the picture was being decoded. */
    fun onArt(title: String, art: Bitmap?) {
        val current = state.value ?: return
        if (current.title == title) state.value = current.copy(art = art)
    }

    fun clear() {
        state.value = null
    }
}

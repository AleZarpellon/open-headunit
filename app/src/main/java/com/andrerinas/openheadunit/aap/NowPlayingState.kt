package com.andrerinas.openheadunit.aap

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NowPlaying(val title: String, val artist: String, val isPlaying: Boolean)

/** What the phone says is playing, for the music bar. Null when there is nothing to show. */
object NowPlayingState {
    private val state = MutableStateFlow<NowPlaying?>(null)
    val current: StateFlow<NowPlaying?> = state

    fun onMetadata(title: String, artist: String) {
        state.value = if (title.isBlank()) null
            else NowPlaying(title, artist, state.value?.isPlaying ?: true)
    }

    fun onPlaying(isPlaying: Boolean) {
        state.value = state.value?.copy(isPlaying = isPlaying)
    }

    fun clear() {
        state.value = null
    }
}

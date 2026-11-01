package com.andrerinas.openheadunit.aap

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MusicBarPolicyTest {

    private val width = 100
    private val height = 40
    private val mapBlue = 0xFF4A6FA5.toInt()
    private val black = 0xFF000000.toInt()

    private fun screen(fill: Int = mapBlue, darkColumn: Int? = null) = IntArray(width * height) { i ->
        if (i % width == darkColumn) black else fill
    }

    @Test
    fun `a map has no dark column`() {
        assertFalse(MusicBarPolicy.hasDarkColumn(screen(), width, height))
    }

    @Test
    fun `the gap beside the widgets is found wherever it falls`() {
        assertTrue(MusicBarPolicy.hasDarkColumn(screen(darkColumn = 60), width, height))
        assertTrue(MusicBarPolicy.hasDarkColumn(screen(darkColumn = 80), width, height))
    }

    @Test
    fun `a dark column on the left is the icon rail, not the gap`() {
        assertFalse(MusicBarPolicy.hasDarkColumn(screen(darkColumn = 10), width, height))
    }

    @Test
    fun `a dark full screen app counts as not a map`() {
        assertTrue(MusicBarPolicy.hasDarkColumn(screen(fill = black), width, height))
    }

    @Test
    fun `a column broken by the map is not a gap`() {
        val pixels = screen(darkColumn = 60)
        for (y in 10..25) pixels[y * width + 60] = mapBlue
        assertFalse(MusicBarPolicy.hasDarkColumn(pixels, width, height))
    }

    @Test
    fun `a picture of the wrong size reads as nothing`() {
        assertFalse(MusicBarPolicy.hasDarkColumn(IntArray(4), width, height))
    }

    @Test
    fun `the bar shows only when on, with music, and over a map`() {
        assertTrue(MusicBarPolicy.showsBar(enabled = true, hasMedia = true, hideOutsideMap = true, screenIsDark = false))
        assertFalse(MusicBarPolicy.showsBar(enabled = false, hasMedia = true, hideOutsideMap = true, screenIsDark = false))
        assertFalse(MusicBarPolicy.showsBar(enabled = true, hasMedia = false, hideOutsideMap = true, screenIsDark = false))
        assertFalse(MusicBarPolicy.showsBar(enabled = true, hasMedia = true, hideOutsideMap = true, screenIsDark = true))
    }

    @Test
    fun `with recognition off the bar stays over any screen`() {
        assertTrue(MusicBarPolicy.showsBar(enabled = true, hasMedia = true, hideOutsideMap = false, screenIsDark = true))
    }

    @Test
    fun `one odd frame does not change the reading`() {
        val reading = StableReading(2)
        assertFalse(reading.feed(true))
        assertFalse(reading.feed(false))
        assertFalse(reading.feed(true))
        assertTrue(reading.feed(true))
        assertTrue(reading.feed(false))
        assertFalse(reading.feed(false))
    }

    @Test
    fun `now playing follows the phone and clears`() {
        NowPlayingState.clear()
        NowPlayingState.onPlaying(false)
        assertEquals(null, NowPlayingState.current.value)
        NowPlayingState.onMetadata("Brano", "Artista")
        assertEquals(NowPlaying("Brano", "Artista", true), NowPlayingState.current.value)
        NowPlayingState.onPlaying(false)
        NowPlayingState.onMetadata("Altro", "Artista")
        assertEquals(NowPlaying("Altro", "Artista", false), NowPlayingState.current.value)
        NowPlayingState.onMetadata("", "")
        assertEquals(null, NowPlayingState.current.value)
    }
}

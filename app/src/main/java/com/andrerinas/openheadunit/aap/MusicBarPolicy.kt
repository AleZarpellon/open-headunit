package com.andrerinas.openheadunit.aap

/**
 * When the music bar under Android Auto shows, and how the screen behind it is read.
 *
 * Android Auto never says which screen is open, so the picture is the only evidence. A dark column
 * running down the right half is the gap between the map and the cards on the widget screen, and
 * the dark background of a full-screen app has one too. A map has none, so its colours are what
 * makes the bar show. The search covers a range rather than one place, so it survives a DPI change.
 */
object MusicBarPolicy {

    const val BAR_HEIGHT_PX = 80
    const val CHECK_INTERVAL_MS = 1_500L
    const val SAMPLE_WIDTH = 320
    const val SAMPLE_HEIGHT = 90

    /** Readings in a row before the screen counts as changed, so one odd frame moves nothing. */
    const val READINGS_TO_CHANGE = 2

    /** Readings ignored after the margin moved: Android Auto redraws, and black frames say nothing. */
    const val READINGS_TO_SKIP_AFTER_MOVE = 3

    const val DEFAULT_CARD_COLOR = 0xFF2A2A2E.toInt()
    private const val COVER_PERCENT = 45
    private const val MAX_CARD_CHANNEL = 110

    private const val DARK_LUMA = 28
    private const val DARK_ROWS_PERCENT = 95
    private const val FIRST_COLUMN_PERCENT = 50
    private const val LAST_COLUMN_PERCENT = 92
    private const val FIRST_ROW_PERCENT = 15
    private const val LAST_ROW_PERCENT = 75

    fun hasDarkColumn(pixels: IntArray, width: Int, height: Int): Boolean {
        if (width <= 0 || height <= 0 || pixels.size < width * height) return false
        val firstRow = height * FIRST_ROW_PERCENT / 100
        val lastRow = height * LAST_ROW_PERCENT / 100
        val rows = lastRow - firstRow
        if (rows <= 0) return false
        for (x in width * FIRST_COLUMN_PERCENT / 100 until width * LAST_COLUMN_PERCENT / 100) {
            var dark = 0
            for (y in firstRow until lastRow) {
                if (luma(pixels[y * width + x]) <= DARK_LUMA) dark++
            }
            if (dark * 100 >= rows * DARK_ROWS_PERCENT) return true
        }
        return false
    }

    fun showsBar(enabled: Boolean, hasMedia: Boolean, hideOutsideMap: Boolean, screenIsDark: Boolean): Boolean =
        enabled && hasMedia && !(hideOutsideMap && screenIsDark)

    /** How far along the track is, or null when the phone did not say how long it is. */
    fun progressFraction(positionSeconds: Int, durationSeconds: Int): Float? =
        if (durationSeconds <= 0) null
        else (positionSeconds.toFloat() / durationSeconds).coerceIn(0f, 1f)

    /** The cover's average colour pulled towards the card's own dark grey, so white stays readable. */
    fun cardColorFor(averageArgb: Int): Int {
        val mix = { shift: Int ->
            val cover = (averageArgb shr shift) and 0xFF
            val base = (DEFAULT_CARD_COLOR shr shift) and 0xFF
            ((cover * COVER_PERCENT + base * (100 - COVER_PERCENT)) / 100).coerceAtMost(MAX_CARD_CHANNEL)
        }
        return (0xFF shl 24) or (mix(16) shl 16) or (mix(8) shl 8) or mix(0)
    }

    private fun luma(argb: Int): Int {
        val r = (argb shr 16) and 0xFF
        val g = (argb shr 8) and 0xFF
        val b = argb and 0xFF
        return (r * 299 + g * 587 + b * 114) / 1000
    }
}

/** Holds a reading until the opposite one has come [needed] times in a row. */
class StableReading(private val needed: Int) {
    private var current = false
    private var opposite = 0

    fun feed(reading: Boolean): Boolean {
        if (reading == current) {
            opposite = 0
        } else if (++opposite >= needed) {
            current = reading
            opposite = 0
        }
        return current
    }
}

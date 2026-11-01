package com.andrerinas.openheadunit.aap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.andrerinas.openheadunit.R

/**
 * The strip under Android Auto, drawn like its media card: what is playing, how far along, and the
 * three buttons that control it. The card takes its colour from the cover and everything on it
 * follows, so it reads on a bright cover as well as a dark one. Sizes are in pixels, like the
 * margin it sits in.
 */
class MusicBarView(context: Context, private val onKey: (Int) -> Unit) : FrameLayout(context) {

    private val card = GradientDrawable().apply { cornerRadius = CARD_CORNER }
    private val pill = GradientDrawable().apply { cornerRadius = PILL_CORNER }
    private val artView = ImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        visibility = View.GONE
    }
    private val nowPlaying = TextView(context).apply {
        textSize = 19f
        includeFontPadding = false
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }
    private val playPause = button(R.drawable.ic_music_pause, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE).apply { background = pill }
    private val previous = button(R.drawable.ic_music_previous, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
    private val next = button(R.drawable.ic_music_next, KeyEvent.KEYCODE_MEDIA_NEXT)
    private val progress = View(context).apply {
        pivotX = 0f
        scaleX = 0f
    }
    private var shownArt: Bitmap? = null

    init {
        setBackgroundColor(Color.BLACK)
        isClickable = true

        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(6, 0, 8, 0)
            background = card
        }
        row.addView(artView, LinearLayout.LayoutParams(ART_SIZE, ART_SIZE).apply { rightMargin = 14 })
        row.addView(nowPlaying, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply { leftMargin = 8; rightMargin = 12 })
        row.addView(playPause, LinearLayout.LayoutParams(116, 58).apply { rightMargin = 8 })
        row.addView(previous, LinearLayout.LayoutParams(92, 68))
        row.addView(next, LinearLayout.LayoutParams(92, 68))

        addView(row, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            setMargins(CARD_GAP, CARD_GAP, CARD_GAP, CARD_GAP)
        })
        addView(progress, LayoutParams(LayoutParams.MATCH_PARENT, 3, Gravity.BOTTOM).apply {
            setMargins(CARD_GAP + 16, 0, CARD_GAP + 16, CARD_GAP + 2)
        })
        applyColors(MusicBarPolicy.DEFAULT_CARD_COLOR)
    }

    fun show(playing: NowPlaying?) {
        nowPlaying.text = styled(playing?.title.orEmpty(), playing?.artist.orEmpty())
        playPause.setImageResource(
            if (playing?.isPlaying == false) R.drawable.ic_music_play else R.drawable.ic_music_pause
        )
        val fraction = MusicBarPolicy.progressFraction(playing?.positionSeconds ?: 0, playing?.durationSeconds ?: 0)
        progress.visibility = if (fraction == null) View.GONE else View.VISIBLE
        progress.scaleX = fraction ?: 0f
        showArt(playing?.art)
    }

    private fun styled(title: String, artist: String): CharSequence {
        val text = SpannableStringBuilder(title)
        text.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (artist.isNotBlank()) text.append("  ·  ").append(artist)
        return text
    }

    private fun showArt(art: Bitmap?) {
        if (art === shownArt) return
        shownArt = art
        if (art == null) {
            artView.visibility = View.GONE
            artView.setImageDrawable(null)
            applyColors(MusicBarPolicy.DEFAULT_CARD_COLOR)
            return
        }
        artView.setImageDrawable(RoundedBitmapDrawableFactory.create(resources, art).apply {
            cornerRadius = ART_CORNER
        })
        artView.visibility = View.VISIBLE
        // One pixel is the average of the whole picture.
        val average = Bitmap.createScaledBitmap(art, 1, 1, true).getPixel(0, 0)
        applyColors(MusicBarPolicy.cardColorFor(average))
    }

    private fun applyColors(cardColor: Int) {
        val content = MusicBarPolicy.contentColorOn(cardColor)
        card.setColor(cardColor)
        pill.setColor(content)
        nowPlaying.setTextColor(content)
        progress.setBackgroundColor(content)
        previous.setColorFilter(content)
        next.setColorFilter(content)
        playPause.setColorFilter(cardColor)
    }

    private fun button(icon: Int, keyCode: Int) = ImageButton(context).apply {
        setImageResource(icon)
        setBackgroundColor(Color.TRANSPARENT)
        scaleType = ImageView.ScaleType.FIT_CENTER
        setPadding(8, 8, 8, 8)
        setOnClickListener { onKey(keyCode) }
    }

    private companion object {
        const val CARD_GAP = 5
        const val CARD_CORNER = 22f
        const val PILL_CORNER = 29f
        const val ART_SIZE = 58
        const val ART_CORNER = 14f
    }
}

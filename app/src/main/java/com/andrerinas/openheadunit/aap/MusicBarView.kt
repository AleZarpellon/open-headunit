package com.andrerinas.openheadunit.aap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
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
 * three buttons that control it. Sizes are in pixels, like the margin it sits in.
 */
class MusicBarView(context: Context, private val onKey: (Int) -> Unit) : FrameLayout(context) {

    private val card = GradientDrawable().apply {
        cornerRadius = CARD_CORNER
        setColor(MusicBarPolicy.DEFAULT_CARD_COLOR)
    }
    private val artView = ImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        visibility = View.GONE
    }
    private val title = line(16f, Color.WHITE, Typeface.BOLD)
    private val artist = line(13f, 0xCCFFFFFF.toInt(), Typeface.NORMAL)
    private val playPause = button(R.drawable.ic_music_pause, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, Color.BLACK).apply {
        background = GradientDrawable().apply {
            cornerRadius = PILL_CORNER
            setColor(Color.WHITE)
        }
    }
    private val progress = View(context).apply {
        setBackgroundColor(Color.WHITE)
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
            setPadding(6, 0, 12, 0)
            background = card
        }
        row.addView(artView, LinearLayout.LayoutParams(ART_SIZE, ART_SIZE).apply { rightMargin = 14 })
        row.addView(LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            addView(title)
            addView(artist)
        }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(playPause, LinearLayout.LayoutParams(96, 50).apply { rightMargin = 8 })
        row.addView(button(R.drawable.ic_music_previous, KeyEvent.KEYCODE_MEDIA_PREVIOUS, Color.WHITE), LinearLayout.LayoutParams(72, 60))
        row.addView(button(R.drawable.ic_music_next, KeyEvent.KEYCODE_MEDIA_NEXT, Color.WHITE), LinearLayout.LayoutParams(72, 60))

        addView(row, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            setMargins(CARD_GAP, CARD_GAP, CARD_GAP, CARD_GAP)
        })
        addView(progress, LayoutParams(LayoutParams.MATCH_PARENT, 3, Gravity.BOTTOM).apply {
            setMargins(CARD_GAP + 16, 0, CARD_GAP + 16, CARD_GAP + 2)
        })
    }

    fun show(playing: NowPlaying?) {
        title.text = playing?.title.orEmpty()
        artist.text = playing?.artist.orEmpty()
        playPause.setImageResource(
            if (playing?.isPlaying == false) R.drawable.ic_music_play else R.drawable.ic_music_pause
        )
        val fraction = MusicBarPolicy.progressFraction(playing?.positionSeconds ?: 0, playing?.durationSeconds ?: 0)
        progress.visibility = if (fraction == null) View.GONE else View.VISIBLE
        progress.scaleX = fraction ?: 0f
        showArt(playing?.art)
    }

    private fun showArt(art: Bitmap?) {
        if (art === shownArt) return
        shownArt = art
        if (art == null) {
            artView.visibility = View.GONE
            artView.setImageDrawable(null)
            card.setColor(MusicBarPolicy.DEFAULT_CARD_COLOR)
            return
        }
        artView.setImageDrawable(RoundedBitmapDrawableFactory.create(resources, art).apply {
            cornerRadius = ART_CORNER
        })
        artView.visibility = View.VISIBLE
        // One pixel is the average of the whole picture.
        val average = Bitmap.createScaledBitmap(art, 1, 1, true).getPixel(0, 0)
        card.setColor(MusicBarPolicy.cardColorFor(average))
    }

    private fun line(size: Float, color: Int, style: Int) = TextView(context).apply {
        setTextColor(color)
        textSize = size
        setTypeface(typeface, style)
        includeFontPadding = false
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }

    private fun button(icon: Int, keyCode: Int, color: Int) = ImageButton(context).apply {
        setImageResource(icon)
        setColorFilter(color)
        setBackgroundColor(Color.TRANSPARENT)
        setOnClickListener { onKey(keyCode) }
    }

    private companion object {
        const val CARD_GAP = 5
        const val CARD_CORNER = 22f
        const val PILL_CORNER = 25f
        const val ART_SIZE = 58
        const val ART_CORNER = 14f
    }
}

package com.andrerinas.openheadunit.aap

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

/** The strip under Android Auto: what is playing, and the three buttons that control it. */
class MusicBarView(context: Context, private val onKey: (Int) -> Unit) : LinearLayout(context) {

    private val title = line(18f)
    private val artist = line(14f)
    private val playPause = button(android.R.drawable.ic_media_pause, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        setBackgroundColor(Color.BLACK)
        setPadding(24, 0, 24, 0)
        isClickable = true

        addView(LinearLayout(context).apply {
            orientation = VERTICAL
            addView(title)
            addView(artist)
        }, LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f))
        addView(button(android.R.drawable.ic_media_previous, KeyEvent.KEYCODE_MEDIA_PREVIOUS), buttonParams())
        addView(playPause, buttonParams())
        addView(button(android.R.drawable.ic_media_next, KeyEvent.KEYCODE_MEDIA_NEXT), buttonParams())
    }

    fun show(playing: NowPlaying?) {
        title.text = playing?.title.orEmpty()
        artist.text = playing?.artist.orEmpty()
        playPause.setImageResource(
            if (playing?.isPlaying == false) android.R.drawable.ic_media_play else android.R.drawable.ic_media_pause
        )
    }

    private fun line(size: Float) = TextView(context).apply {
        setTextColor(Color.WHITE)
        textSize = size
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }

    private fun button(icon: Int, keyCode: Int) = ImageButton(context).apply {
        setImageResource(icon)
        setBackgroundColor(Color.TRANSPARENT)
        setOnClickListener { onKey(keyCode) }
    }

    private fun buttonParams() = LayoutParams(112, LayoutParams.MATCH_PARENT)
}

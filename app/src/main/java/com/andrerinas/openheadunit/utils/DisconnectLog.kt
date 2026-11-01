package com.andrerinas.openheadunit.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A short history of the connection, in Italian, kept apart from the debug logs so it is always
 * on: when the session dropped, why, how many wake rounds it took and how long until it was back.
 *
 * Every write is guarded: a history that cannot be written must never take a session down with it.
 */
object DisconnectLog {

    private const val FILE_NAME = "registro_disconnessioni.txt"
    private const val MAX_LINES = 1000
    private const val SHOWN_LINES = 300

    private var file: File? = null
    private var startedAt = 0L
    private var connectedAt = 0L
    private var droppedAt = 0L
    private var attempts = 0
    private var cause: String? = null

    @Synchronized
    fun init(context: Context) {
        if (file == null) initFile(File(context.applicationContext.filesDir, FILE_NAME))
    }

    @Synchronized
    internal fun initFile(target: File) {
        file = target
        startedAt = SystemClock.elapsedRealtime()
        connectedAt = 0L
        droppedAt = 0L
        attempts = 0
        cause = null
        trim()
        append("AVVIO", "App avviata")
    }

    /** A wake round was sent to the phone. */
    @Synchronized
    fun noteAttempt() {
        attempts++
    }

    /** The low-level reason a session is about to end, picked up by [noteDisconnected]. */
    @Synchronized
    fun noteCause(text: String) {
        cause = text
    }

    @Synchronized
    fun noteConnected() {
        val now = SystemClock.elapsedRealtime()
        if (connectedAt != 0L) return
        val rounds = roundsText(attempts)
        when {
            droppedAt != 0L -> append("RICOLLEGATO", "dopo ${formatDuration(now - droppedAt)}, $rounds")
            else -> append("COLLEGATO", "dopo ${formatDuration(now - startedAt)} dall'avvio, $rounds")
        }
        connectedAt = now
        droppedAt = 0L
        attempts = 0
        cause = null
    }

    /** Ignored when no session was up, so failed attempts do not read as drops. */
    @Synchronized
    fun noteDisconnected(reason: String, unexpected: Boolean) {
        val now = SystemClock.elapsedRealtime()
        if (connectedAt == 0L) return
        val detail = cause?.takeIf { unexpected }?.let { ": $it" } ?: ""
        append("CADUTO", "$reason$detail, sessione durata ${formatDuration(now - connectedAt)}")
        connectedAt = 0L
        droppedAt = now
        attempts = 0
        cause = null
    }

    /** The newest lines first, or an empty string when there is nothing yet. */
    @Synchronized
    fun read(): String = try {
        file?.takeIf { it.exists() }?.readLines().orEmpty()
            .takeLast(SHOWN_LINES).reversed().joinToString("\n")
    } catch (e: Exception) {
        AppLog.e("DisconnectLog: could not read the history", e)
        ""
    }

    /** A copy for the share sheet, in the cache root the file provider covers. */
    fun copyForSharing(context: Context): File? = try {
        val source = synchronized(this) { file }?.takeIf { it.exists() }
        source?.copyTo(File(context.cacheDir, exportName()), overwrite = true)
    } catch (e: Exception) {
        AppLog.e("DisconnectLog: could not prepare the history for sharing", e)
        null
    }

    /**
     * Saves a copy where a file manager can reach it and returns where it went. The public
     * Downloads folder when the unit allows it, otherwise this app's own folder.
     */
    fun saveCopy(context: Context): String? {
        val source = synchronized(this) { file }?.takeIf { it.exists() } ?: return null
        val name = exportName()
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("MediaStore refused the file")
                context.contentResolver.openOutputStream(uri).use { out ->
                    source.inputStream().use { it.copyTo(requireNotNull(out)) }
                }
                "${Environment.DIRECTORY_DOWNLOADS}/$name"
            } else {
                @Suppress("DEPRECATION")
                val target = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name)
                source.copyTo(target, overwrite = true).absolutePath
            }
        } catch (e: Exception) {
            AppLog.e("DisconnectLog: could not save to Downloads, trying the app's own folder", e)
            try {
                val dir = LogFilesHelper.resolveLogDirectory(context) ?: return null
                source.copyTo(File(dir, name), overwrite = true).absolutePath
            } catch (e2: Exception) {
                AppLog.e("DisconnectLog: could not save the history", e2)
                null
            }
        }
    }

    internal fun formatDuration(ms: Long): String {
        val total = (ms.coerceAtLeast(0L) + 500L) / 1000L
        val hours = total / 3600
        val minutes = (total % 3600) / 60
        val seconds = total % 60
        return when {
            hours > 0 -> "$hours h $minutes min"
            minutes > 0 -> "$minutes min $seconds s"
            else -> "$seconds s"
        }
    }

    private fun roundsText(rounds: Int) = if (rounds == 1) "1 tentativo" else "$rounds tentativi"

    private fun exportName() =
        "registro_disconnessioni_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.txt"

    private fun append(kind: String, text: String) {
        try {
            val time = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALY).format(Date())
            file?.appendText("$time | $kind | $text\n")
        } catch (e: Exception) {
            AppLog.e("DisconnectLog: could not write the history", e)
        }
    }

    private fun trim() {
        try {
            val target = file?.takeIf { it.exists() } ?: return
            val lines = target.readLines()
            if (lines.size > MAX_LINES) {
                target.writeText(lines.takeLast(MAX_LINES).joinToString("\n", postfix = "\n"))
            }
        } catch (e: Exception) {
            AppLog.e("DisconnectLog: could not trim the history", e)
        }
    }
}

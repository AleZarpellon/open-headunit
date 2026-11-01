package com.andrerinas.openheadunit.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.andrerinas.openheadunit.ssl.ConscryptInitializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.HttpsURLConnection

/**
 * The update offered when the app starts: fetches the release APK and hands it to Android's own
 * installer, which still asks the user to confirm and refuses a file signed with another key.
 */
object UpdateInstaller {

    const val STARTUP_CHECK_DELAY_MS = 5_000L

    private const val PREFS = "update_prefs"
    private const val KEY_SKIPPED = "skipped_version"
    private val startupCheckClaimed = AtomicBoolean(false)

    /** True once per process: the activity can be recreated, and that is not a new start. */
    fun claimStartupCheck(): Boolean = startupCheckClaimed.compareAndSet(false, true)

    fun shouldOffer(info: UpdateInfo, skippedVersion: String?): Boolean =
        info.isUpdateAvailable && !info.isPlayStore && info.apkUrl != null &&
            info.latestVersionName != skippedVersion

    fun skippedVersion(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_SKIPPED, null)

    fun skipVersion(context: Context, version: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_SKIPPED, version).apply()
    }

    /** The downloaded APK, or null if it did not arrive whole. */
    suspend fun download(context: Context, url: String): File? = withContext(Dispatchers.IO) {
        try {
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 60_000
                setRequestProperty("User-Agent", "OpenHeadunit-App")
            }
            if (conn is HttpsURLConnection) {
                ConscryptInitializer.httpsSocketFactory(context)?.let { conn.sslSocketFactory = it }
            }
            if (conn.responseCode != 200) {
                AppLog.e("UpdateInstaller: HTTP ${conn.responseCode} downloading the update")
                return@withContext null
            }
            val dir = File(context.cacheDir, "updates").apply { mkdirs() }
            val apk = File(dir, "update.apk")
            val written = conn.inputStream.use { input -> apk.outputStream().use { input.copyTo(it) } }
            val expected = conn.contentLength.toLong()
            if (expected > 0 && written != expected) {
                AppLog.e("UpdateInstaller: got $written of $expected bytes")
                apk.delete()
                return@withContext null
            }
            apk
        } catch (e: Exception) {
            AppLog.e("UpdateInstaller: download failed: ${e.message}", e)
            null
        }
    }

    /** False if Android has nothing to open the file with. */
    fun install(context: Context, apk: File): Boolean = try {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apk)
        context.startActivity(Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        true
    } catch (e: Exception) {
        AppLog.e("UpdateInstaller: could not open the installer: ${e.message}", e)
        false
    }
}

package com.andrerinas.openheadunit.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateInstallerTest {

    private fun info(
        available: Boolean = true,
        playStore: Boolean = false,
        apk: String? = "https://example.invalid/app.apk",
    ) = UpdateInfo(available, "1.3", "1.2", "https://example.invalid", apk, playStore)

    @Test
    fun `a newer release with an APK is offered`() {
        assertTrue(UpdateInstaller.shouldOffer(info(), skippedVersion = null))
    }

    @Test
    fun `nothing is offered when already up to date`() {
        assertFalse(UpdateInstaller.shouldOffer(info(available = false), skippedVersion = null))
    }

    @Test
    fun `a skipped version is not offered again, but the next one is`() {
        assertFalse(UpdateInstaller.shouldOffer(info(), skippedVersion = "1.3"))
        assertTrue(UpdateInstaller.shouldOffer(info(), skippedVersion = "1.2"))
    }

    @Test
    fun `a release without an APK has nothing to install`() {
        assertFalse(UpdateInstaller.shouldOffer(info(apk = null), skippedVersion = null))
    }

    @Test
    fun `a Play Store install is not offered a file`() {
        assertFalse(UpdateInstaller.shouldOffer(info(playStore = true), skippedVersion = null))
    }
}

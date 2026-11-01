package com.andrerinas.openheadunit.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DisconnectLogTest {

    @get:Rule
    val folder = TemporaryFolder()

    @Test
    fun `durations read in plain units`() {
        assertEquals("0 s", DisconnectLog.formatDuration(0L))
        assertEquals("42 s", DisconnectLog.formatDuration(42_000L))
        assertEquals("2 min 5 s", DisconnectLog.formatDuration(125_000L))
        assertEquals("1 h 1 min", DisconnectLog.formatDuration(3_660_000L))
    }

    @Test
    fun `a drop and its recovery are both recorded, newest first`() {
        DisconnectLog.initFile(folder.newFile())
        DisconnectLog.noteConnected()
        DisconnectLog.noteCause("lettura fallita")
        DisconnectLog.noteDisconnected("Collegamento perso", unexpected = true)
        DisconnectLog.noteAttempt()
        DisconnectLog.noteAttempt()
        DisconnectLog.noteConnected()

        val lines = DisconnectLog.read().lines()
        assertTrue(lines[0], lines[0].contains("RICOLLEGATO") && lines[0].contains("2 tentativi"))
        assertTrue(lines[1], lines[1].contains("CADUTO") && lines[1].contains("Collegamento perso: lettura fallita"))
        assertTrue(lines[2], lines[2].contains("COLLEGATO"))
        assertTrue(lines[3], lines[3].contains("AVVIO"))
    }

    @Test
    fun `a failed attempt with no session behind it is not a drop`() {
        DisconnectLog.initFile(folder.newFile())
        DisconnectLog.noteDisconnected("Collegamento perso", unexpected = true)
        assertFalse(DisconnectLog.read().contains("CADUTO"))
    }

    @Test
    fun `a low level cause is left out of a deliberate end`() {
        DisconnectLog.initFile(folder.newFile())
        DisconnectLog.noteConnected()
        DisconnectLog.noteCause("lettura fallita")
        DisconnectLog.noteDisconnected("Disconnessione manuale", unexpected = false)
        assertFalse(DisconnectLog.read().contains("lettura fallita"))
    }
}

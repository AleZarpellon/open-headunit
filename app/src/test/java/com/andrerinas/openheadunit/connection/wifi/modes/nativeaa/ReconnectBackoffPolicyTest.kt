package com.andrerinas.openheadunit.connection.wifi.modes.nativeaa

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReconnectBackoffPolicyTest {

    @Test
    fun `the gaps grow and then stay at thirty seconds`() {
        val gaps = (1..7).map { ReconnectBackoffPolicy.gapMs(it) }
        assertEquals(listOf(2_000L, 5_000L, 10_000L, 30_000L, 30_000L, 30_000L, 30_000L), gaps)
    }

    @Test
    fun `a round count of zero still waits the first gap`() {
        assertEquals(2_000L, ReconnectBackoffPolicy.gapMs(0))
    }

    @Test
    fun `a very long search never gives up or widens further`() {
        assertEquals(30_000L, ReconnectBackoffPolicy.gapMs(100_000))
    }

    @Test
    fun `a hold counts down and never exceeds the longest gap`() {
        assertEquals(10_000L, ReconnectBackoffPolicy.holdRemainingMs(heldUntil = 20_000L, now = 10_000L))
        assertEquals(0L, ReconnectBackoffPolicy.holdRemainingMs(heldUntil = 5_000L, now = 10_000L))
        assertEquals(30_000L, ReconnectBackoffPolicy.holdRemainingMs(heldUntil = 90_000L, now = 0L))
    }

    private fun lapsed(
        running: Boolean = true,
        disconnected: Boolean = true,
        wakesPhone: Boolean = true,
        wakeLoopRunning: Boolean = false,
        attemptInFlight: Boolean = false,
        selectionHeld: Boolean = false,
    ) = ReconnectBackoffPolicy.wakeLapsed(running, disconnected, wakesPhone, wakeLoopRunning, attemptInFlight, selectionHeld)

    @Test
    fun `a loop that ended with the phone still away is started again`() {
        assertTrue(lapsed())
    }

    @Test
    fun `a manual disconnect is respected`() {
        assertFalse(lapsed(wakesPhone = false))
        assertFalse(lapsed(running = false))
    }

    @Test
    fun `nothing is started while something is already trying`() {
        assertFalse(lapsed(disconnected = false))
        assertFalse(lapsed(wakeLoopRunning = true))
        assertFalse(lapsed(attemptInFlight = true))
        assertFalse(lapsed(selectionHeld = true))
    }
}

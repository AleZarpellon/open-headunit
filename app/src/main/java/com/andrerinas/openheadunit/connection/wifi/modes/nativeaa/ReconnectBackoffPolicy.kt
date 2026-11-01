package com.andrerinas.openheadunit.connection.wifi.modes.nativeaa

/**
 * The reconnection guardian's timing: how long to wait between wake rounds, and when a wake loop
 * that ended on its own has to be started again.
 *
 * The gaps grow and then stay flat, and nothing here ever gives up: only a manual disconnect stops
 * the guardian. [JoinRefusalPolicy] can still widen a gap, because a phone that keeps refusing the
 * network is flickering "Connecting to Android Auto" on every round.
 */
object ReconnectBackoffPolicy {

    private val GAPS_MS = longArrayOf(2_000L, 5_000L, 10_000L, 30_000L)

    const val MAX_GAP_MS = 30_000L

    /** How often the guardian checks that something is still trying to bring the phone back. */
    const val GUARD_CHECK_MS = 30_000L

    /** The wait after [roundsDone] wake rounds since the last session. */
    fun gapMs(roundsDone: Int): Long = GAPS_MS[(roundsDone - 1).coerceIn(0, GAPS_MS.size - 1)]

    /** What is left of a hold set until [heldUntil], 0 if none. */
    fun holdRemainingMs(heldUntil: Long, now: Long): Long =
        (heldUntil - now).coerceIn(0L, MAX_GAP_MS)

    /**
     * Whether the wake loop has to be started again. It ends by itself when no phone is eligible,
     * when a session or handshake interrupts it, and in a few other cases, and nothing used to
     * start it again until the credentials were next delivered.
     */
    fun wakeLapsed(
        running: Boolean,
        disconnected: Boolean,
        wakesPhone: Boolean,
        wakeLoopRunning: Boolean,
        attemptInFlight: Boolean,
        selectionHeld: Boolean,
    ): Boolean = running && disconnected && wakesPhone && !wakeLoopRunning && !attemptInFlight && !selectionHeld
}

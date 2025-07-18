package org.everbuild.celestia.orion.core.util

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class Cooldown(var duration: Duration) {
    private var lastCall = 0L

    fun get(): Boolean {
        val now = System.currentTimeMillis()
        if(now - lastCall >= duration.inWholeMilliseconds) {
            lastCall = now
            return true
        }
        return false
    }

    fun getTimeToNextExecution(): Duration {
        val now = System.currentTimeMillis()
        val elapsed = now - lastCall
        val remaining = duration.inWholeMilliseconds - elapsed
        return if (remaining > 0) {
            remaining.milliseconds
        } else {
            Duration.ZERO
        }
    }
}
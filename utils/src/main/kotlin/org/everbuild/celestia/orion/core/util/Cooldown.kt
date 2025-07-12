package org.everbuild.celestia.orion.core.util

import kotlin.time.Duration

class Cooldown(private val duration: Duration) {
    private var lastCall = 0L

    fun get(): Boolean {
        val now = System.currentTimeMillis()
        if(now - lastCall >= duration.inWholeMilliseconds) {
            lastCall = now
            return true
        }
        return false

    }
}
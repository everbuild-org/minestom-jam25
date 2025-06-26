package org.everbuild.celestia.orion.core.remote.schedulers

import org.everbuild.celestia.orion.core.platform.OrionPlatform

class BroadcastingScheduler(private val platform: OrionPlatform) : Runnable {
    fun start() {
        Thread.ofVirtual().start(this)
    }

    override fun run() {
        while (true) {
            iterate()
            Thread.sleep(2000)
        }
    }

    private fun iterate() {
        broadcastServerState(platform)
        broadcastPlayerState(platform)
    }
}
package org.everbuild.celestia.orion.core.remote.schedulers

import org.everbuild.celestia.orion.core.remote.RemotePlatform

class RequeryScheduler : Runnable {
    fun start() {
        Thread.ofVirtual().start(this)
    }

    override fun run() {
        while (true) {
            RemotePlatform.refreshServersAndClients()
            Thread.sleep(1000)
        }
    }
}
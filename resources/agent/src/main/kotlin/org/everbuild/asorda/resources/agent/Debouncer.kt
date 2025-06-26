package org.everbuild.asorda.resources.agent

import org.everbuild.asorda.resources.triggerRefresh

class Debouncer : Thread() {
    private var count = 0
    private var lastCount = 0
    private var lastReloadCount = 0

    override fun run() {
        while (true) {
            if (count != lastCount) {
                lastCount = count
            }
            sleep(500)

            if (count == lastCount && lastReloadCount != lastCount) {
                lastReloadCount = lastCount
                triggerRefresh()
            }
        }
    }

    fun ackquire() {
        count += 1
    }
}
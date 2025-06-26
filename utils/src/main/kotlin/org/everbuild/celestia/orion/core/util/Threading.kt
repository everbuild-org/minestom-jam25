package org.everbuild.celestia.orion.core.util

fun greenThread(block: () -> Unit): Thread{
    return Thread.ofVirtual().start {
        block()
    }
}
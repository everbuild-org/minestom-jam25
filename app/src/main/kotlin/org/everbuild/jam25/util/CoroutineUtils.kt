package org.everbuild.jam25.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import org.everbuild.celestia.orion.core.OrionCore
import org.everbuild.celestia.orion.platform.minestom.api.Mc

fun <T> defer(fn: suspend () -> T): Deferred<T> = OrionCore.scope.async { fn() }

fun background(fn: suspend () -> Unit): Job = OrionCore.scope.launch { fn() }

fun dedicatedBackground(fn: suspend () -> Unit): Thread = Thread { runBlocking { fn() } }.also { it.start() }

suspend fun launchWithContext(fn: suspend CoroutineScope.() -> Unit) {
    withContext(Dispatchers.Default) {
        launch { fn() }
    }
}

suspend fun <T> Collection<T>.forEachParallel(batchSize: Int = 20, fn: suspend (T) -> Unit) {
    if (this.isEmpty()) return
    
    if (this.size == 1) {
        fn(this.first())
        return
    }

    val semaphore = Semaphore(batchSize)
    val jobs = mutableListOf<Job>()

    this.chunked(batchSize).forEach { batch ->
        val job = OrionCore.scope.launch {
            try {
                batch.forEach { item ->
                    semaphore.acquire()
                    try {
                        fn(item)
                    } finally {
                        semaphore.release()
                    }
                }
            } catch (e: Exception) {
                Mc.exception.handleException(e)
            }
        }
        jobs.add(job)
    }

    jobs.forEach { it.join() }
}
package org.everbuild.asorda.resources

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias StepFn = (String) -> Unit

class TerminalSpinner(private val frames: List<String>, private val delayMs: Long = 500, var step: String = "Loading") {
    private var isRunning = false

    suspend fun start() {
        isRunning = true
        while (isRunning && coroutineContext.isActive) {
            for (frame in frames) {
                print("\r$frame $step                                  ")
                System.out.flush()
                delay(delayMs)
                if (!isRunning) break
            }
        }
    }

    fun stop() {
        isRunning = false
        println("\r✓ Done!")
    }
}

suspend fun <T> spinner(block: suspend (StepFn) -> T): T {
    val spinner = TerminalSpinner(
        listOf(
            "[    ]",
            "[=   ]",
            "[==  ]",
            "[=== ]",
            "[====]",
            "[ ===]",
            "[  ==]",
            "[   =]",
            "[    ]",
            "[   =]",
            "[  ==]",
            "[ ===]",
            "[====]",
            "[=== ]",
            "[==  ]",
            "[=   ]"
        )
    )
    return withContext(Dispatchers.Default) {
        launch {
            spinner.start()
        }
        val result = block {
            spinner.step = it
        }
        spinner.stop()
        return@withContext result
    }
}

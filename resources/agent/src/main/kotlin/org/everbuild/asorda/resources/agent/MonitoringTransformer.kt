package org.everbuild.asorda.resources.agent

import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.IllegalClassFormatException
import java.security.ProtectionDomain

class MonitoringTransformer : ClassFileTransformer {
    private val alreadyLoadedClasses = mutableSetOf<String>()
    private val stateDebouncer = Debouncer()

    init {
        stateDebouncer.start()
    }

    @Throws(IllegalClassFormatException::class)
    override fun transform(
        loader: ClassLoader,
        className: String,
        classBeingRedefined: Class<*>?,
        protectionDomain: ProtectionDomain,
        classfileBuffer: ByteArray
    ): ByteArray {
        if (className.startsWith("org/everbuild/asorda/resources/")) {
            if (!alreadyLoadedClasses.contains(className)) {
                alreadyLoadedClasses.add(className)
            } else {
                stateDebouncer.ackquire()
            }
        }

        return classfileBuffer
    }
}
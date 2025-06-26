package org.everbuild.asorda.resources.agent

import java.lang.instrument.Instrumentation

object ResourceAgent {
    @JvmStatic
    fun premain(agentArgs: String?, inst: Instrumentation) {
        inst.addTransformer(MonitoringTransformer())
    }
}
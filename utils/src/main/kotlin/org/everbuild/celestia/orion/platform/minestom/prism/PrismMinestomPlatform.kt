package org.everbuild.celestia.orion.platform.minestom.prism

import kotlin.time.Duration
import kotlin.time.toJavaDuration
import org.everbuild.celestia.orion.core.kube.KubernetesProvider
import org.everbuild.celestia.orion.platform.minestom.api.Mc
import org.everbuild.celestia.orion.platform.minestom.util.tickLater
import org.everbuild.prism.client.AbstractPrismPlatform

class PrismMinestomPlatform : AbstractPrismPlatform() {
    private var isHealthy = false
    var callback: PrismProfileLoaderCallback? = null

    init {
        tickLater {
            isHealthy = true
        }
    }

    override fun canUseKubernetes(): Boolean = KubernetesProvider.canProvideKubernetes()

    override fun getExtraForceloadedProfiles(): String = callback?.getExtraForceloadedProfiles() ?: ""

    override fun getPlayerCount(): Int = Mc.connection.onlinePlayerCount

    override fun getPlayerProfiles(): List<Int> = listOf() // A paper server is never a profile provider

    override fun isHealthy(): Boolean = isHealthy

    override fun schedule(sleep: Duration, block: () -> Unit) {
        Mc.scheduler
            .buildTask(block)
            .repeat(sleep.toJavaDuration())
            .schedule()
    }

    override fun syncLoadedProfiles(profiles: List<Int>) {
        callback?.syncLoadedProfiles(profiles)
    }
}
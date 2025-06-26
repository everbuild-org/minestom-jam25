package org.everbuild.celestia.orion.core

import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.everbuild.celestia.orion.core.autoconfigure.SharedPropertyConfig
import org.everbuild.celestia.orion.core.chat.ChatImageTextureResolver
import org.everbuild.celestia.orion.core.chatmanager.BadWordConfig
import org.everbuild.celestia.orion.core.database.Migrator
import org.everbuild.celestia.orion.core.database.playerdata.onPlayerdataPlayerJoin
import org.everbuild.celestia.orion.core.packs.OrionPacks
import org.everbuild.celestia.orion.core.platform.OrionPlatform
import org.everbuild.celestia.orion.core.platform.onPlatformPlayerJoin
import org.everbuild.celestia.orion.core.platform.onPlatformPlayerLeave
import org.everbuild.celestia.orion.core.remote.RemotePlatform
import org.everbuild.celestia.orion.core.translation.Translator
import org.everbuild.celestia.orion.core.util.globalOrion

@Suppress("LeakingThis")
open class OrionCore<Platform : OrionPlatform>(val platform: Platform) {
    init {
        globalOrion = this
        javaClass.getResourceAsStream("/resources.lock.json")!!.copyTo(File("resources.lock.json").outputStream())

        SharedPropertyConfig
        BadWordConfig
        Translator
        OrionPacks

        Migrator.assertMigrationTable()

        Migrator.migrate(
            OrionCore::class.java, "migrations",
            listOf(
                "0000-playerdata",
                "0001-coin-transactions",
                "0002-bank"
            )
        )
    }

    val chatTextureResolver = ChatImageTextureResolver(this)

    fun load() {
        platform.registerJoinEvent {
            onPlayerdataPlayerJoin(it)
            onPlatformPlayerJoin(it)
        }

        platform.registerLeaveEvent {
            onPlatformPlayerLeave(it)
        }

        RemotePlatform.listenAndSchedule(platform)
    }

    companion object {
        val backgroundThreadPool: ExecutorService = Executors.newScheduledThreadPool(5)
        val executorService = backgroundThreadPool.asCoroutineDispatcher()
        val scope = CoroutineScope(executorService)
    }
}
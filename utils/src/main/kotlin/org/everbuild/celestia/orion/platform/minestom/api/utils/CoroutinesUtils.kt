package org.everbuild.celestia.orion.platform.minestom.api.utils

import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Entity
import net.minestom.server.entity.Player
import net.minestom.server.instance.Chunk
import net.minestom.server.instance.IChunkLoader
import net.minestom.server.instance.Instance
import net.minestom.server.instance.InstanceContainer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Entity.waitNextTick(): Entity = suspendCoroutine { cont -> scheduleNextTick { cont.resume(it) } }

suspend fun Entity.suspendTeleport(position: Pos): Unit = suspendCoroutine { teleport(position).whenComplete { _, _ -> it.resume(Unit) } }
suspend fun Player.suspendTeleport(position: Pos): Unit = suspendCoroutine { teleport(position).whenComplete { _, _ -> it.resume(Unit) } }

suspend fun Instance.suspendSaveChunkToStorage(chunk: Chunk): Unit = suspendCoroutine { saveChunkToStorage(chunk).whenComplete { _, _ -> it.resume(Unit) } }
suspend fun Instance.suspendSaveChunksToStorage(): Unit = suspendCoroutine { saveChunksToStorage().whenComplete { _, _ -> it.resume(Unit) } }

suspend fun InstanceContainer.suspendSaveInstance(): Unit = suspendCoroutine { saveInstance().whenComplete { _, _ -> it.resume(Unit) } }
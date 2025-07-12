package org.everbuild.celestia.orion.core.util

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

private val minimessage = MiniMessage.miniMessage()

fun String.component() = Component.text(this)
fun String.minimessage() = minimessage.deserialize(this)
fun Audience.sendMiniMessage(message: String) = this.sendMessage(minimessage.deserialize(message))
fun Audience.sendMiniMessageActionBar(message: String) = this.sendActionBar(minimessage.deserialize(message))

operator fun Component.plus(component: Component) = this.append(component)
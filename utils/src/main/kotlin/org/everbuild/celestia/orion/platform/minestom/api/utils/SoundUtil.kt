package org.everbuild.celestia.orion.platform.minestom.api.utils

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.minestom.server.entity.Player

fun Player.pling() {
    playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 1f, 1f))
}

fun Player.levelUp() {
    playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 1f, 1f))
}

fun Player.error() {
    playSound(Sound.sound(Key.key("minecraft:block.note_block.bass"), Sound.Source.MASTER, 1f, 1f))
}

fun Player.click() {
    playSound(Sound.sound(Key.key("minecraft:ui.button.click"), Sound.Source.MASTER, 1f, 1f))
}
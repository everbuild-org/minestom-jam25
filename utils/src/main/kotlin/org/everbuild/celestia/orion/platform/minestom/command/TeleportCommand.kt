package org.everbuild.celestia.orion.platform.minestom.command

import net.minestom.server.coordinate.Pos
import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import org.everbuild.celestia.orion.platform.minestom.api.command.Kommand
import org.everbuild.celestia.orion.platform.minestom.util.sendTranslated

object TeleportCommand : Kommand("teleport", "tp") {
    init {
        permission = "orion.command.teleport"
        default { player, _ ->
            player.sendTranslated("orion.command.teleport.usage")
        }

        val x = Arg.double("x")
        val y = Arg.double("y")
        val z = Arg.double("z")
        val targetToTeleport = Arg.player("targetToTeleport")
        val targetToTeleportTo = Arg.player("teleportToTarget")

        // Teleport self to coordinates
        command {
            args += x
            args += y
            args += z
            executes {
                val pos = Pos(args[x], args[y], args[z])
                player.teleport(pos)
                player.sendTranslated("orion.command.teleport.arg1.self") {
                    it.replace("target", "${pos.x.toInt()} ${pos.y.toInt()} ${pos.z.toInt()}")
                }
            }
        }

        // Teleport self to another player
        command {
            args += targetToTeleportTo
            executes {
                val target = args[targetToTeleportTo]
                val newInstance = target.instance
                if (newInstance == player.instance)
                    player.teleport(target.position)
                else
                    player.setInstance(newInstance, target.position)
                player.sendTranslated("orion.command.teleport.arg1.self") {
                    it.replace("target", target.username)
                }
                target.sendTranslated("orion.command.teleport.arg1.other") {
                    it.replace("sender", player.username)
                }
            }
        }

        requiresPermission("orion.command.teleport.other") {
            command {
                args += targetToTeleport

                // Teleport another player to coordinates
                command {
                    args += x
                    args += y
                    args += z
                    executes {
                        val target = args[targetToTeleport]
                        val pos = Pos(args[x], args[y], args[z])
                        target.teleport(pos)
                        player.sendTranslated("orion.command.teleport.player.notify") {
                            it.replace("targettoteleport", target.username)
                            it.replace("coords", "${pos.x.toInt()} ${pos.y.toInt()} ${pos.z.toInt()}")
                        }
                        target.sendTranslated("orion.command.tp.player") {
                            it.replace("targettoteleportto", "${pos.x.toInt()} ${pos.y.toInt()} ${pos.z.toInt()}")
                            it.replace("player", player.username)
                        }
                    }
                }

                // Teleport another player to another player
                command {
                    args += targetToTeleportTo
                    executes {
                        val target = args[targetToTeleport]
                        val destination = args[targetToTeleportTo]
                        val newInstance = destination.instance
                        if (newInstance == target.instance)
                            target.teleport(destination.position)
                        else
                            target.setInstance(newInstance, destination.position)
                        player.sendTranslated("orion.tp.target.tp.notify") {
                            it.replace("targettoteleport", target.username)
                            it.replace("targettoteleportto", destination.username)
                        }
                        target.sendTranslated("orion.command.tp.player") {
                            it.replace("targettoteleportto", destination.username)
                            it.replace("player", player.username)
                        }
                        destination.sendTranslated("orion.command.tp.target") {
                            it.replace("player", player.username)
                            it.replace("targettoteleport", target.username)
                        }
                    }
                }

            }
        }
    }
}
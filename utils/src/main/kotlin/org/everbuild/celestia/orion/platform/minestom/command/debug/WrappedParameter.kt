package org.everbuild.celestia.orion.platform.minestom.command.debug

import net.minestom.server.command.builder.arguments.Argument
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.particle.Particle
import net.minestom.server.utils.entity.EntityFinder
import net.minestom.server.utils.location.RelativeVec
import org.everbuild.celestia.orion.core.database.playerdata.OrionPlayer
import org.everbuild.celestia.orion.core.database.playerdata.PlayerLoader
import org.everbuild.celestia.orion.platform.minestom.api.command.Arg
import java.lang.reflect.Parameter
import java.security.InvalidParameterException
import java.util.UUID

class WrappedParameter(parameter: Parameter, prefix: String) {
    var cast: (Any, Player) -> Any = { it, _ -> it }
    val name: String = parameter.name
    val argument: Argument<*>
    private val argType: String

    init {
        when (parameter.type) {
            OrionPlayer::class.java -> {
                argument = Arg.orionPlayer("$prefix/$name")
                argType = "orionPlayer"
                cast = {it, _ -> PlayerLoader.load(it as String)!! }
            }
            Player::class.java -> {
                argument = Arg.player("$prefix/$name")
                argType = "player"
                cast = { it, sender -> (it as EntityFinder).findFirstPlayer(sender)!! }
            }
            Boolean::class.java -> {
                argument = Arg.bool("$prefix/$name")
                argType = "boolean"
            }
            Int::class.java -> {
                argument = Arg.int("$prefix/$name")
                argType = "int"
            }
            Long::class.java -> {
                argument = Arg.long("$prefix/$name")
                argType = "long"
            }
            String::class.java -> {
                argument = Arg.string("$prefix/$name")
                argType = "string"
            }
            Double::class.java -> {
                argument = Arg.double("$prefix/$name")
                argType = "double"
            }
            Float::class.java -> {
                argument = Arg.float("$prefix/$name")
                argType = "float"
            }
            ItemStack::class.java -> {
                argument = Arg.float("$prefix/$name")
                argType = "itemStack"
            }
            UUID::class.java -> {
                argument = Arg.uuid("$prefix/$name")
                argType = "uuid"
            }
            Particle::class.java -> {
                argument = Arg.particle("$prefix/$name")
                argType = "particle"
            }
            RelativeVec::class.java -> {
                argument = Arg.relvec3("$prefix/$name")
                argType = "relativeVec3"
            }
            else -> throw InvalidParameterException("Unsupported parameter type: ${parameter.type}")
        }
    }

    override fun toString(): String = "$name:$argType"
}
package org.everbuild.celestia.orion.platform.minestom.api.command

import net.minestom.server.command.builder.arguments.*
import net.minestom.server.command.builder.arguments.minecraft.*
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentEntityType
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentParticle
import net.minestom.server.command.builder.arguments.number.ArgumentDouble
import net.minestom.server.command.builder.arguments.number.ArgumentFloat
import net.minestom.server.command.builder.arguments.number.ArgumentInteger
import net.minestom.server.command.builder.arguments.number.ArgumentLong
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeBlockPosition
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec2
import net.minestom.server.command.builder.arguments.relative.ArgumentRelativeVec3

object Arg {
    fun literal(id: String): ArgumentLiteral = ArgumentType.Literal(id)
    fun group(id: String, vararg args: Argument<*>): ArgumentGroup = ArgumentType.Group(id, *args)
    fun <T> loop(id: String, vararg args: Argument<T>): ArgumentLoop<T> = ArgumentType.Loop(id, *args)
    fun bool(id: String): ArgumentBoolean = ArgumentType.Boolean(id)
    fun int(id: String): ArgumentInteger = ArgumentType.Integer(id)
    fun float(id: String): ArgumentFloat = ArgumentType.Float(id)
    fun double(id: String): ArgumentDouble = ArgumentType.Double(id)
    fun string(id: String): ArgumentString = ArgumentType.String(id)
    fun word(id: String): ArgumentWord = ArgumentType.Word(id)
    fun stringArray(id: String): ArgumentStringArray = ArgumentType.StringArray(id)
    fun command(id: String): ArgumentCommand = ArgumentType.Command(id)
    inline fun <R, reified T : Enum<R>> enum(id: String): ArgumentEnum<T> = ArgumentType.Enum(id, T::class.java).also { it.setFormat(ArgumentEnum.Format.LOWER_CASED) }!!
    fun color(id: String): ArgumentColor = ArgumentType.Color(id)
    fun time(id: String): ArgumentTime = ArgumentType.Time(id)
    fun particle(id: String): ArgumentParticle = ArgumentType.Particle(id)
    fun resource(id: String, identifier: String): ArgumentResource = ArgumentType.Resource(id, identifier)
    fun entitytype(id: String): ArgumentEntityType = ArgumentType.EntityType(id)
    fun blockstate(id: String): ArgumentBlockState = ArgumentType.BlockState(id)
    fun intrange(id: String): ArgumentIntRange = ArgumentType.IntRange(id)
    fun floatrange(id: String): ArgumentFloatRange = ArgumentType.FloatRange(id)
    fun entity(id: String): ArgumentEntity = ArgumentType.Entity(id)
    fun itemstack(id: String): ArgumentItemStack = ArgumentType.ItemStack(id)
    fun component(id: String): ArgumentComponent = ArgumentType.Component(id)
    fun uuid(id: String): ArgumentUUID = ArgumentType.UUID(id)
    fun nbt(id: String): ArgumentNbtTag = ArgumentType.NBT(id)
    fun relblockpos(id: String): ArgumentRelativeBlockPosition = ArgumentType.RelativeBlockPosition(id)
    fun relvec3(id: String): ArgumentRelativeVec3 = ArgumentType.RelativeVec3(id)
    fun relvec2(id: String): ArgumentRelativeVec2 = ArgumentType.RelativeVec2(id)
    fun long(id: String): ArgumentLong = ArgumentType.Long(id)
    fun player(id: String): PlayerArgument = PlayerArgument(id)
    fun orionPlayer(id: String): OrionPlayerArgument = OrionPlayerArgument(id)
}
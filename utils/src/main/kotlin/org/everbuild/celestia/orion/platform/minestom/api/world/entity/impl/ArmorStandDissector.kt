package org.everbuild.celestia.orion.platform.minestom.api.world.entity.impl

import net.kyori.adventure.nbt.BinaryTagTypes
import net.kyori.adventure.nbt.CompoundBinaryTag
import net.kyori.adventure.nbt.FloatBinaryTag
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minestom.server.component.DataComponents
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.metadata.other.ArmorStandMeta
import net.minestom.server.instance.Chunk
import net.minestom.server.item.ItemStack
import org.everbuild.celestia.orion.platform.minestom.api.world.entity.EntityDissector

object ArmorStandDissector : EntityDissector {
    override fun dissect(chunk: Chunk, entity: CompoundBinaryTag) {
        val pos = requireCoordinates(entity)
        val instance = chunk.instance

        val ast = LivingEntity(EntityType.ARMOR_STAND)
        val astMeta = ast.entityMeta as ArmorStandMeta

        if (entity.getString("CustomName").isNotEmpty()) {
            ast[DataComponents.CUSTOM_NAME] = GsonComponentSerializer.gson().deserialize(entity.getString("CustomName"))
            ast.isCustomNameVisible = entity.getBoolean("CustomNameVisible")
        }

        astMeta.isHasNoGravity = true

        if (entity.get("Pose") != null) {
            val pose = entity.getCompound("Pose")
            resolvePose(pose, "Head") { astMeta.headRotation = it }
            resolvePose(pose, "Body") { astMeta.bodyRotation = it }
            resolvePose(pose, "LeftArm") { astMeta.leftArmRotation = it }
            resolvePose(pose, "RightArm") { astMeta.rightArmRotation = it }
            resolvePose(pose, "LeftLeg") { astMeta.leftLegRotation = it }
            resolvePose(pose, "RightLeg") { astMeta.rightLegRotation = it }
        }

        astMeta.isInvisible = entity.getBoolean("Invisible")
        astMeta.isHasNoBasePlate = entity.getBoolean("NoBasePlate")
        astMeta.isHasArms = entity.getBoolean("ShowArms")
        astMeta.isSmall = entity.getBoolean("Small")

        val armorItems = entity.getList("ArmorItems", BinaryTagTypes.COMPOUND)
        val handItems = entity.getList("HandItems", BinaryTagTypes.COMPOUND)

        if (armorItems.size() != 0) {
            ast.boots = ItemStack.fromItemNBT(armorItems[0] as CompoundBinaryTag)
            ast.leggings = ItemStack.fromItemNBT(armorItems[1] as CompoundBinaryTag)
            ast.chestplate = ItemStack.fromItemNBT(armorItems[2] as CompoundBinaryTag)
            ast.helmet = ItemStack.fromItemNBT(armorItems[3] as CompoundBinaryTag)
        }

        if (handItems.size() != 0) {
            ast.itemInMainHand = ItemStack.fromItemNBT(handItems[0] as CompoundBinaryTag)
            ast.itemInOffHand = ItemStack.fromItemNBT(handItems[1] as CompoundBinaryTag)
        }

        ast.setInstance(instance, pos)
    }

    private fun resolvePose(pose: CompoundBinaryTag, key: String, callback: (Vec) -> Unit) {
        if (pose.get(key) == null) return
        val head = pose.getList(key, BinaryTagTypes.FLOAT)
        val vec = Vec(
            (head[0] as FloatBinaryTag).value().toDouble(),
            (head[1] as FloatBinaryTag).value().toDouble(),
            (head[2] as FloatBinaryTag).value().toDouble()
        )

        callback(vec)
    }
}
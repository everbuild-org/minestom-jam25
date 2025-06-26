package org.everbuild.asorda.resources.data

import org.everbuild.asorda.resources.data.api.ContentList
import org.everbuild.asorda.resources.data.api.addContent
import org.everbuild.asorda.resources.data.api.minecraftKey
import org.everbuild.asorda.resources.data.api.resource
import org.everbuild.asorda.resources.data.blocks.ExampleMachineResource
import org.everbuild.asorda.resources.data.blocks.MysteryBlockResource
import team.unnamed.creative.ResourcePack

object ResourcePackBlockList : ContentList("blocks") {
    init {
        include(MysteryBlockResource)
        include(ExampleMachineResource)
    }
}

suspend fun addBlocks(pack: ResourcePack) {
    addContent(pack, ResourcePackBlockList)

    for (file in listOf(
        "trial_spawner_bottom.png",
        "trial_spawner_side_active.png",
        "trial_spawner_side_active_ominous.png",
        "trial_spawner_side_inactive.png",
        "trial_spawner_side_inactive_ominous.png",
        "trial_spawner_top_active.png",
        "trial_spawner_top_active_ominous.png",
        "trial_spawner_top_ejecting_reward.png",
        "trial_spawner_top_ejecting_reward_ominous.png",
        "trial_spawner_top_inactive.png",
        "trial_spawner_top_inactive_ominous.png"
    )) {
        pack.texture(minecraftKey("block/$file"), resource("minecraft/textures/block/blank.png"))
    }
}

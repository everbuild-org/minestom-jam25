package org.everbuild.jam25.block.impl.crafting

import net.worldseed.multipart.GenericModelImpl

class CrafterModel(val modelId: String?) : GenericModelImpl(){
    override fun getId() = modelId
}
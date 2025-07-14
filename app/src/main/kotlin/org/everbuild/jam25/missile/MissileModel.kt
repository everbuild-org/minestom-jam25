package org.everbuild.jam25.missile

import net.worldseed.multipart.GenericModelImpl

class MissileModel(val num: Int) : GenericModelImpl() {
    override fun getId(): String = "missile_$num.bbmodel"
}
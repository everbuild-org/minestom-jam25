package org.everbuild.celestia.orion.platform.minestom.prism

interface PrismProfileLoaderCallback {
    fun syncLoadedProfiles(profiles: List<Int>)
    fun getExtraForceloadedProfiles(): String = ""
}
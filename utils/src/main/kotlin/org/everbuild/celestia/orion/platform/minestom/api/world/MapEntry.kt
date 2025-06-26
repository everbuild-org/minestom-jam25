package org.everbuild.celestia.orion.platform.minestom.api.world

class MapEntry<out K, out V>(
    override val key: K,
    override val value: V
) : Map.Entry<K, V>
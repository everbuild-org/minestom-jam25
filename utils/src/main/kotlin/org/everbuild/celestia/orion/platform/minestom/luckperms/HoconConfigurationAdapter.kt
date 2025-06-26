package org.everbuild.celestia.orion.platform.minestom.luckperms

import me.lucko.luckperms.common.config.generic.adapter.ConfigurateConfigAdapter
import me.lucko.luckperms.common.config.generic.adapter.ConfigurationAdapter
import me.lucko.luckperms.common.plugin.LuckPermsPlugin
import me.lucko.luckperms.minestom.LPMinestomPlugin
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import java.nio.file.Path

class HoconConfigurationAdapter(plugin: LuckPermsPlugin) :
    ConfigurateConfigAdapter(plugin, (plugin as LPMinestomPlugin).resolveConfig("luckperms.conf")),
    ConfigurationAdapter {
    override fun createLoader(path: Path): ConfigurationLoader<out ConfigurationNode> {
        return HoconConfigurationLoader.builder().setPath(path).build()
    }
}
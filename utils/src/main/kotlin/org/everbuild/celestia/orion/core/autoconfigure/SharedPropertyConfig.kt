package org.everbuild.celestia.orion.core.autoconfigure

import org.everbuild.celestia.orion.core.configuration.ConfigurationNamespace

object SharedPropertyConfig : ConfigurationNamespace("orion") {
    private val defaultEnvironment = loadDefaultEnvironment()
    val tolgeeHost by string("tolgee.host", "https://translate.everbuild.org")
    val tolgeeProjectKey by string("tolgee.key", "api-key")
    val globalPrefix by string("global.prefix", "<hover:show_text:'{authenticity}'><bold><gradient:gold:red>Asorda</gradient></bold></hover> <dark_gray>» <gray>")

    val bcpEnabled by boolean("bcp.enabled", false)

    val argosEnabled by boolean("argos.enabled", false)
    val argosHost by string("argos.host", "argos")
    val argosPort by integer("argos.port", 3377)

    val redisHost by string("redis.uri", "redis://localhost:6379")

    val mysqlHost by string("mysql.host", defaultEnvironment.mariadbHost)
    val mysqlUsername by string("mysql.username", defaultEnvironment.mariadbUsername)
    val mysqlPassword by string("mysql.password", defaultEnvironment.mariadbPassword)
    val mysqlPort by integer("mysql.port", defaultEnvironment.mariadbPort)
    val mysqlDatabaseName by string("mysql.database", defaultEnvironment.mariadbDatabase)

    val resourcePack by string("packs.resource", "https://s3.everbuild.org/resources/resource_pack_main.zip")
    val experiencePack by string("packs.experience", "https://s3.everbuild.org/resources/experience_pack_main.zip")

    val amigoServer by string("amigo.host", "amigo:8080")
}
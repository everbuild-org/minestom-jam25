package org.everbuild.celestia.orion.core.database

import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.filter
import org.ktorm.entity.toList
import org.slf4j.LoggerFactory
import java.sql.Connection

object Migrator {
    private val logger = LoggerFactory.getLogger(Migrator::class.java)

    fun assertMigrationTable() {
        DataSource.database.useConnection { conn ->
            conn.prepareStatement(
                """
                CREATE TABLE IF NOT EXISTS orion_migrations (
                    id      INTEGER PRIMARY KEY AUTOINCREMENT,
                    plugin  VARCHAR(512) NOT NULL,
                    name    VARCHAR(512) NOT NULL
                )
            """.trimIndent()
            ).execute()
        }
    }

    private fun loadMigration(plugin: Class<*>, path: String, migrationName: String): MigrationScript? {
        val bufferedReader = plugin.getResourceAsStream("/$path/$migrationName.sql")?.bufferedReader()
            ?: return null
        val text = bufferedReader.use { it.readText() }
        val sqlScripts = text.split(";")
            .filter { it.trim().isNotBlank() }

        return MigrationScript(migrationName, sqlScripts)
    }

    fun migrate(plugin: Class<*>, path: String, migrationNames: List<String>) {
        var pluginId = plugin.name
        if (pluginId.length > 512) {
            // last 512 chars
            val longPluginIdOverLength = pluginId.length - 513
            pluginId = pluginId.substring(longPluginIdOverLength)
        }

        val migrations = database.migrations
            .filter { it.plugin eq pluginId }
            .toList()
            .filter { migrationNames.contains(it.name) }

        val migrationsToPerform = migrationNames.filter { name -> migrations.none { it.name == name } }

        logger.info("Migrator summary: Applied ${migrationNames.size-migrationsToPerform.size}/${migrationNames.size} Migrations for ${plugin.simpleName}")
        if (migrationsToPerform.isEmpty()) return
        logger.info("Loading missing migrations...")

        val migrationScriptCache = mutableListOf<MigrationScript>()

        migrationsToPerform.forEach { migration ->
            val migrationScript = loadMigration(plugin, path, migration)
                ?: throw MigrationFailedException(migration, MigrationPhase.GATHER, null)
            migrationScriptCache.add(migrationScript)
        }

        DataSource.database.useConnection { conn ->
            for (migration in migrationScriptCache) {
                try {
                    executeMigration(migration, conn, pluginId)
                    logger.info(" - ${migration.name} executed")
                } catch (e: Exception) {
                    throw MigrationFailedException(migration.name, MigrationPhase.EXECUTE, e)
                }
            }
        }
    }

    private fun executeMigration(
        migration: MigrationScript,
        conn: Connection,
        pluginId: String
    ) {
        for (script in migration.sql) {
            // not safe - not unsafe, but required.
            // we're just executing developer-provided scripts,
            // nothing more, nothing less
            conn.prepareStatement(script).use { it.execute() }
        }

        database.migrations.add(Migration {
            this.plugin = pluginId
            this.name = migration.name
        })
    }

    data class MigrationScript(val name: String, val sql: List<String>)

    enum class MigrationPhase {
        GATHER, EXECUTE
    }

    class MigrationFailedException(name: String, phase: MigrationPhase, exception: Exception?) :
        Exception("Migration failed: $name could not be processed in the $phase phase", exception)
}
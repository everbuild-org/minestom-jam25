package org.everbuild.celestia.orion.core.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.everbuild.celestia.orion.core.autoconfigure.SharedPropertyConfig
import org.ktorm.database.Database

object DataSource {
    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:mysql://${SharedPropertyConfig.mysqlHost}:${SharedPropertyConfig.mysqlPort}/${SharedPropertyConfig.mysqlDatabaseName}"
        username = SharedPropertyConfig.mysqlUsername
        password = SharedPropertyConfig.mysqlPassword
        driverClassName = "com.mysql.cj.jdbc.Driver"

        addDataSourceProperty("cachePrepStmts", "true")
        addDataSourceProperty("prepStmtCacheSize", "250")
        addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
    }

    private val dataSource = HikariDataSource(hikariConfig)

    val connection get() = dataSource.connection
    val database = Database.connect(dataSource)
}

val database get() = DataSource.database
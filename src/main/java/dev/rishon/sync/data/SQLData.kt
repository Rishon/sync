package dev.rishon.sync.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.rishon.sync.enums.Colors
import dev.rishon.sync.handler.FileHandler
import dev.rishon.sync.handler.MainHandler
import dev.rishon.sync.utils.ColorUtil
import dev.rishon.sync.utils.SchedulerUtil
import org.bukkit.configuration.file.FileConfiguration
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import java.util.concurrent.CompletableFuture


class SQLData(val handler: MainHandler) : IDataModule {

    // Hikari Config
    private val hikariConfig = HikariConfig()
    private var hikariDataSource: HikariDataSource? = null

    // Tables
    private val playersTable = "sync_players"

    override fun init() {
        val config: FileConfiguration? = FileHandler.handler.config
        val path = "mysql."
        val host = config?.getString(path + "host") ?: throw RuntimeException("MySQL host is null")
        val port = config.getInt(path + "port")
        val database = config.getString(path + "database") ?: throw RuntimeException("MySQL database is null")
        val username = config.getString(path + "username") ?: throw RuntimeException("MySQL username is null")
        val password = config.getString(path + "password") ?: throw RuntimeException("MySQL password is null")

        this.hikariConfig.jdbcUrl =
            "jdbc:mysql://$host:$port/$database?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf8&useJDBCCompliantTimezoneShift=true&serverTimezone=UTC"
        this.hikariConfig.username = username
        this.hikariConfig.password = password
        this.hikariConfig.maximumPoolSize = 10
        this.hikariConfig.connectionTimeout = 30000

        this.hikariDataSource = HikariDataSource(hikariConfig)

        createTables(database)
    }

    override fun end() {
        try {
            if (this.hikariDataSource != null) this.hikariDataSource?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    // Create tables
    private fun createTables(database: String) {
        try {
            getConnection().use { connection ->
                val statement = connection.createStatement()
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS $playersTable (uuid VARCHAR(36) NOT NULL, `json` MEDIUMTEXT, PRIMARY KEY (uuid))")
                statement.executeUpdate("ALTER DATABASE $database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun loadUser(uuid: UUID) {
        val redisData = this.handler.redisData ?: return kickPlayer(uuid)
        doesUserExistInDatabaseAsync(uuid).thenAcceptAsync { exists: Boolean ->
            if (exists) {
                getPlayerDataAsync(uuid).thenAcceptAsync { playerData: PlayerData? ->
                    if (playerData != null) {
                        redisData.setPlayerData(uuid, playerData)
                    } else {
                        kickPlayer(uuid)
                    }
                }
            } else {
                handlePlayerNotInDatabase(uuid)
            }
        }
    }

    private fun handlePlayerNotInDatabase(uuid: UUID) {
        SchedulerUtil.runTaskAsync {
            val redisData = this.handler.redisData ?: return@runTaskAsync kickPlayer(uuid)
            var playerData = PlayerData()
            playerData = playerData.apply { this.uuid = uuid }
            try {
                getConnection().use { connection ->
                    connection.prepareStatement("INSERT INTO $playersTable (UUID, JSON) VALUES (?, ?)")
                        .use { statement ->
                            statement.setString(1, uuid.toString())
                            statement.setString(2, playerData.toString())
                            statement.executeUpdate()
                            redisData.setPlayerData(uuid, playerData)
                        }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }

    private fun doesUserExistInDatabaseAsync(uuid: UUID): CompletableFuture<Boolean> {
        try {
            getConnection().use { connection ->
                connection.prepareStatement("SELECT UUID FROM $playersTable WHERE UUID=?").use { statement ->
                    statement.setString(1, uuid.toString())
                    statement.executeQuery().use { resultSet ->
                        return CompletableFuture.completedFuture(resultSet.next())
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            return CompletableFuture.completedFuture(false)
        }
    }

    private fun getPlayerDataAsync(uuid: UUID): CompletableFuture<PlayerData> {
        return CompletableFuture.supplyAsync {
            try {
                getConnection().use { connection ->
                    connection.prepareStatement("SELECT JSON FROM $playersTable WHERE UUID=?").use { statement ->
                        statement.setString(1, uuid.toString())
                        statement.executeQuery().use { resultSet ->
                            if (!resultSet.next()) return@supplyAsync null
                            return@supplyAsync PlayerData.fromJson(resultSet.getString("JSON"))
                        }
                    }
                }
            } catch (e: SQLException) {
                e.printStackTrace()
                return@supplyAsync null
            }
        }
    }

    fun saveUser(uuid: UUID, playerData: PlayerData) {
        try {
            getConnection().use { connection ->
                connection.prepareStatement("SELECT * FROM $playersTable WHERE UUID='$uuid';").executeQuery()
                    .use { resultSet ->
                        if (resultSet.next()) {
                            connection.createStatement()
                                .executeUpdate("UPDATE $playersTable SET JSON='$playerData' WHERE UUID='$uuid';")
                        }
                    }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    private fun kickPlayer(uuid: UUID) {
        SchedulerUtil.runTaskSync {
            val player = handler.instance.server.getPlayer(uuid) ?: return@runTaskSync
            player.kick(ColorUtil.translate("Couldn't load player data.", Colors.DENIED))
        }
    }

    // Get connection
    private fun getConnection(): Connection {
        return hikariDataSource!!.connection
    }

}
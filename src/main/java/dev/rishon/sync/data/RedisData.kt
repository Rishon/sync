package dev.rishon.sync.data

import dev.rishon.sync.Sync
import dev.rishon.sync.api.SyncAPI
import dev.rishon.sync.handler.FileHandler
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.utils.InventorySerialization
import dev.rishon.sync.utils.LoggerUtil
import dev.rishon.sync.utils.SchedulerUtil
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.params.ScanParams
import java.util.*
import java.util.concurrent.CompletableFuture


class RedisData : IDataModule {

    // Identifer
    private val playerIdentifier: String = "sync_player_"
    private val serverIdentifier: String = "sync_server_${Sync.instance.instanceID}"

    // Redis Pool
    var jedisPool: JedisPool? = null
    private var jedisPoolConfig: JedisPoolConfig? = null

    // JedisManager
    private var jedisManager: JedisManager? = null

    override fun init() {
        instance = this
        val path = "redis."
        try {
            this.jedisPoolConfig = JedisPoolConfig()

            this.jedisPoolConfig!!.maxTotal = 200
            this.jedisPoolConfig!!.maxIdle = 100
            this.jedisPoolConfig!!.minIdle = 50

            // FileHandler config
            val config = FileHandler.handler.config ?: throw RuntimeException("FileHandler config is null")

            this.jedisPool = JedisPool(
                this.jedisPoolConfig, config.getString(path + "host"), Math.toIntExact(config.getLong(path + "port"))
            )

            jedisPool!!.resource.ping()
            LoggerUtil.info("Successfully connected to Redis!")

            // Cache Server Data
            val serverData = ServerData()
            serverData.instanceID = Sync.instance.instanceID
            this.setServerDataAsync(serverData)

            // Register API
            SyncAPI()

            // Initialize JedisManager
            this.jedisManager = JedisManager(this)
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(e)
        }
    }

    override fun end() {
        try {
            this.jedisPool!!.resource.use { jedis ->
                val scanParams = ScanParams().match(this.serverIdentifier)
                var cursor = "0"
                do {
                    val scanResult = jedis.scan(cursor, scanParams)
                    val keys = scanResult.result
                    for (key in keys) {
                        println("Deleting key: $key")
                        jedis.del(key)
                    }
                    cursor = scanResult.cursor
                } while (cursor != "0")
            }
        } finally {
            this.jedisManager?.end()
            this.jedisPool!!.close()
        }
    }

    // Cache Server Data
    fun setServerDataAsync(serverData: ServerData) {
        SchedulerUtil.runTaskSync {
            jedisPool!!.resource.use { jedis ->
                val json = serverData.toString()
                jedis[serverIdentifier] = json
            }
        }
    }

    fun getServerDataAsync(): ServerData {
        val future: CompletableFuture<ServerData> = CompletableFuture.supplyAsync {
            jedisPool!!.resource.use { jedis ->
                val json = jedis[serverIdentifier] ?: return@supplyAsync ServerData()
                return@supplyAsync ServerData.fromJson(json)
            }
        }
        return future.join()
    }

    // Cache Player Data
    fun setPlayerDataAsync(uuid: UUID, playerData: PlayerData) {
        SchedulerUtil.runTaskAsync {
            jedisPool!!.resource.use { jedis ->
                val json = playerData.toString()
                jedis["$playerIdentifier$uuid"] = json
            }
        }
    }

    fun getPlayerDataAsync(uuid: UUID): PlayerData {
        val future: CompletableFuture<PlayerData> = CompletableFuture.supplyAsync {
            jedisPool!!.resource.use { jedis ->
                val json = jedis["$playerIdentifier$uuid"] ?: return@supplyAsync null
                return@supplyAsync PlayerData.fromJson(json)
            }
        }
        return future.join()
    }

    fun getPlayerDataSync(uuid: UUID): PlayerData? {
        jedisPool!!.resource.use { jedis ->
            val json = jedis["$playerIdentifier$uuid"] ?: return PlayerData()
            return PlayerData.fromJson(json)
        }
    }

    fun removePlayerDataAsync(uuid: UUID) {
        SchedulerUtil.runTaskAsync {
            jedisPool!!.resource.use { jedis ->
                jedis.del("$playerIdentifier$uuid")
            }
        }
    }

    fun doesPlayerExistAsync(uuid: UUID): Boolean {
        val future = CompletableFuture.supplyAsync {
            jedisPool!!.resource.use { jedis ->
                return@supplyAsync jedis.exists("$playerIdentifier$uuid")
            }
        }
        return future.join()
    }

    fun loadPlayerInfo(player: Player, playerData: PlayerData) {
        // Load player inventory
        playerData.loadInventory(player, this)
        // Load player location
        val locationMap = playerData.location
        if (locationMap.isNotEmpty()) {
            val location = Location.deserialize(playerData.location)
            player.teleportAsync(location)
        }
        // Load player exp points
        player.exp = playerData.expPoints
        // Load player exp level
        player.level = playerData.expLevel
        // Load player health
        player.health = playerData.health
        // Load player max health
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = playerData.maxHealth
        // Load player hunger
        player.foodLevel = playerData.hunger
        // Load player gamemode
        player.gameMode = playerData.gamemode
        // Load player effects
        playerData.potionEffects.forEach { player.addPotionEffect(PotionEffect(it)) }
    }

    fun savePlayerInfo(player: Player, playerData: PlayerData) {
        // Save current player inventory
        playerData.inventory = InventorySerialization.toBase64(player.inventory)
        // Save player location
        playerData.location = player.location.serialize()
        // Save player exp points
        playerData.expPoints = player.exp
        // Save player exp level
        playerData.expLevel = player.level
        // Save player health
        playerData.health = player.health
        // Save player max health
        playerData.maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
        // Save player hunger
        playerData.hunger = player.foodLevel
        // Save player gamemode
        playerData.gamemode = player.gameMode
        // Save player effects
        val effectsCollection: MutableCollection<MutableMap<String, Any>> = mutableListOf()
        player.activePotionEffects.forEach { effectsCollection.add(it.serialize()) }
        playerData.potionEffects = effectsCollection.toList()
    }

    companion object {
        // Static-Access
        lateinit var instance: RedisData
    }
}
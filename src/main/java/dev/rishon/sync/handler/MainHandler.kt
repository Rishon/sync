package dev.rishon.sync.handler

import dev.rishon.sync.Sync
import dev.rishon.sync.api.SyncAPI
import dev.rishon.sync.command.SyncCommand
import dev.rishon.sync.command.TransferCommand
import dev.rishon.sync.command.WhereAmICommand
import dev.rishon.sync.data.CacheData
import dev.rishon.sync.data.IDataModule
import dev.rishon.sync.data.RedisData
import dev.rishon.sync.data.SQLData
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packet.ConnectPacket
import dev.rishon.sync.jedis.packet.DisconnectPacket
import dev.rishon.sync.jedis.packet.LogPacket
import dev.rishon.sync.listener.*
import dev.rishon.sync.utils.Utils

class MainHandler(val instance: Sync) : IHandler {

    // Data Modules
    private val dataModules: MutableList<IDataModule> = mutableListOf()
    var sqlData: SQLData? = null
    var redisData: RedisData? = null
    var cacheData: CacheData? = null

    // Hooks
    var placeholderAPI: Boolean? = null

    override fun init() {
        handler = this

        // Initialize hooks
        this.placeholderAPI = this.instance.server.pluginManager.getPlugin("PlaceholderAPI") != null

        // Register dataModules
        this.sqlData = SQLData(this)
        this.redisData = RedisData()
        this.cacheData = CacheData()
        this.dataModules.add(this.sqlData!!)
        this.dataModules.add(this.redisData!!)
        this.dataModules.add(this.cacheData!!)
        this.dataModules.forEach { it.init() }

        // Register
        registerListeners()
        registerCommands()

        // Load online players
        loadOnlinePlayers()
    }

    override fun end() {
        // Save online players
        saveOnlinePlayers()
        // Notify other instances
        JedisManager.instance.sendPacket(
            LogPacket(
                "Instance ${
                    SyncAPI.getAPI().getFormattedInstanceID()
                } has been disconnected"
            )
        )
        // End dataModules
        this.dataModules.forEach { it.end() }
    }

    private fun registerListeners() {
        val pm = instance.server.pluginManager
        pm.registerEvents(Connections(this), instance)
        pm.registerEvents(InventoryEvents(this.redisData!!), instance)
        pm.registerEvents(ServerPing(this), instance)
        pm.registerEvents(AsyncChat(), instance)
        pm.registerEvents(WorldEvents(), instance)
        pm.registerEvents(AnimationEvent(), instance)
        pm.registerEvents(EntityDamage(this), instance)
    }

    private fun registerCommands() {
        instance.getCommand("sync")?.setExecutor(SyncCommand(this))
        instance.getCommand("whereami")?.setExecutor(WhereAmICommand(this))

        instance.getCommand("transfer")?.setExecutor(TransferCommand(this))
        instance.getCommand("transfer")?.tabCompleter = TransferCommand(this)
    }

    private fun loadOnlinePlayers() {
        this.instance.server.onlinePlayers.forEach { player ->
            val uuid = player.uniqueId
            this.sqlData?.loadUser(uuid)
            val playerData = this.redisData?.getPlayerData(uuid) ?: return
            redisData?.loadPlayerInfo(player, playerData)
            // Add player to online players
            JedisManager.instance.sendPacket(
                ConnectPacket(
                    player.location.serialize(), player.name, uuid, Utils.getSkin(player)
                )
            )
        }
    }

    private fun saveOnlinePlayers() {
        val redisData = this.redisData
        this.instance.server.onlinePlayers.forEach { player ->
            val uuid = player.uniqueId
            val playerData = redisData?.getPlayerData(uuid)
            if (playerData == null) return@forEach

            // Disconnect player
            JedisManager.instance.sendPacket(DisconnectPacket(uuid))

            redisData.savePlayerInfo(player, playerData)
            this.sqlData?.saveUser(uuid, playerData)
        }
    }

    companion object {
        // Static-Access
        lateinit var handler: MainHandler
    }
}
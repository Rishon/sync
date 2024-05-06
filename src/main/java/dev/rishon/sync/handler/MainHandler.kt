package dev.rishon.sync.handler

import dev.rishon.sync.Sync
import dev.rishon.sync.commands.SyncCommand
import dev.rishon.sync.commands.WhereAmICommand
import dev.rishon.sync.data.CacheData
import dev.rishon.sync.data.IDataModule
import dev.rishon.sync.data.RedisData
import dev.rishon.sync.data.SQLData
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packets.ConnectPacket
import dev.rishon.sync.listeners.*
import dev.rishon.sync.tasks.InstanceTask
import dev.rishon.sync.utils.SchedulerUtil
import dev.rishon.sync.utils.Utils

class MainHandler(val instance: Sync) : IHandler {

    // Data Modules
    private val dataModules: MutableList<IDataModule> = mutableListOf()
    var sqlData: SQLData? = null
    var redisData: RedisData? = null
    var cacheData: CacheData? = null

    override fun init() {
        handler = this
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

        // Load tasks
        loadTasks()

        // Load online players
        loadOnlinePlayers()
    }

    override fun end() {
        // Save online players
        saveOnlinePlayers()
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
        pm.registerEvents(ServerTick(), instance)
    }

    private fun registerCommands() {
        instance.getCommand("sync")?.setExecutor(SyncCommand(this))
        instance.getCommand("whereami")?.setExecutor(WhereAmICommand(this))
    }

    private fun loadTasks() {
        SchedulerUtil.runTaskTimerAsync(InstanceTask(this), 20)
    }

    private fun loadOnlinePlayers() {
        this.instance.server.onlinePlayers.forEach { player ->
            val uuid = player.uniqueId
            this.sqlData?.loadUser(uuid)
            val playerData = this.redisData?.getPlayerDataAsync(uuid) ?: return
            redisData?.loadPlayerInfo(player, playerData)
            // Add player to online players
            JedisManager.instance.sendPacket(
                ConnectPacket(
                    player.location.serialize(),
                    player.name,
                    uuid,
                    Utils.getSkin(player)
                )
            )
        }
    }

    private fun saveOnlinePlayers() {
        val redisData = this.redisData
        this.instance.server.onlinePlayers.forEach { player ->
            val uuid = player.uniqueId
            val playerData = redisData?.getPlayerDataSync(uuid)
            if (playerData == null) {
                player.sendMessage("Player data is null")
                return
            }
            redisData.savePlayerInfo(player, playerData)
            this.sqlData?.saveUser(uuid, playerData)
        }
    }

    companion object {
        // Static-Access
        lateinit var handler: MainHandler
    }
}
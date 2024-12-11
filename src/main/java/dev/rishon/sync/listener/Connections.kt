package dev.rishon.sync.listener

import dev.rishon.sync.api.SyncAPI
import dev.rishon.sync.handler.FileHandler
import dev.rishon.sync.handler.MainHandler
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packet.ConnectPacket
import dev.rishon.sync.jedis.packet.DisconnectPacket
import dev.rishon.sync.utils.ColorUtil
import dev.rishon.sync.utils.SchedulerUtil
import dev.rishon.sync.utils.Utils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID

class Connections(private val handler: MainHandler) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onAsyncPreLogin(event: AsyncPlayerPreLoginEvent) {
        val result = event.loginResult
        if (result != AsyncPlayerPreLoginEvent.Result.ALLOWED) return
        val uuid: UUID = event.uniqueId
        this.handler.sqlData?.loadUser(uuid)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage(null)

        val player = event.player
        val uuid = player.uniqueId

        val fileHandler = FileHandler.handler
        val redisData = this.handler.redisData

        // Load player data
        val playerData = this.handler.redisData?.getPlayerData(uuid)
            ?: return player.kick(ColorUtil.translate("An error occurred while loading your data. Please try again."))

        redisData?.loadPlayerInfo(player, playerData)

        // Add player to online players
        JedisManager.instance.sendPacket(
            ConnectPacket(
                playerData.location, player.name, uuid, Utils.getSkin(player)
            )
        )

        // Broadcast join message
        if (fileHandler.joinMessage.first) SyncAPI.getAPI()
            .broadcastMessage(ColorUtil.translate(fileHandler.joinMessage.second))
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerDisconnect(event: PlayerQuitEvent) {
        event.quitMessage(null)

        val player = event.player
        val uuid = player.uniqueId

        val fileHandler = FileHandler.handler
        val redisData = this.handler.redisData
        val playerData = redisData?.getPlayerData(uuid) ?: return

        // Save player data
        redisData.savePlayerInfo(player, playerData)

        if (player.server.isStopping) {
            SchedulerUtil.runTaskSync { this.handler.sqlData?.saveUser(uuid, playerData) }
        } else {
            SchedulerUtil.runTaskAsync { this.handler.sqlData?.saveUser(uuid, playerData) }
        }

        // Remove player data from cache
        redisData.removePlayerData(uuid)

        // Remove player from online players
        JedisManager.instance.sendPacket(DisconnectPacket(uuid))

        // Broadcast quit message
        if (fileHandler.quitMessage.first) SyncAPI.getAPI()
            .broadcastMessage(ColorUtil.translate(fileHandler.quitMessage.second))
    }

}
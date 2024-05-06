package dev.rishon.sync.listeners

import dev.rishon.sync.handler.MainHandler
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packets.ConnectPacket
import dev.rishon.sync.jedis.packets.DisconnectPacket
import dev.rishon.sync.utils.ColorUtil
import dev.rishon.sync.utils.Utils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

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
        val player = event.player
        val uuid = player.uniqueId
        val redisData = this.handler.redisData

        // Load player data
        val playerData = this.handler.redisData?.getPlayerDataAsync(uuid)
            ?: return player.kick(ColorUtil.translate("An error occurred while loading your data. Please try again."))
        redisData?.loadPlayerInfo(player, playerData)

        // Add player to online players
        JedisManager.instance.sendPacket(
            ConnectPacket(
                playerData.location, player.name, uuid, Utils.getSkin(player)
            )
        )
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerDisconnect(event: PlayerQuitEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val redisData = this.handler.redisData
        val playerData = redisData?.getPlayerDataAsync(uuid) ?: return

        // Save player data
        redisData.savePlayerInfo(player, playerData)
        playerData.let { this.handler.sqlData?.saveUser(uuid, it) }

        // Remove player from online players
        JedisManager.instance.sendPacket(DisconnectPacket(uuid))
    }

}
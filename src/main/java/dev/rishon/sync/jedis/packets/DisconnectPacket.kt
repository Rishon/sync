package dev.rishon.sync.jedis.packets

import dev.rishon.sync.data.CacheData
import dev.rishon.sync.data.RedisData
import dev.rishon.sync.nms.ClientRemovePlayerPacket
import dev.rishon.sync.utils.LoggerUtil
import org.bukkit.Bukkit
import java.util.*

class DisconnectPacket(private val playerUUID: UUID) : IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received packet for $playerUUID")
        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val entityId = cacheData.fakePlayers[playerUUID]?.id

        onlinePlayers.forEach { player ->
            if (playerUUID == player.uniqueId) return@forEach
            ClientRemovePlayerPacket.sendPacket(player, entityId!!)
        }

        // Remove player from local cache
        cacheData.fakePlayers.remove(playerUUID)

        // Remove player from online players
        val serverData = RedisData.instance.getServerData() ?: return
        serverData.onlinePlayers.remove(playerUUID)
        RedisData.instance.setServerData(serverData)
    }
}

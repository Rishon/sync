package dev.rishon.sync.jedis.packets

import dev.rishon.sync.data.RedisData
import dev.rishon.sync.nms.ClientRemovePlayerPacket
import dev.rishon.sync.utils.LoggerUtil
import org.bukkit.Bukkit
import java.util.*

class DisconnectPacket(private val playerUUID: UUID) : IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received packet for $playerUUID")

        // Remove player from online players
        val serverData = RedisData.instance.getServerDataAsync()
        serverData.onlinePlayers.remove(playerUUID)
        RedisData.instance.setServerDataAsync(serverData)
    }
}

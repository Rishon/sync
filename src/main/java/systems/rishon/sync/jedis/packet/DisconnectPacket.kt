package systems.rishon.sync.jedis.packet

import org.bukkit.Bukkit
import systems.rishon.sync.data.CacheData
import systems.rishon.sync.data.RedisData
import systems.rishon.sync.packet.ClientRemovePlayerPacket
import systems.rishon.sync.utils.LoggerUtil
import java.util.*

class DisconnectPacket(private val playerUUID: UUID) : IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received disconnect packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID] ?: return

        onlinePlayers.forEach { player ->
            if (playerUUID == player.uniqueId) return@forEach
            ClientRemovePlayerPacket.sendPacket(player, fakePlayer.second.uuid, fakePlayer.second.id)
        }

        // Remove player from local cache
        cacheData.fakePlayers.remove(playerUUID)

        // Remove player from online players
        val serverData = RedisData.instance.getServerData() ?: return
        serverData.onlinePlayers.remove(playerUUID)
        RedisData.instance.setServerData(serverData)
    }
}

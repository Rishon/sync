package dev.rishon.sync.jedis.packets

import dev.rishon.sync.data.CacheData
import dev.rishon.sync.nms.ClientPlayerMovePacket
import org.bukkit.Bukkit
import java.util.*

class MovePacket(
    private val playerUUID: UUID,
    private val location: MutableMap<String, Any>,
) : IPacket {

    override fun onReceive() {
        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID] ?: return

        onlinePlayers.forEach { player ->
            if (player.uniqueId == playerUUID) return@forEach
            ClientPlayerMovePacket.sendPacket(player, fakePlayer.second, location)
        }
    }
}

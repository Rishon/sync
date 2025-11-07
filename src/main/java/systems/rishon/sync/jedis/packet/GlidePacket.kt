package systems.rishon.sync.jedis.packet

import org.bukkit.Bukkit
import systems.rishon.sync.data.CacheData
import systems.rishon.sync.packet.ClientGlidePlayerPacket
import systems.rishon.sync.utils.LoggerUtil
import java.util.*

class GlidePacket(private val playerUUID: UUID, private val isGliding: Boolean) : IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received glide packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID] ?: return

        onlinePlayers.forEach { player ->
            if (player.uniqueId == playerUUID) return@forEach
            ClientGlidePlayerPacket.sendPacket(player, fakePlayer.second, isGliding)
        }
    }
}

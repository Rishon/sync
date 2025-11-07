package systems.rishon.sync.jedis.packet

import org.bukkit.Bukkit
import systems.rishon.sync.data.CacheData
import systems.rishon.sync.enums.Animations
import systems.rishon.sync.packet.ClientAnimatePlayerPacket
import systems.rishon.sync.utils.LoggerUtil
import java.util.*

class AnimationPacket(private val playerUUID: UUID, private val animation: Animations) : IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received punch packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID] ?: return

        onlinePlayers.forEach { player ->
            if (player.uniqueId == playerUUID) return@forEach
            ClientAnimatePlayerPacket.sendPacket(player, fakePlayer.second, animation)
        }
    }
}

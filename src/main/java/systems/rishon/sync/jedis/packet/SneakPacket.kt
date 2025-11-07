package systems.rishon.sync.jedis.packet

import org.bukkit.Bukkit
import systems.rishon.sync.data.CacheData
import systems.rishon.sync.packet.ClientSneakPlayerPacket
import systems.rishon.sync.utils.LoggerUtil
import java.util.*

class SneakPacket(private val playerUUID: UUID, private val isSneaking: Boolean, private val isFlying: Boolean) :
    IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received sneak packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID] ?: return

        onlinePlayers.forEach { player ->
            if (player.uniqueId == playerUUID) return@forEach
            ClientSneakPlayerPacket.sendPacket(player, fakePlayer.second, isSneaking, isFlying)
        }
    }
}

package systems.rishon.sync.jedis.packet

import org.bukkit.Bukkit
import systems.rishon.sync.data.CacheData
import systems.rishon.sync.packet.ClientSwimPlayerPacket
import systems.rishon.sync.utils.LoggerUtil
import java.util.*

class SwimPacket(private val playerUUID: UUID, private val isSwimming: Boolean) : IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received swim packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID] ?: return

        onlinePlayers.forEach { player ->
            if (player.uniqueId == playerUUID) return@forEach
            ClientSwimPlayerPacket.sendPacket(player, fakePlayer.second, isSwimming)
        }
    }
}

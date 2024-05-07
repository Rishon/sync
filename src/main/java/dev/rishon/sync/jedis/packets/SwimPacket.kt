package dev.rishon.sync.jedis.packets

import dev.rishon.sync.data.CacheData
import dev.rishon.sync.nms.ClientSwimPlayerPacket
import dev.rishon.sync.utils.LoggerUtil
import org.bukkit.Bukkit
import java.util.*

class SwimPacket(private val playerUUID: UUID, private val isSwimming: Boolean) : IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received swim packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID]

        onlinePlayers.forEach { player ->
            if (player.uniqueId == playerUUID) return@forEach
            ClientSwimPlayerPacket.sendPacket(player, fakePlayer!!, isSwimming)
        }
    }
}

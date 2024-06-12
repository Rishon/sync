package dev.rishon.sync.jedis.packet

import dev.rishon.sync.data.CacheData
import dev.rishon.sync.packet.ClientSneakPlayerPacket
import dev.rishon.sync.utils.LoggerUtil
import org.bukkit.Bukkit
import java.util.*

class SneakPacket(private val playerUUID: UUID, private val isSneaking: Boolean) : IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received sneak packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID] ?: return

        onlinePlayers.forEach { player ->
            if (player.uniqueId == playerUUID) return@forEach
            ClientSneakPlayerPacket.sendPacket(player, fakePlayer.second, isSneaking)
        }
    }
}

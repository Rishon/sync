package dev.rishon.sync.jedis.packets

import dev.rishon.sync.data.CacheData
import dev.rishon.sync.nms.ClientGlidePlayerPacket
import dev.rishon.sync.utils.LoggerUtil
import org.bukkit.Bukkit
import java.util.*

class GlidePacket(private val playerUUID: UUID, private val isGliding: Boolean) : IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received glide packet for $playerUUID")

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

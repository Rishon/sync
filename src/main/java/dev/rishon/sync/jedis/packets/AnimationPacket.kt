package dev.rishon.sync.jedis.packets

import dev.rishon.sync.data.CacheData
import dev.rishon.sync.enums.Animations
import dev.rishon.sync.nms.ClientAnimatePlayerPacket
import dev.rishon.sync.utils.LoggerUtil
import org.bukkit.Bukkit
import java.util.*

class AnimationPacket(private val playerUUID: UUID, private val animation: Animations) : IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received punch packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID]

        onlinePlayers.forEach { player ->
            if (player.uniqueId == playerUUID) return@forEach
            ClientAnimatePlayerPacket.sendPacket(player, fakePlayer!!, animation)
        }
    }
}

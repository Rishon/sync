package dev.rishon.sync.jedis.packets

import dev.rishon.sync.data.CacheData
import dev.rishon.sync.utils.LoggerUtil
import org.bukkit.Bukkit
import java.util.*

class DamagePacket(private val playerUUID: UUID, private val damage: Double, private val criticalHit: Boolean) :
    IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received damage packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID] ?: return

        onlinePlayers.forEach { player ->
            player.damage(damage)
        }
    }
}

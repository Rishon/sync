package dev.rishon.sync.jedis.packet

import dev.rishon.sync.Sync
import dev.rishon.sync.data.CacheData
import dev.rishon.sync.data.RedisData
import dev.rishon.sync.utils.LoggerUtil
import org.bukkit.Bukkit
import java.util.*

class DamagePacket(
    private val damagerUUID: UUID,
    private val victimUUID: UUID,
    private val damage: Double,
    private val criticalHit: Boolean
) :
    IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received damage packet for $damagerUUID")

        val server = Bukkit.getServer()
        val cacheData = CacheData.instance
        val redisData = RedisData.instance
        val fakePlayer = cacheData.fakePlayers[damagerUUID] ?: return
        val instanceID = Sync.instance.instanceID

        if (instanceID == redisData.getPlayerData(victimUUID)?.instanceID) return

        val victim = server.getPlayer(victimUUID) ?: return
        victim.damage(damage)
        victim.location.direction.multiply(-0.5)
        victim.sendMessage("You have been hit by ${fakePlayer.second.name} with damage $damage")
    }
}

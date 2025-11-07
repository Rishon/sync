package systems.rishon.sync.jedis.packet

import org.bukkit.Bukkit
import systems.rishon.sync.Sync
import systems.rishon.sync.data.CacheData
import systems.rishon.sync.data.RedisData
import systems.rishon.sync.utils.LoggerUtil
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

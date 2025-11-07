package systems.rishon.sync.listener

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import systems.rishon.sync.handler.FileHandler
import systems.rishon.sync.handler.MainHandler
import systems.rishon.sync.jedis.JedisManager
import systems.rishon.sync.jedis.packet.DamagePacket

class EntityDamage(private val handler: MainHandler) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onEntityDamageByEntity(event: PlayerUseUnknownEntityEvent) {
        val damager = event.player
        val cacheData = this.handler.cacheData ?: return
        val collection = cacheData.fakePlayers.values

        if (collection.isEmpty()) return

        val fileHandler = FileHandler.handler
        val entityID = event.entityId

        // Still in development
        if (!fileHandler.debug) return

        if (collection.any { it.first == entityID }) {
            val fakePlayer = collection.first { it.first == entityID }.second
            val livingEntity = damager as LivingEntity
            var damage = 2.0
            val isCriticalHit = (damager.fallDistance > 0 && !livingEntity.isOnGround)
            if (isCriticalHit) damage *= 1.5
            damager.sendMessage("You have punched ${fakePlayer.name} with damage $damage")
            JedisManager.instance.sendPacket(DamagePacket(damager.uniqueId, fakePlayer.uuid, damage, isCriticalHit))
        }
    }

}
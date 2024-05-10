package dev.rishon.sync.listeners

import com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent
import dev.rishon.sync.handler.MainHandler
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packets.DamagePacket
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class EntityDamage(private val handler: MainHandler) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onEntityDamageByEntity(event: PlayerUseUnknownEntityEvent) {
        val damager = event.player
        val cacheData = this.handler.cacheData ?: return
        val collection = cacheData.fakePlayers.values

        if (collection.isEmpty()) return

        val entityID = event.entityId

        if (collection.any { it.first == entityID }) {
            val fakePlayer = collection.first { it.first == entityID }.second
            val livingEntity = damager as LivingEntity
            var damage = 1.0
            val isCriticalHit = (damager.fallDistance > 0 && !livingEntity.isOnGround)
            if (isCriticalHit) damage *= 1.5
            damager.sendMessage("You have punched ${fakePlayer.name} with damage $damage")
            JedisManager.instance.sendPacket(DamagePacket(damager.uniqueId, damage, isCriticalHit))
        }
    }

}
package dev.rishon.sync.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EntityDamage : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        val damager = event.damager
        val damage = event.damage
        val cause = event.cause
        Bukkit.broadcastMessage("Entity: $entity, Damager: $damager, Damage: $damage, Cause: $cause")
    }

}
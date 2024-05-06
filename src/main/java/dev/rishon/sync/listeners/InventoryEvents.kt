package dev.rishon.sync.listeners

import dev.rishon.sync.data.RedisData
import dev.rishon.sync.utils.InventorySerialization
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryEvents(private val redisData: RedisData) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onInventoryCloseEvent(event: InventoryCloseEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val playerData = redisData.getPlayerDataAsync(uuid)
        val playerInventory = player.inventory
        playerData.inventory = InventorySerialization.toBase64(playerInventory)
    }
}
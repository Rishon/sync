package dev.rishon.sync.listeners

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.mojang.datafixers.util.Pair
import dev.rishon.sync.data.RedisData
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packets.EquipmentPacket
import dev.rishon.sync.utils.InventorySerialization
import net.minecraft.world.entity.EquipmentSlot
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack

class InventoryEvents(private val redisData: RedisData) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onInventoryCloseEvent(event: InventoryCloseEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val playerData = redisData.getPlayerData(uuid) ?: return
        val playerInventory = player.inventory
        playerData.inventory = InventorySerialization.toBase64(playerInventory)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onArmorEquip(event: PlayerArmorChangeEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val playerData = redisData.getPlayerData(uuid) ?: return
        val playerInventory = player.inventory

        // Update player inventory in cache
        playerData.inventory = InventorySerialization.toBase64(playerInventory)

        // TODO: Improve this
        val equipmentList = mutableListOf<Pair<EquipmentSlot, MutableMap<String, Any>>>()

        player.equipment.armorContents.forEachIndexed { index, itemStack ->
            if (itemStack == null) {
                val emptyItemStack = ItemStack(Material.AIR)
                addEquipment(
                    equipmentList, EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, index), emptyItemStack
                )
                return@forEachIndexed
            }
            addEquipment(equipmentList, EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, index), itemStack)
        }

        JedisManager.instance.sendPacket(EquipmentPacket(uuid, equipmentList))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onItemSwap(event: PlayerItemHeldEvent) {
        val player = event.player
        val uuid = player.uniqueId

        // TODO: Improve this
        val equipmentList = mutableListOf<Pair<EquipmentSlot, MutableMap<String, Any>>>()
        val newSlot = event.newSlot
        var itemNewSlot = player.inventory.getItem(newSlot)
        if (itemNewSlot == null) itemNewSlot = ItemStack(Material.AIR)

        // Mainhand
        addEquipment(equipmentList, EquipmentSlot.MAINHAND, itemNewSlot)

        // Offhand
        addEquipment(equipmentList, EquipmentSlot.OFFHAND, player.inventory.itemInOffHand)

        JedisManager.instance.sendPacket(EquipmentPacket(uuid, equipmentList))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onOffhandSwap(event: PlayerSwapHandItemsEvent) {
        val player = event.player
        val uuid = player.uniqueId
        // TODO: Improve this
        val equipmentList = mutableListOf<Pair<EquipmentSlot, MutableMap<String, Any>>>()

        // Mainhand
        addEquipment(equipmentList, EquipmentSlot.MAINHAND, player.inventory.itemInMainHand)
        // Offhand
        addEquipment(equipmentList, EquipmentSlot.OFFHAND, player.inventory.itemInOffHand)

        JedisManager.instance.sendPacket(EquipmentPacket(uuid, equipmentList))
    }

    private fun addEquipment(
        equipmentList: MutableList<Pair<EquipmentSlot, MutableMap<String, Any>>>,
        equipmentSlot: EquipmentSlot,
        itemStack: ItemStack
    ) {
        equipmentList.add(
            Pair.of(
                equipmentSlot, itemStack.serialize()
            )
        )
    }
}
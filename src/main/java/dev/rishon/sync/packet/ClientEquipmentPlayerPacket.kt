package dev.rishon.sync.packet

import com.mojang.datafixers.util.Pair
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.entity.Player

object ClientEquipmentPlayerPacket : IPacket {

    @JvmStatic
    fun sendPacket(
        player: Player,
        serverPlayer: ServerPlayer,
        equipmentList: MutableList<Pair<EquipmentSlot, MutableMap<String, Any>>>
    ) {
        val craftPlayer = player as CraftPlayer
        val connection = craftPlayer.handle.connection
        val deserializedList = mutableListOf<Pair<EquipmentSlot, ItemStack>>()
        equipmentList.forEach {
            if (it.second == null || it.second.isEmpty()) return@forEach
            val slot = it.first ?: return@forEach
            val deserializeItemStack = CraftItemStack.deserialize(it.second)
            deserializedList.add(
                Pair.of(
                    slot, CraftItemStack.asNMSCopy(deserializeItemStack)
                )
            )
        }

        connection.send(ClientboundSetEquipmentPacket(serverPlayer.id, deserializedList))
    }
}
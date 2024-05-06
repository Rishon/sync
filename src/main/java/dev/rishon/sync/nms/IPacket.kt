package dev.rishon.sync.nms

import net.minecraft.world.entity.Entity
import org.bukkit.entity.Player
import java.lang.reflect.Field

interface IPacket {

    fun updateMetadata(player: Player, entity: Entity) {
        NMSManager.sendUpdatePacket(player, entity)
    }

    fun setValue(packet: Any, fieldName: String, value: Any) {
        try {
            val field: Field = packet.javaClass.getDeclaredField(fieldName)
            field.setAccessible(true)
            field.set(packet, value)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}
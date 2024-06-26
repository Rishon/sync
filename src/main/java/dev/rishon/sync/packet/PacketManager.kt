package dev.rishon.sync.packet

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import javax.annotation.Nullable

object PacketManager {

    @JvmStatic
    fun sendUpdatePacket(player: Player, entity: Entity) {
        val handle = (player as CraftPlayer).handle
        @Nullable val metas = getDataWatcher(entity).nonDefaultValues
        if (!metas.isNullOrEmpty()) {
            val packet = ClientboundSetEntityDataPacket(entity.id, metas)
            handle.connection.send(packet)
        }
    }

    @JvmStatic
    fun getDataWatcher(entity: Entity): SynchedEntityData {
        return entity.entityData
    }

}
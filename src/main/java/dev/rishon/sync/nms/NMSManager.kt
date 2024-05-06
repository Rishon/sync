package dev.rishon.sync.nms

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import javax.annotation.Nullable


object NMSManager {

    @JvmStatic
    fun sendUpdatePacket(player: Player, entity: Entity) {
        val handle = (player as CraftPlayer).handle
        @Nullable val metas = getDataWatcher(entity).nonDefaultValues
        if (!metas.isNullOrEmpty()) {
            val packet = ClientboundSetEntityDataPacket(entity.id, metas)
            handle.connection.send(packet)
        }
    }

    private fun getDataWatcher(entity: Entity): SynchedEntityData {
        return entity.entityData
    }

}
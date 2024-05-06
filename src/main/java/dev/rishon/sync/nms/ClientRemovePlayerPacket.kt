package dev.rishon.sync.nms

import dev.rishon.sync.utils.SchedulerUtil
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

object ClientRemovePlayerPacket : IPacket {

    @JvmStatic
    fun sendPacket(player: Player, uuid: UUID) {
        val craftPlayer = player as CraftPlayer
        val connection = craftPlayer.handle.connection
        SchedulerUtil.runTaskSync {
            val craftEntity = craftPlayer.server.getEntity(uuid) as CraftEntity
            connection.send(ClientboundRemoveEntitiesPacket(craftEntity.entityId))
        }
    }
}
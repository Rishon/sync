package dev.rishon.sync.nms

import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

object ClientRemovePlayerPacket : IPacket {

    @JvmStatic
    fun sendPacket(player: Player, entityID: Int) {
        val craftPlayer = player as CraftPlayer
        val connection = craftPlayer.handle.connection
        connection.send(ClientboundRemoveEntitiesPacket(entityID))
    }
}
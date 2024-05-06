package dev.rishon.sync.nms

import dev.rishon.sync.enums.Animations
import net.minecraft.network.protocol.game.ClientboundAnimatePacket
import net.minecraft.server.level.ServerPlayer
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

object ClientAnimatePlayerPacket : IPacket {

    @JvmStatic
    fun sendPacket(player: Player, serverPlayer: ServerPlayer, animation: Animations) {
        val craftPlayer = player as CraftPlayer
        val connection = craftPlayer.handle.connection
        connection.send(ClientboundAnimatePacket(serverPlayer, animation.id))
    }
}
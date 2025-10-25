package dev.rishon.sync.packet

import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.PositionMoveRotation
import net.minecraft.world.entity.Relative
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

object ClientPlayerMovePacket : IPacket {

    @JvmStatic
    fun sendPacket(player: Player, serverPlayer: ServerPlayer, location: MutableMap<String, Any>) {
        val craftPlayer = player as CraftPlayer
        val connection = craftPlayer.handle.connection
        val deserializeLocaiton = Location.deserialize(location)
        val x = deserializeLocaiton.x
        val y = deserializeLocaiton.y
        val z = deserializeLocaiton.z
        val yaw = deserializeLocaiton.yaw
        val pitch = deserializeLocaiton.pitch
        serverPlayer.snapTo(x, y, z, yaw, pitch)
        serverPlayer.setRot(yaw, pitch)
        connection.send(
            ClientboundTeleportEntityPacket(
                serverPlayer.id,
                PositionMoveRotation.of(serverPlayer),
                mutableSetOf<Relative>(),
                false
            )
        )
        connection.send(
            ClientboundRotateHeadPacket(
                serverPlayer, ((yaw * 256.0F / 360.0F).toInt().toByte())
            )
        )
    }
}
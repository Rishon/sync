package systems.rishon.sync.packet

import net.minecraft.network.protocol.game.ClientboundAnimatePacket
import net.minecraft.server.level.ServerPlayer
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import systems.rishon.sync.enums.Animations

object ClientAnimatePlayerPacket : IPacket {

    @JvmStatic
    fun sendPacket(player: Player, serverPlayer: ServerPlayer, animation: Animations) {
        val craftPlayer = player as CraftPlayer
        val connection = craftPlayer.handle.connection
        connection.send(ClientboundAnimatePacket(serverPlayer, animation.id))
    }
}
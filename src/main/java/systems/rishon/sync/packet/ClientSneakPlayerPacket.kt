package systems.rishon.sync.packet

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData.DataValue
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Pose
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

object ClientSneakPlayerPacket : IPacket {

    @JvmStatic
    fun sendPacket(player: Player, serverPlayer: ServerPlayer, isSneaking: Boolean, isFlying: Boolean) {
        val craftPlayer = player as CraftPlayer
        val connection = craftPlayer.handle.connection
        val pose: Pose = if (isSneaking) Pose.CROUCHING else Pose.STANDING
        val flag: Byte = if (isSneaking) 0x02 else 0
        val dataValueList: MutableList<DataValue<*>> = getDataWatcher(serverPlayer).nonDefaultValues ?: return

        if (!isFlying) dataValueList.add(
            DataValue.create(
                EntityDataAccessor(6, EntityDataSerializers.POSE), pose
            )
        )
        dataValueList.add(DataValue.create(EntityDataAccessor(0, EntityDataSerializers.BYTE), flag))
        connection.send(ClientboundSetEntityDataPacket(serverPlayer.id, dataValueList))
    }
}
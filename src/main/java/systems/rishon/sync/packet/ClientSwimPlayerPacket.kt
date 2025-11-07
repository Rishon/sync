package systems.rishon.sync.packet

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData.DataValue
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Pose
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

object ClientSwimPlayerPacket : IPacket {

    @JvmStatic
    fun sendPacket(player: Player, serverPlayer: ServerPlayer, isSwimming: Boolean) {
        val craftPlayer = player as CraftPlayer
        val connection = craftPlayer.handle.connection
        val pose: Pose = if (isSwimming) Pose.SWIMMING else Pose.STANDING
        val flag: Byte = if (isSwimming) 0x10 else 0
        val dataValueList: MutableList<DataValue<*>> = getDataWatcher(serverPlayer).nonDefaultValues ?: return
        dataValueList.add(DataValue.create(EntityDataAccessor(6, EntityDataSerializers.POSE), pose))
        dataValueList.add(DataValue.create(EntityDataAccessor(0, EntityDataSerializers.BYTE), flag))
        connection.send(ClientboundSetEntityDataPacket(serverPlayer.id, dataValueList))
    }
}
package dev.rishon.sync.nms

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData.DataValue
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Pose
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player


object ClientJumpPlayerPacket : IPacket {

    @JvmStatic
    fun sendPacket(player: Player, serverPlayer: ServerPlayer) {
        val craftPlayer = player as CraftPlayer
        val connection = craftPlayer.handle.connection
        val pose: Pose = Pose.LONG_JUMPING
        val dataValueList: MutableList<DataValue<*>> = getDataWatcher(serverPlayer).nonDefaultValues ?: return
        dataValueList.add(DataValue.create(EntityDataAccessor(6, EntityDataSerializers.POSE), pose))
        connection.send(ClientboundSetEntityDataPacket(serverPlayer.id, dataValueList))
    }
}
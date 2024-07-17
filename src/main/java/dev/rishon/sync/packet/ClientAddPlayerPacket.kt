package dev.rishon.sync.packet

import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

object ClientAddPlayerPacket : IPacket {

    // Packet list
    private val packetList: MutableList<ClientboundPlayerInfoUpdatePacket.Action> = listOf(
        ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY,
        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
        ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE
    ).toMutableList()

    @JvmStatic
    fun sendPacket(player: Player, serverPlayer: ServerPlayer) {
        val craftPlayer = player as CraftPlayer
        val connection = craftPlayer.handle.connection
        setValue(serverPlayer, "c", craftPlayer.handle.connection);

        packetList.forEach { action -> connection.send(ClientboundPlayerInfoUpdatePacket(action, serverPlayer)) }

        connection.send(ClientboundAddEntityPacket(serverPlayer, serverPlayer.id, serverPlayer.blockPosition()))

        // Update entity metadata
        val dataValues: MutableList<SynchedEntityData.DataValue<*>> = ArrayList()
        val b = (0x01 or 0x02 or 0x04 or 0x08 or 0x10 or 0x20 or 0x40).toByte()
        dataValues.add(SynchedEntityData.DataValue.create(EntityDataAccessor(17, EntityDataSerializers.BYTE), b))
        connection.send(ClientboundSetEntityDataPacket(serverPlayer.id, dataValues))
    }
}
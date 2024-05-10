package dev.rishon.sync.jedis.packets

import com.mojang.datafixers.util.Pair
import dev.rishon.sync.data.CacheData
import dev.rishon.sync.nms.ClientEquipmentPlayerPacket
import dev.rishon.sync.utils.LoggerUtil
import net.minecraft.world.entity.EquipmentSlot
import org.bukkit.Bukkit
import java.util.*

class EquipmentPacket(
    private val playerUUID: UUID,
    private val equipmentList: MutableList<Pair<EquipmentSlot, MutableMap<String, Any>>>
) :
    IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received equipment packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance
        val fakePlayer = cacheData.fakePlayers[playerUUID] ?: return

        onlinePlayers.forEach { player ->
            if (player.uniqueId == playerUUID) return@forEach
            ClientEquipmentPlayerPacket.sendPacket(player, fakePlayer.second, equipmentList)
        }
    }
}

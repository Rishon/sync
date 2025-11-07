package systems.rishon.sync.jedis.packet

import com.mojang.datafixers.util.Pair
import net.minecraft.world.entity.EquipmentSlot
import org.bukkit.Bukkit
import systems.rishon.sync.data.CacheData
import systems.rishon.sync.packet.ClientEquipmentPlayerPacket
import systems.rishon.sync.utils.LoggerUtil
import java.util.*

class EquipmentPacket(
    private val playerUUID: UUID,
    private val equipmentList: MutableList<Pair<EquipmentSlot, MutableMap<String, Any>>>
) :
    IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received equipment packet for $playerUUID")

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

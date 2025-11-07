package systems.rishon.sync.jedis.packet

import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.Location
import systems.rishon.sync.utils.LoggerUtil
import systems.rishon.sync.utils.SchedulerUtil

class WorldPacket(
    private val name: String,
    private val time: Long,
    private val worldBorderSize: Double,
    private val worldBorderCenter: Map<String, Any>,
    private val weatherDuration: Int,
    private val storming: Boolean,
    private val thundering: Boolean,
    private val difficulty: Difficulty
) : IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received world packet")

        val localWorld = Bukkit.getWorld(name) ?: return
        SchedulerUtil.runTaskSync {
            localWorld.time = time
            localWorld.worldBorder.size = worldBorderSize
            localWorld.worldBorder.center = Location.deserialize(worldBorderCenter)
            localWorld.weatherDuration = weatherDuration
            localWorld.setStorm(storming)
            localWorld.isThundering = thundering
            localWorld.difficulty = difficulty
        }
    }
}

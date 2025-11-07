package systems.rishon.sync.jedis.packet

import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Bukkit
import systems.rishon.sync.utils.LoggerUtil

class MessagePacket(private val json: String, private val permission: String) : IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received message packet")

        val server = Bukkit.getServer()
        val component = JSONComponentSerializer.json().deserialize(json);
        server.onlinePlayers.forEach { player ->
            if (permission.isNotEmpty() && player.hasPermission(permission)) player.sendMessage(component)
            else player.sendMessage(component)
        }
    }
}

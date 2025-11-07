package systems.rishon.sync.jedis.packet

import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Bukkit
import systems.rishon.sync.utils.LoggerUtil

class ChatPacket(private val playerName: String, private val json: String) : IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received chat packet for $playerName")
        val server = Bukkit.getServer()
        val component = JSONComponentSerializer.json().deserialize(json);
        server.sendMessage(component)
    }
}

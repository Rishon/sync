package dev.rishon.sync.jedis.packet

import dev.rishon.sync.utils.LoggerUtil
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Bukkit

class ChatPacket(private val playerName: String, private val json: String) : IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received chat packet for $playerName")
        val server = Bukkit.getServer()
        val component = JSONComponentSerializer.json().deserialize(json);
        server.sendMessage(component)
    }
}

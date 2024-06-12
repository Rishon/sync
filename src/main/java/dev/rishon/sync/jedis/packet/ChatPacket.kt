package dev.rishon.sync.jedis.packet

import dev.rishon.sync.utils.LoggerUtil
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Bukkit

class ChatPacket(private val playerName: String, private val json: String) : IPacket {

    override fun onReceive() {
        LoggerUtil.debug("Received chat packet for $playerName")
        val player = Bukkit.getPlayer(playerName)
        if (player != null) return // Don't send the message to the same server as player
        val server = Bukkit.getServer()
        val component = JSONComponentSerializer.json().deserialize(json);
        server.sendMessage(component)
    }
}

package dev.rishon.sync.jedis.packets

import dev.rishon.sync.utils.LoggerUtil
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Bukkit

class MessagePacket(private val json: String, private val permission: String) : IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received message packet")
        val server = Bukkit.getServer()
        val component = JSONComponentSerializer.json().deserialize(json);
        server.onlinePlayers.forEach { player ->
            if (permission.isNotEmpty() && player.hasPermission(permission)) player.sendMessage(component)
            else player.sendMessage(component)
        }
    }
}

package dev.rishon.sync.jedis.packets

import dev.rishon.sync.utils.LoggerUtil
import org.bukkit.Bukkit

class SneakPacket(private val playerName: String) : IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received sneak packet for $playerName")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers

        onlinePlayers.forEach { player ->

        }
    }
}

package dev.rishon.sync.api

import dev.rishon.sync.data.RedisData
import dev.rishon.sync.handler.FileHandler
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packets.MessagePacket
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import java.util.*

class SyncAPI {

    init {
        instance = this
    }

    fun getOnlinePlayersCount(): Int {
        val serverData = RedisData.instance.getServerData() ?: return 0
        return serverData.onlinePlayers.size
    }

    fun getOnlinePlayers(): List<UUID>? {
        val serverData = RedisData.instance.getServerData() ?: return null
        return serverData.onlinePlayers.toList()
    }

    fun getInstanceID(): String? {
        val serverData = RedisData.instance.getServerData() ?: return null
        return serverData.instanceID
    }

    fun getInstancesNames(): List<String>? {
        return RedisData.instance.getInstancesNames()
    }

    fun getFormattedInstanceID(): String? {
        return FileHandler.handler.instanceFormat
    }

    fun broadcastMessage(message: Component) {
        broadcastMessagePermission(message, "")
    }

    fun broadcastMessagePermission(message: Component, permission: String) {
        JedisManager.instance.sendPacket(MessagePacket(JSONComponentSerializer.json().serialize(message), permission))
    }

    companion object {
        // Static instance
        lateinit var instance: SyncAPI

        @JvmStatic
        fun getAPI(): SyncAPI {
            return instance
        }
    }
}
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

    /*
   * Get the count of online players across all instances.
   * @return The online players count.
   */
    fun getOnlinePlayersCount(): Int {
        val serverData = RedisData.instance.getServerData() ?: return 0
        return serverData.onlinePlayers.size
    }

    /*
     * Get the list of online players across all instances.
     * @return A list of UUIDs representing online players.
     */
    fun getOnlinePlayers(): List<UUID>? {
        val serverData = RedisData.instance.getServerData() ?: return null
        return serverData.onlinePlayers.toList()
    }

    /*
     * Get the instance ID.
     * @return The instance ID as a String.
     */
    fun getInstanceID(): String? {
        val serverData = RedisData.instance.getServerData() ?: return null
        return serverData.instanceID
    }

    /*
     * Get the names of all instances.
     * @return A list of instance names.
     */
    fun getInstancesNames(): List<String>? {
        return RedisData.instance.getInstancesNames()
    }

    /*
     * Get the formatted instance ID.
     * @return The formatted instance ID as a String.
     */
    fun getFormattedInstanceID(): String? {
        return FileHandler.handler.instanceFormat
    }

    /*
     * Broadcast a message to all instances.
     * @param message The message to broadcast.
     */
    fun broadcastMessage(message: Component) {
        broadcastMessage(message, "")
    }

    /*
     * Broadcast a message to all instances.
     * @param message The message to broadcast.
     * @param permission The permission required to receive the message.
     */
    fun broadcastMessage(message: Component, permission: String) {
        JedisManager.instance.sendPacket(MessagePacket(JSONComponentSerializer.json().serialize(message), permission))
    }

    companion object {
        // Static-Access
        lateinit var instance: SyncAPI

        @JvmStatic
        fun getAPI(): SyncAPI {
            return instance
        }
    }
}
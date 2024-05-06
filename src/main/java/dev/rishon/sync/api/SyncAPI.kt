package dev.rishon.sync.api

import dev.rishon.sync.data.RedisData
import dev.rishon.sync.handler.FileHandler
import java.util.*

class SyncAPI {

    init {
        instance = this
    }

    fun getOnlinePlayersCount(): Int {
        val serverData = RedisData.instance.getServerDataAsync()
        return serverData.onlinePlayers.size
    }

    fun getOnlinePlayers(): List<UUID>? {
        val serverData = RedisData.instance.getServerDataAsync()
        return serverData.onlinePlayers.toList()
    }

    fun getInstanceID(): String? {
        val serverData = RedisData.instance.getServerDataAsync()
        return serverData.instanceID
    }

    fun getFormattedInstanceID(): String? {
        return FileHandler.handler.instancePrefix
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
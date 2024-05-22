package dev.rishon.sync

import dev.rishon.sync.handler.FileHandler
import dev.rishon.sync.handler.IHandler
import dev.rishon.sync.handler.MainHandler
import dev.rishon.sync.utils.FoliaSupport
import dev.rishon.sync.utils.SchedulerUtil
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class Sync : JavaPlugin() {

    // Handlers
    private val handlers: MutableList<IHandler> = mutableListOf()

    // Instance ID
    val instanceID: String = UUID.randomUUID().toString().slice(0..4)

    // isFolia?
    val isFolia: Boolean = FoliaSupport.isFolia()

    override fun onEnable() {
        instance = this

        // Has Folia?
        if (isFolia) this.logger.info("Folia detected! Still experimental!")

        // Register plugin channel
        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        server.messenger.registerOutgoingPluginChannel(this, "sync:toproxy")

        // Register handlers
        this.handlers.add(FileHandler(this))
        this.handlers.add(MainHandler(this))
        this.handlers.forEach { it.init() }

        this.logger.info("${this.name} has been enabled!")
    }

    override fun onDisable() {
        this.handlers.forEach { it.end() }
        SchedulerUtil.cancelAllTasks()
        this.logger.info("${this.name} has been disabled!")
    }

    companion object {
        lateinit var instance: Sync
    }
}

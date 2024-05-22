package dev.rishon.sync.handler

import dev.rishon.sync.Sync
import net.minecraft.server.MinecraftServer
import net.minecraft.server.dedicated.DedicatedServer
import net.minecraft.server.dedicated.DedicatedServerSettings
import org.bukkit.configuration.file.FileConfiguration
import java.io.File


class FileHandler(private var instance: Sync) : IHandler {

    // Config
    var config: FileConfiguration? = null

    // Settings
    var instancePrefix: String? = null
    var instanceFormat: String? = null
    var transferPackets: Boolean = false
    var isUnderProxy: Boolean = false

    override fun init() {
        handler = this;
        createConfig()
        loadConfigSettings()
        handleTransferPackets()
    }

    override fun end() {}

    private fun createConfig() {
        val file = File(this.instance.dataFolder, "config.yml")
        if (!file.exists()) this.instance.saveResource("config.yml", false)
        this.instance.saveDefaultConfig();
        this.config = this.instance.config
        this.config?.options()?.copyDefaults(true)
        this.instance.saveConfig()
    }

    private fun loadConfigSettings() {
        this.instancePrefix = this.config?.getString("instance-prefix")
        this.instanceFormat = this.instancePrefix?.replace("{id}", Sync.instance.instanceID)
        this.transferPackets = this.config?.getBoolean("allow-transfer-packets") ?: false
        this.isUnderProxy = this.config?.getBoolean("is-under-proxy") ?: false
    }

    private fun handleTransferPackets() {
        if (this.transferPackets) {
            // Allow transfer packets
            val minecraftServer = MinecraftServer.getServer() as DedicatedServer
            val settings: DedicatedServerSettings = minecraftServer.settings
            settings.properties.properties.setProperty("accepts-transfers", "true")
            settings.properties.acceptsTransfers = true
            settings.forceSave()
        }
    }

    companion object {
        // Static-Access
        lateinit var handler: FileHandler
    }
}
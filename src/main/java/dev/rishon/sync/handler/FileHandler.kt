package dev.rishon.sync.handler

import dev.rishon.sync.Sync
import dev.rishon.sync.data.DataType
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
        loadDataTypes()
    }

    override fun end() {}

    private fun createConfig() {
        val file = File(this.instance.dataFolder, "config.yml")
        if (!file.exists()) this.instance.saveResource("config.yml", false)
        this.config = this.instance.config
        this.config?.options()?.copyDefaults(true)
        this.instance.saveDefaultConfig();
        this.instance.saveConfig()
    }

    private fun loadConfigSettings() {
        this.instancePrefix = this.config?.getString("instance-prefix")
        this.instanceFormat = this.instancePrefix?.replace("{id}", Sync.instance.instanceID)
        this.transferPackets = this.config?.getBoolean("allow-transfer-packets") == true
        this.isUnderProxy = this.config?.getBoolean("is-under-proxy") == true
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

    private fun loadDataTypes() {
        val config = this.config
        val path = "player-data"
        val configurationSection = config?.getConfigurationSection(path)

        if (configurationSection == null) {
            // Write all data types if configuration section is not found
            DataType.entries.forEach { dataType ->
                config?.addDefault("$path.${dataType}", true)
            }
            this.instance.saveConfig()
        } else {
            // Load data types
            DataType.entries.forEach { dataType ->
                val dataTypeString = dataType.toString()
                if (!configurationSection.contains(dataTypeString)) {
                    config.set("$path.${dataTypeString}", true)
                    this.instance.saveConfig()
                }
                dataType.isSynced = configurationSection.getBoolean(dataTypeString)
            }
        }
    }

    companion object {
        // Static-Access
        lateinit var handler: FileHandler
    }
}
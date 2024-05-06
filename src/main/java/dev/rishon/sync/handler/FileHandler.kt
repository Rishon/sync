package dev.rishon.sync.handler

import dev.rishon.sync.Sync
import org.bukkit.configuration.file.FileConfiguration
import java.io.File

class FileHandler(private var instance: Sync) : IHandler {

    // Config
    var config: FileConfiguration? = null

    // Settings
    var instancePrefix: String? = null

    override fun init() {
        handler = this;
        createConfig()
        loadConfigSettings()
    }

    override fun end() {}

    private fun createConfig() {
        val file = File(this.instance.dataFolder, "config.yml")
        if (!file.exists()) this.instance.saveResource("config.yml", false)
        this.instance.saveDefaultConfig();
        this.config = this.instance.config
    }

    private fun loadConfigSettings() {
        this.instancePrefix =
            this.config?.getString("instance-prefix")?.replace("{id}", Sync.instance.instanceID)
    }

    companion object {
        // Static-Access
        lateinit var handler: FileHandler
    }
}
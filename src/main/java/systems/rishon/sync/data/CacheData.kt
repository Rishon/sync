package systems.rishon.sync.data

import net.minecraft.server.level.ServerPlayer
import java.util.*

class CacheData : IDataModule {

    // Cached fake players <UUID, EntityID>
    val fakePlayers: MutableMap<UUID, Pair<Int, ServerPlayer>> = mutableMapOf()

    override fun init() {
        instance = this
    }

    override fun end() {}

    companion object {
        // Static-Access
        lateinit var instance: CacheData
    }
}
package dev.rishon.sync.utils

import com.google.common.io.ByteStreams
import dev.rishon.sync.Sync
import org.bukkit.entity.Player

object PluginMessageUtil {

    @JvmStatic
    fun sendPluginMessage(player: Player, identifier: String, subchannel: String, data: String) {
        SchedulerUtil.runTaskAsync {
            val out = ByteStreams.newDataOutput()
            out.writeUTF(subchannel)
            out.writeUTF(data)
            player.sendPluginMessage(Sync.instance, identifier, out.toByteArray())
        }
    }

}
package dev.rishon.sync.tasks

import dev.rishon.sync.api.SyncAPI
import dev.rishon.sync.handler.MainHandler
import net.kyori.adventure.text.Component

class InstanceTask(val handler: MainHandler) : Runnable {

    override fun run() {
        this.handler.instance.server.onlinePlayers.forEach { player ->
            player.sendActionBar(
                Component.text(
                    "Instance: ${
                        SyncAPI.getAPI().getFormattedInstanceID()
                    } | Online Players: ${SyncAPI.getAPI().getOnlinePlayersCount()}"
                )
            )
        }
    }

}
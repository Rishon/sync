package dev.rishon.sync.listener

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import dev.rishon.sync.api.SyncAPI
import dev.rishon.sync.handler.MainHandler
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class ServerPing(val handler: MainHandler) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onServerListPing(event: PaperServerListPingEvent) {
        event.numPlayers = SyncAPI.getAPI().getOnlinePlayersCount()
    }

}
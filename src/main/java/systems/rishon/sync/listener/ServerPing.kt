package systems.rishon.sync.listener

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import systems.rishon.sync.api.SyncAPI
import systems.rishon.sync.handler.MainHandler

class ServerPing(val handler: MainHandler) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onServerListPing(event: PaperServerListPingEvent) {
        event.numPlayers = SyncAPI.getAPI().getOnlinePlayersCount()
    }

}
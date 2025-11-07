package systems.rishon.sync.listener

import io.papermc.paper.event.player.AsyncChatEvent
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import systems.rishon.sync.handler.FileHandler
import systems.rishon.sync.handler.MainHandler
import systems.rishon.sync.jedis.JedisManager
import systems.rishon.sync.jedis.packet.ChatPacket
import systems.rishon.sync.utils.ColorUtil

class AsyncChat : Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onAsyncChat(event: AsyncChatEvent) {
        event.isCancelled = true

        val player = event.player
        val fileHandler = FileHandler.handler

        var format = fileHandler.playerChatFormat!!.replace("{player}", player.name).replace(
            "{message}", LegacyComponentSerializer.legacySection().serialize(event.message())
        )

        // If PlaceholderAPI is installed, replace placeholders
        if (MainHandler.handler.placeholderAPI!!) format = PlaceholderAPI.setPlaceholders(player, format)

        val renderedComponent: Component = ColorUtil.translate(format)
        event.renderer { _, _, _, _ -> return@renderer renderedComponent }

        JedisManager.instance.sendPacket(
            ChatPacket(
                player.name, JSONComponentSerializer.json().serialize(renderedComponent)
            )
        )
    }

}
package dev.rishon.sync.listener

import dev.rishon.sync.handler.FileHandler
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packet.ChatPacket
import dev.rishon.sync.utils.ColorUtil
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class AsyncChat : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onAsyncChat(event: AsyncChatEvent) {
        val player = event.player
        val fileHandler = FileHandler.handler

        val format = fileHandler.playerChatFormat!!.replace("{player}", player.name).replace(
            "{message}", LegacyComponentSerializer.legacySection().serialize(event.message())
        )

        val renderedComponent: Component = ColorUtil.translate(format)
        event.renderer { _, _, _, _ -> return@renderer renderedComponent }

        JedisManager.instance.sendPacket(
            ChatPacket(
                player.name, JSONComponentSerializer.json().serialize(renderedComponent)
            )
        )
    }

}
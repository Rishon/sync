package dev.rishon.sync.listener

import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packet.ChatPacket
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class AsyncChat : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onAsyncChat(event: AsyncChatEvent) {
        val player = event.player;
        val renderedComponent: Component =
            Component.text(player.name).append(Component.text(": ")).append(event.message())

        event.renderer { _, _, _, _ -> return@renderer renderedComponent }

        JedisManager.instance.sendPacket(
            ChatPacket(
                player.name,
                JSONComponentSerializer.json().serialize(renderedComponent)
            )
        )
    }

}
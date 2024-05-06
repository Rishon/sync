package dev.rishon.sync.utils

import dev.rishon.sync.enums.Colors
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage

object ColorUtil {

    private val mm: MiniMessage = MiniMessage.builder().build()

    @JvmStatic
    fun translate(string: String): Component {
        return mm.deserialize(string).decoration(TextDecoration.ITALIC, false)
    }

    @JvmStatic
    fun translate(string: String, color: Colors): Component {
        return mm.deserialize(color.color + string).decoration(TextDecoration.ITALIC, false)
    }

}
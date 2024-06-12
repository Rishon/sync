package dev.rishon.sync.command

import dev.rishon.sync.api.SyncAPI
import dev.rishon.sync.enums.Colors
import dev.rishon.sync.handler.MainHandler
import dev.rishon.sync.utils.ColorUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class WhereAmICommand(val handler: MainHandler) : CommandExecutor {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>?): Boolean {

        if (sender !is Player) {
            sender.sendMessage(ColorUtil.translate("Only players can execute this command!", Colors.ERROR))
            return false
        }

        val player: Player = sender
        player.sendMessage(
            ColorUtil.translate(
                "You are connected to ${SyncAPI.getAPI().getFormattedInstanceID()}", Colors.INFO
            )
        )

        return false
    }
}
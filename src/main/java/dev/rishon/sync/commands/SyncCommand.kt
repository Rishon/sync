package dev.rishon.sync.commands

import dev.rishon.sync.enums.Animations
import dev.rishon.sync.enums.Colors
import dev.rishon.sync.handler.MainHandler
import dev.rishon.sync.nms.ClientAnimatePlayerPacket
import dev.rishon.sync.utils.ColorUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class SyncCommand(val handler: MainHandler) : CommandExecutor {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>?): Boolean {

        if (args?.isEmpty() == true) {
            sender.sendMessage(ColorUtil.translate("Command Usage: /sync <test>", Colors.INFO))
            return false
        }

        when (args?.get(0)) {
            "test" -> {
                val player = sender as Player
                // Create fake player
                val cacheData = handler.cacheData
                cacheData?.fakePlayers?.forEach { (uuid, serverPlayer) ->
                    if (serverPlayer == null) {
                        player.sendMessage("Server player is null")
                        return@forEach
                    }
                    ClientAnimatePlayerPacket.sendPacket(player, serverPlayer, Animations.SWING_MAIN_ARM)
                    player.sendMessage("Sent animation packet for $uuid")
                }
            }
        }

        return false
    }
}
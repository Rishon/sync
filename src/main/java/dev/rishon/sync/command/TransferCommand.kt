package dev.rishon.sync.command

import dev.rishon.sync.api.SyncAPI
import dev.rishon.sync.enums.Colors
import dev.rishon.sync.handler.FileHandler
import dev.rishon.sync.handler.MainHandler
import dev.rishon.sync.utils.ColorUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class TransferCommand(val handler: MainHandler) : CommandExecutor, TabExecutor {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            if (args?.size == 0) {
                sender.sendMessage(ColorUtil.translate("Command Usage: /transfer <target> <instance>", Colors.INFO))
                return true
            }

            val target: Player? = sender.server.getPlayer(args!![0])
            if (target == null) {
                sender.sendMessage(ColorUtil.translate("Target not found!", Colors.ERROR))
                return true
            }

            val instance: String = args[1]
            if (instance == this.handler.instance.instanceID) {
                sender.sendMessage(
                    ColorUtil.translate(
                        "You cannot transfer the target to the same instance!", Colors.ERROR
                    )
                )
                return true
            }

            // Transfer player
            transferPlayer(target, instance)
            return true
        } else {
            val player: Player = sender
            if (args?.size == 0) {
                player.sendMessage(ColorUtil.translate("Command Usage: /transfer <instance>", Colors.INFO))
                return true
            }

            val instance: String = args!![0]
            if (instance == FileHandler.handler.instanceFormat) {
                player.sendMessage(
                    ColorUtil.translate(
                        "You cannot transfer yourself to the same instance!", Colors.ERROR
                    )
                )
                return true
            }

            // Transfer player
            transferPlayer(player, instance)
            return true
        }
    }

    override fun onTabComplete(
        sender: CommandSender, p1: Command, p2: String, args: Array<out String>
    ): MutableList<String>? {
        if (sender !is Player) return mutableListOf()
        if (args?.size == 1) {
            return SyncAPI.getAPI().getInstancesNames()?.toMutableList()
        }
        return mutableListOf()
    }

    private fun transferPlayer(player: Player, instance: String) {
        // Transfer player
        val serverData = this.handler.redisData?.getInstanceDataByName(instance)
        if (serverData == null) {
            player.sendMessage(ColorUtil.translate("Instance not found!", Colors.ERROR))
            return
        }
        player.transfer(serverData.serverIP!!, serverData.serverPort!!)
    }
}
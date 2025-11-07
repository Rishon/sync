package systems.rishon.sync.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import systems.rishon.sync.api.SyncAPI
import systems.rishon.sync.enums.Colors
import systems.rishon.sync.handler.MainHandler
import systems.rishon.sync.utils.ColorUtil

class WhereAmICommand(val handler: MainHandler) : CommandExecutor {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {

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
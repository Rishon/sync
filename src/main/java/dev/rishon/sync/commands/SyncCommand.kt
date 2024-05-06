package dev.rishon.sync.commands

import com.mojang.authlib.GameProfile
import dev.rishon.sync.enums.Colors
import dev.rishon.sync.handler.MainHandler
import dev.rishon.sync.nms.ClientAddPlayerPacket
import dev.rishon.sync.utils.ColorUtil
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerPlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Player
import java.util.*


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
                val nmsWorld = (player.world as CraftWorld).handle
                val gameProfile = GameProfile(UUID.fromString("0ef63bc9-eb9e-4101-890b-0417552f43a2"), "ItsRishon")
                val nmsServer: MinecraftServer = (player.server as CraftServer).server
                val fakePlayer = ServerPlayer(nmsServer, nmsWorld, gameProfile, ClientInformation.createDefault())
                val location = player.location
                fakePlayer.setPos(location.x, location.y, location.z)

                ClientAddPlayerPacket.sendPacket(player, fakePlayer)
                player.sendMessage(ColorUtil.translate("Created fake player for ItsRishon", Colors.INFO))
            }
        }

        return false
    }
}
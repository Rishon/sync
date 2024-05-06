package dev.rishon.sync.jedis.packets

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.rishon.sync.data.CacheData
import dev.rishon.sync.data.RedisData
import dev.rishon.sync.nms.ClientAddPlayerPacket
import dev.rishon.sync.utils.LoggerUtil
import dev.rishon.sync.utils.Utils
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ClientInformation
import net.minecraft.server.level.ServerPlayer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Player
import java.util.*


class ConnectPacket(
    private val location: MutableMap<String, Any>,
    private val playerName: String,
    private val playerUUID: UUID,
    private val skin: Array<String>
) : IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received connect packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance

        cacheData.fakePlayers.forEach { (uuid, fakePlayer) ->
            Bukkit.broadcastMessage("Checking fake player $uuid")
            if (uuid.toString() == playerUUID.toString()) return@forEach
            val joinedPlayer = server.getPlayer(playerUUID) ?: return@forEach
            val fakePlayerLocation = fakePlayer.bukkitEntity.location.serialize()
            val fakePlayerName = fakePlayer.gameProfile.name
            Bukkit.broadcastMessage("Attempting to create a fake player of $fakePlayerName")
            createFakePlayer(
                joinedPlayer,
                server,
                Location.deserialize(fakePlayerLocation),
                fakePlayerName,
                uuid,
                Utils.getFakePlayerSkin(fakePlayer)
            )
            Bukkit.broadcastMessage("1Created fake player for $fakePlayerName")
        }

        onlinePlayers.forEach { player ->
            Bukkit.broadcastMessage("Checking player ${player.uniqueId}")
            if (player.uniqueId.toString() == playerUUID.toString()) {
                Bukkit.broadcastMessage("Player already exists")
                return@forEach
            }
            createFakePlayer(
                player, server, Location.deserialize(this.location), this.playerName, this.playerUUID, this.skin
            ) // Fake player of the player that has joined
            Bukkit.broadcastMessage("2Created fake player for $playerName")
        }

        // Add player to online players
        val serverData = RedisData.instance.getServerDataAsync()
        serverData.onlinePlayers.add(playerUUID)

        RedisData.instance.setServerDataAsync(serverData)
    }

    private fun createFakePlayer(
        viewer: Player, server: Server, location: Location, playerName: String, playerUUID: UUID, skin: Array<String>
    ) {
        // Create fake player
        val level = (location.world as CraftWorld).handle
        val gameProfile = GameProfile(playerUUID, playerName)
        gameProfile.properties.put(
            "textures", Property("textures", skin[0], skin[1])
        )
        val nmsServer: MinecraftServer = (server as CraftServer).server
        val fakePlayer = ServerPlayer(nmsServer, level, gameProfile, ClientInformation.createDefault())
        fakePlayer.setPos(location.x, location.y, location.z)
        fakePlayer.spawnIn(level)
        ClientAddPlayerPacket.sendPacket(viewer, fakePlayer)

        // Add to local cacheData
        val cacheData = CacheData.instance
        cacheData.fakePlayers[playerUUID] = fakePlayer
    }
}

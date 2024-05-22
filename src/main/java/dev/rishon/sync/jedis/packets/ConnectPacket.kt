package dev.rishon.sync.jedis.packets

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import dev.rishon.sync.data.CacheData
import dev.rishon.sync.data.RedisData
import dev.rishon.sync.nms.ClientAddPlayerPacket
import dev.rishon.sync.utils.LoggerUtil
import dev.rishon.sync.utils.Utils
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
    private val location: Map<String, Any>,
    private val playerName: String,
    private val playerUUID: UUID,
    private val skin: Array<String>
) : IPacket {

    override fun onReceive() {
        LoggerUtil.info("Received connect packet for $playerUUID")

        val server = Bukkit.getServer()
        val onlinePlayers = server.onlinePlayers
        val cacheData = CacheData.instance

        val deserializedLocation = Location.deserialize(location)

        // Create fake player for the joining player
        createFakePlayer(null, server, deserializedLocation, playerName, playerUUID, skin)

        // Inform the new player of existing fake players
        val joinedPlayer = server.getPlayer(playerUUID)
        if (joinedPlayer != null) {
            cacheData.fakePlayers.forEach { (uuid, fakePlayer) ->
                if (uuid != playerUUID) {
                    val fakePlayerLocation = fakePlayer.second.bukkitEntity.location
                    val fakePlayerName = fakePlayer.second.gameProfile.name
                    val fakePlayerSkin = Utils.getFakePlayerSkin(fakePlayer.second)
                    createFakePlayer(joinedPlayer, server, fakePlayerLocation, fakePlayerName, uuid, fakePlayerSkin)
                }
            }
        } else {
            // Inform existing online players of the new player
            onlinePlayers.forEach { player ->
                createFakePlayer(player, server, deserializedLocation, playerName, playerUUID, skin)
            }
        }

        // Add player to online players in server data
        RedisData.instance.getServerData()?.let { serverData ->
            serverData.onlinePlayers.add(playerUUID)
            RedisData.instance.setServerData(serverData)
        }
    }

    private fun createFakePlayer(
        viewer: Player?,
        server: Server,
        location: Location,
        playerName: String,
        playerUUID: UUID,
        skin: Array<String>
    ) {
        // Create the game profile and fake player
        val gameProfile = GameProfile(playerUUID, playerName).apply {
            properties.put("textures", Property("textures", skin[0], skin[1]))
        }
        val nmsServer = (server as CraftServer).server
        val level = (location.world as CraftWorld).handle
        val fakePlayer = ServerPlayer(nmsServer, level, gameProfile, ClientInformation.createDefault()).apply {
            setPos(location.x, location.y, location.z)
            spawnIn(level)
        }

        // Send the fake player to the viewer, if applicable
        if (viewer != null && playerUUID != viewer.uniqueId) {
            ClientAddPlayerPacket.sendPacket(viewer, fakePlayer)
        }

        // Add to local cache data
        CacheData.instance.fakePlayers[playerUUID] = Pair(fakePlayer.id, fakePlayer)
    }
}
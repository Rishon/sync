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
            if (uuid.toString() == playerUUID.toString()) return@forEach
            val joinedPlayer = server.getPlayer(playerUUID) ?: return@forEach
            val fakePlayerLocation = fakePlayer.second.bukkitEntity.location.serialize()
            val fakePlayerName = fakePlayer.second.gameProfile.name
            createFakePlayer(
                joinedPlayer,
                server,
                Location.deserialize(fakePlayerLocation),
                fakePlayerName,
                uuid,
                Utils.getFakePlayerSkin(fakePlayer.second)
            )
        }

        onlinePlayers.forEach { player ->
            createFakePlayer(
                player, server, Location.deserialize(this.location), this.playerName, this.playerUUID, this.skin
            ) // Fake player of the player that has joined
        }

        // Add player to online players
        val serverData = RedisData.instance.getServerData() ?: return
        serverData.onlinePlayers.add(playerUUID)

        RedisData.instance.setServerData(serverData)
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

        if (playerUUID != viewer.uniqueId) {
            fakePlayer.setPos(location.x, location.y, location.z)
            fakePlayer.spawnIn(level)
            ClientAddPlayerPacket.sendPacket(viewer, fakePlayer)
        }

        // Add to local cacheData
        val cacheData = CacheData.instance
        cacheData.fakePlayers[playerUUID] = Pair(fakePlayer.id, fakePlayer)
    }
}

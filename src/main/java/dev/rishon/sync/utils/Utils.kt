package dev.rishon.sync.utils

import net.minecraft.server.level.ServerPlayer
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

object Utils {

    @JvmStatic
    fun getSkin(player: Player): Array<String> {
        val playerNMS = (player as CraftPlayer).handle
        val profile = playerNMS.bukkitEntity.profile
        val property = profile.properties["textures"].iterator().next()
        val texture: String = property.value
        val signature: String = property.signature.toString()
        return arrayOf(texture, signature)
    }

    @JvmStatic
    fun getFakePlayerSkin(serverPlayer: ServerPlayer): Array<String> {
        val profile = serverPlayer.gameProfile
        val property = profile.properties["textures"].iterator().next()
        val texture: String = property.value
        val signature: String = property.signature.toString()
        return arrayOf(texture, signature)
    }

}
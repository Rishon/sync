package dev.rishon.sync.listeners

import dev.rishon.sync.enums.Animations
import dev.rishon.sync.jedis.JedisManager
import dev.rishon.sync.jedis.packets.AnimationPacket
import dev.rishon.sync.jedis.packets.MovePacket
import dev.rishon.sync.jedis.packets.SneakPacket
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerAnimationType
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleSneakEvent

class AnimationEvent : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerAnimation(event: PlayerAnimationEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val animation = event.animationType

        val animationID = when (animation) {
            PlayerAnimationType.ARM_SWING -> {
                Animations.SWING_MAIN_ARM
            }

            PlayerAnimationType.OFF_ARM_SWING -> {
                Animations.SWIM_OFFHAND
            }
        }

        JedisManager.instance.sendPacket(AnimationPacket(uuid, animationID))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerSneak(event: PlayerToggleSneakEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val isSneaking = event.isSneaking
        JedisManager.instance.sendPacket(SneakPacket(uuid, isSneaking))
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val uuid = player.uniqueId
        val location = player.location.serialize()
        JedisManager.instance.sendPacket(MovePacket(uuid, location))
    }
}